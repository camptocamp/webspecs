package c2c.webspecs
package geonetwork
package geocat
package spec.WP2

import csw._
import OutputSchemas._
import xml.NodeSeq
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.MustThrownMatchers
import scala.xml.transform.BasicTransformer
import scala.xml.Node

@RunWith(classOf[JUnitRunner])
class CompareGeocat1Metadata extends GeonetworkSpecification with MustThrownMatchers {
  def is =

    "This spec will compare GeoCat1 metadata to the migrated ones" ! doDiff

  def doDiff = {
    val prodUrl = "http://www.geocat.ch/geonetwork/srv/fra/"

    val geocatLogin = FormPostRequest(prodUrl + "login",
      "username" -> "admin",
      "password" -> Properties("geocat.admin.password"))
    ExecutionContext.withDefault { prodContext =>
      implicit val c = prodContext
      geocatLogin(Nil)

      val filter = PropertyIsEqualTo("_isHarvested", "n")
      val xmlRet = XmlPostRequest(prodUrl + "csw", CswXmlUtil.getRecordsXml(filter = filter.xml, maxRecords = 10000))(Nil).value.getXml
      println(xmlRet)

      val numRecords = (xmlRet \\ "SearchResults" \@ "numberOfRecordsMatched").head.toInt

      val pageSize = 2

      for (index <- 1 to numRecords by pageSize; if index < 4) {
        val xmlCurrent = XmlPostRequest(prodUrl + "csw",
          CswXmlUtil.getRecordsXml(startPosition = index,
            filter = filter.xml,
            resultType = ResultTypes.resultsWithSummary,
            outputSchema = CheRecord,
            maxRecords = pageSize))(Nil).value.getXml
        //println(xmlCurrent)
        val mds = (xmlCurrent \\ "MD_Metadata") ++ (xmlCurrent \\ "CHE_MD_Metadata")
        assert(mds.size == pageSize || mds.size == numRecords % pageSize)
        mds.foreach(compareToNewVersion)
      }
      success
    }

  }

  def compareToNewVersion(md: Node) = {
    val mdIds = (md \\ "info" \ "id").map(_.text)
    println(mdIds mkString "\n")

    assert(mdIds.size == 1)

    val mdFromGeocatWithoutInfoInfoXml = RemoveInfo(md)
    
    GetRawMetadataXml(Id(mdIds.head)).value.getXml must beEqualToIgnoringSpace(mdFromGeocatWithoutInfoInfoXml)
  }
  
  object RemoveInfo extends BasicTransformer{
    override def transform(n:Node) = {
      if(n.namespace == "geonet") {
        Nil
      } else {
        n
      }
    }
  }
}