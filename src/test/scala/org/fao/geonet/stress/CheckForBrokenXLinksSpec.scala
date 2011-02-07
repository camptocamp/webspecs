package org.fao.geonet
package stress

import actors.{DaemonActor, Actor}

object CheckForBrokenXLinksSpec extends StressSpecification(2,24 * 60 * 60 * 1000) {

  "Geocat" should {

    "be able to handle multiple users searching" in {

      def ids(page: Int) = {
        CswGetRecordsRequest()
      }

      case object NextPage
      case object GoodMd
      case object Stop
      case class BadMd(i: String)
      val maxRecords = 100

      object Mediator extends Actor {
        private val records = (Config.adminLogin then CswGetRecordsRequest()) {
          case response: XmlResponse => withXml(response) {
            xml => (xml \\ "@numberofrecordsmatched" text).toInt
          }
        }
        private val pages = math.ceil(records.toDouble / maxRecords)
        private var page = 1
        var errors = List[String]()
        var successes = 0
        def act() = react {
            case NextPage =>
              println("recieved request for next page:"+page)
              reply(if(page > pages) None
                    else page)
              page += 1
              act()
            case BadMd(id) =>
              println("A bad metadata was found:"+id)
              errors :+= id
              act()
            case GoodMd =>
              successes += 1
              act()
            case Stop =>  ()
        }
      }

      Mediator.start()

      exec {
        val login = Config.adminLogin.assertPassed
        println("[CheckForBrokenXLinks]logged in, ask for next page")
        var nextTask = (Mediator !? NextPage)
        println("[CheckForBrokenXLinks]Got next page, next page is "+nextTask)
        while (nextTask != None) {
          println("[CheckForBrokenXLinks]Requesting ids")
          val ids = (login then CswGetRecordsRequest(startPosition = nextTask.toString.toInt,
                                                      maxRecords = maxRecords,
                                                      resultType = ResultTypes.results,
                                                      elementSetName = ElementSetNames.full)) {
            response => withXml(response) {
              xml =>
                xml \\ "Record" \\ "info" \ "id" map {
                  _.text.trim
                }
            }
          }
          ids foreach {
            id =>
             // println("[CheckForBrokenXLinks] requesting md id="+id)
              (login then CswGetByFileId(id, OutputSchemas.CheRecord)) {
                response => withXml(response) {
                  xml =>
                    if (xml \\ "ERROR" nonEmpty) Mediator ! BadMd(id)
                    else Mediator ! GoodMd
                }
              }
          }
          nextTask = (Mediator !? NextPage)
        }
      }

      if(Mediator.errors.nonEmpty) {
        println("[CheckForBrokenXLinks] errors found in md: ")
        println(Mediator.errors.sliding(20,20).map{_ mkString ","}.mkString("\t","\n\t",""))
      } else {
        println("[CheckForBrokenXLinks] No errors detected")
      }

      println("[Summary] Good: %s Bad: %s".format(Mediator.successes, Mediator.errors.size))
      Mediator ! Stop
    }
  }
}
