package org.fao.geonet
package debug

object CSW extends Application {

  (Config.adminLogin then CswGetByFileId("c4affa4b-c670-4d42-9fde-36379504342a", OutputSchemas.IsoRecord)) { res => println(res.text)}
//  (Config.adminLogin then CswGetRecordsRequest()){response => println(response.xml.right)}
//  (Config.adminLogin then CswGetRecordsRequest(PropertyIsEqualTo("_isTemplate","y").xml)) {response => println(response.xml.right)}
 (Config.adminLogin then CswGetRecordsRequest(maxRecords=1,resultType = ResultTypes.resultsWithSummary, elementSetName = ElementSetNames.full, outputSchema = OutputSchemas.IsoRecord)) {
   response => println(response.xml.right)
 }
}
