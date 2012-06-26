package c2c.webspecs
package debug

import c2c.webspecs.geonetwork.csw._
import c2c.webspecs.login.LoginRequest
import java.util.zip.ZipFile
import c2c.webspecs.geonetwork.geocat.spec.WP7.ZipFileValueFactory

object CswGetRecordsApp extends WebspecsApp {

  val gcResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
    override def baseServer = "www.geocat.ch"
  }

  val jenkinsResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
    override def baseServer = "ec2-46-51-142-140.eu-west-1.compute.amazonaws.com"
  }
  
  val shadowResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
      override def baseServer = "ec2-176-34-163-138.eu-west-1.compute.amazonaws.com"
  }
  
  val integrationResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
      override def baseServer = "tc-geocat1i.bgdi.admin.ch"
  }
  
  val oldGeocatResolver = new BasicServerResolver("http", "geonetwork/srv/eng") {
      override def baseServer = "tc-geocat0i.bgdi.admin.ch:9999"
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
 // println(total(PropertyIsEqualTo("_id", "330585"), uriResolver))
  
  val groups = GetRequest("http://www.geocat.ch/geonetwork/srv/eng/geocat!").execute().value.getXml \ "gui" \ "groups" \ "record" map (g => (g \ "id").text -> (g \ "name"))
  val diffs = groups.map {
      case (id, name) =>
        println("comparing: "+name+" -> "+id)
        name ->  findMissingElements(PropertyIsEqualTo("_groupOwner", id))
    }
    
  diffs.foreach {
    case (name, values) =>
      println("------------------------------------------------------------")
      println(name+": "+values.size)
      println(values mkString "\n")
      println("------------------------------------------------------------")
  }

//  val uuids:List[OgcFilter] = diffs.toList.flatMap{e => e._2.map(uuid => PropertyIsEqualTo("_uuid",uuid)) }
//  
//  val filter = uuids.reduce(_ or _)
//  LoginRequest("admin","Hup9ieBe").execute()(executionContext, oldGeocatResolver)
//  
//  println(CswGetRecordsRequest(
//    filter.xml,
//    outputSchema = OutputSchemas.Record,
//    resultType = ResultTypes.results).execute()(executionContext, oldGeocatResolver).value.getXml)
//  
//  println(GetRequest("metadata.select", 'id -> 0, 'selected -> "add-all").execute()(executionContext, oldGeocatResolver).value.getXml)
//  
//  val request = new AbstractGetRequest[Any,ZipFile]("mef.export", ZipFileValueFactory, SP("format" -> "full"), SP("version" -> "2")) {}
//  val zipFile = request.execute()(executionContext, oldGeocatResolver).value
//  println(zipFile)
//    
  def findMissingElements(filter: OgcFilter): Set[String] = {
    def allIds(resolver:UriResolver):Set[String] = {
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
    
         val xml = req.execute()(executionContext, resolver).value.getXml
         xml.size
         (xml \ "SearchResults" \ "Record" \ "identifier" map (_.text))
        }
        results.flatten.toSet
    }

    if(total(filter, gcResolver) == total(filter, oldGeocatResolver)) {
      Set.empty
    } else {
        val gc = allIds(gcResolver)
        val old = allIds(oldGeocatResolver)
        
        println(gc.size)
        println(old.size)
        old -- gc
    }
  }
  def simpleCompare {
    val filter = PropertyIsEqualTo("_isHarvested", "n")

    val req = CswGetRecordsRequest(
      filter.xml,
      outputSchema = OutputSchemas.Record,
      maxRecords = 1,
      resultType = ResultTypes.hits)

    val gcResults = (req.execute()(executionContext, gcResolver).value.getXml \\ "@numberOfRecordsMatched").text.toInt
    val jenkinsResults = (req.execute()(executionContext, shadowResolver).value.getXml \\ "@numberOfRecordsMatched").text.toInt
    val diff = (jenkinsResults - gcResults)

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
      val jenkinsResults = (req.execute()(executionContext, shadowResolver).value.getXml \\ "@numberOfRecordsMatched").text.toInt
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
      val jenkinsResults = total(filter, shadowResolver)
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