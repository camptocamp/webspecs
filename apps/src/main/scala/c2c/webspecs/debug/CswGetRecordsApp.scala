package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.login.LoginRequest

object CswGetRecordsApp extends WebspecsApp {

  val gcResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
    override def baseServer = "www.geocat.ch"
  }

  val jenkinsResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
    override def baseServer = "ec2-46-51-142-140.eu-west-1.compute.amazonaws.com"
  }
  
  val integrationResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
      override def baseServer = "tc-geocat1i.bgdi.admin.ch"
  }
  
    val sources = List(
        ("2cbf03e5-10d4-4a5e-b398-241289a97878",-65, "Geneve (SITG)"),
        ("d54fc6dd-1ff0-49da-81b6-2ba6c74b366f",677, "UNEP/GRID-Europe"),
        ("7ea582d4-9ddf-422e-b28f-29760a4c0147",4, "_geocat.ch direct partners"),
        ("adc6f7a2-0e6f-483e-aa9f-ce8ab0978d57",138, "envirocat"))

//    sources.foreach{s =>
//      val filter = PropertyIsEqualTo("_source", s._1)
//      println(s._3,total(filter, gcResolver), total(filter, jenkinsResolver), total(filter, integrationResolver), total(filter,uriResolver))
//    }

  
  
//  println(total(PropertyIsEqualTo("_source", "d54fc6dd-1ff0-49da-81b6-2ba6c74b366f"), uriResolver))
  println(total(PropertyIsEqualTo("_id", "330585"), uriResolver))
  //findMissingElements(PropertyIsEqualTo("_source", "7ea582d4-9ddf-422e-b28f-29760a4c0147"))

  def findMissingElements(filter: OgcFilter) = {
    def allIds(resolver:UriResolver):Set[Int] = {
        val numRecords = total(filter, resolver)
        val step = 100
        val results = for (i <- 1 to numRecords by step) yield {
          println("Requesting records "+i+" to "+(i+100))
          val req = CswGetRecordsRequest(
            filter.xml,
            outputSchema = OutputSchemas.Record,
            startPosition = i,
            maxRecords = step,
            resultType = ResultTypes.results)
    
          (req.execute()(executionContext, resolver).value.getXml \ "SearchResults" \ "Record" \ "info" \ "id" map (_.text.toInt))
        }
        results.flatten.toSet
    }


    println(allIds(jenkinsResolver) -- allIds(gcResolver))

  }
  def simpleCompare {
    val filter = PropertyIsEqualTo("_isHarvested", "n")

    val req = CswGetRecordsRequest(
      filter.xml,
      outputSchema = OutputSchemas.Record,
      maxRecords = 1,
      resultType = ResultTypes.hits)

    val gcResults = (req.execute()(executionContext, gcResolver).value.getXml \\ "@numberOfRecordsMatched").text.toInt
    val jenkinsResults = (req.execute()(executionContext, jenkinsResolver).value.getXml \\ "@numberOfRecordsMatched").text.toInt
    val diff = (gcResults - jenkinsResults)

    println(diff)
  }

  def compareByGroup {
    val groups = List(3, 7, 16, 17, 4, 18, 21, 19, 24, 20, 10, 6, 25, 26, 22, 27, 34, 36, 37, 38, 39, 23, 40, 41, 49, 42, 50, 51, 13, 52, 9, 53, 54, 55, 56, 8, 5)

    // LoginRequest("admin", "Hup9ieBe").execute()
    val diffs = for (group <- groups) yield {
      val filter = PropertyIsEqualTo("_groupOwner", group.toString)
      val req = CswGetRecordsRequest(
        filter.xml,
        outputSchema = OutputSchemas.Record,
        maxRecords = 1,
        resultType = ResultTypes.hits)

      val gcResults = (req.execute()(executionContext, gcResolver).value.getXml \\ "@numberOfRecordsMatched").text.toInt
      val jenkinsResults = (req.execute()(executionContext, jenkinsResolver).value.getXml \\ "@numberOfRecordsMatched").text.toInt
      val diff = (gcResults - jenkinsResults)
      (group, diff)
      //println(req.execute()(executionContext,uriResolver).value.getXml)
    }

    println(diffs.filterNot(_._2 == 0).mkString("\n"))
  }
  def compareBySource {
    val sources = List("7ea582d4-9ddf-422e-b28f-29760a4c0147", "c83d6356-e6d2-4611-8276-62dfba5d11e2", "7c703f99-083b-40f6-be06-dc65e6495b9b", "2cbf03e5-10d4-4a5e-b398-241289a97878", "3d0e7213-74b5-4de4-8d36-b23edd56886e", "558db0c4-2161-401b-b63a-ff7afe1d01ba", "1f4db83c-68b1-4749-899c-09c89f233d6c", "65eb4418-359a-4251-97ce-46492f60c8d2", "d54fc6dd-1ff0-49da-81b6-2ba6c74b366f", "7ea582d4-9ddf-422e-b28f-29760a4c0147", "adc6f7a2-0e6f-483e-aa9f-ce8ab0978d57")

    // LoginRequest("admin", "Hup9ieBe").execute()
    val diffs = for (source <- sources) yield {
      val filter = PropertyIsEqualTo("_source", source.toString)
      val req = CswGetRecordsRequest(
        filter.xml,
        outputSchema = OutputSchemas.Record,
        maxRecords = 1,
        resultType = ResultTypes.hits)

      val gcResults = total(filter, gcResolver)
      val jenkinsResults = total(filter, jenkinsResolver)
      val diff = (gcResults - jenkinsResults)
      (source, diff)
      //println(req.execute()(executionContext,uriResolver).value.getXml)
    }

    println(diffs.filterNot(_._2 == 0).mkString("\n"))
  }

  def total(filter: OgcFilter, resolver: UriResolver) = (CswGetRecordsRequest(
    filter.xml,
    outputSchema = OutputSchemas.Record,
    maxRecords = 1,
    resultType = ResultTypes.hits).execute()(executionContext, resolver).value.getXml \\ "@numberOfRecordsMatched").text.toInt
}