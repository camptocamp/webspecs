package c2c.webspecs
package geonetwork
package geocat
package spec.WP2


import c2c.webspecs.geonetwork.GeonetworkSpecification
import org.parboiled.support.Var

import c2c.webspecs.geonetwork.OutputSchemas._
/**
 * Created by IntelliJ IDEA.
 * User: pmauduit
 * Date: 12/07/11
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */

class CompareGeocat1Metadata extends GeonetworkSpecification { def is =

  "This spec will compare GeoCat1 metadata to the migrated ones" ! doDiff

  def doDiff = {
    val prodUrl = "http://www.geocat.ch/geonetwork/srv/fra/"

    val geocatLogin = FormPostRequest(prodUrl + "login",
                                      "username" -> "admin",
                                      "password" -> Properties("geocat.admin.password"))
    ExecutionContext.withDefault {
       prodContext =>
         implicit val c = prodContext
        geocatLogin(Nil)

         val filter = PropertyIsEqualTo("_isHarvested", "n")
        val xmlRet = XmlPostRequest(prodUrl +  "csw", CswXmlUtil.getRecordsXml(filter=filter.xml, maxRecords=10000))(Nil).value.getXml
        println (xmlRet)

       val numRecords = (xmlRet \\ "SearchResults" \@ "numberOfRecordsMatched").head.toInt

       val pageSize = 2

       for (index <- 1 to numRecords by pageSize; if index < 4) {
         val xmlCurrent = XmlPostRequest(prodUrl +  "csw",
                                         CswXmlUtil.getRecordsXml(startPosition=index,
                                                                  filter=filter.xml,
                                                                  resultType=ResultTypes.resultsWithSummary,
                                                                  outputSchema=new OutputSchema("own"){},
                                                                  maxRecords=pageSize))(Nil).value.getXml
          //println(xmlCurrent)
         val mds = (xmlCurrent  \\ "MD_Metadata") ++ (xmlCurrent \\ "CHE_MD_Metadata")
         assert(mdIds.size == pageSize || mdIds.size == numRecords % pageSize)

       }


      success
    }

  }
  def compareToNewVersion()
      val mdIds = (xmlCurrent \\ "info" \ "id").map(_.text.toInt)
     println(mdIds mkString "\n")

}