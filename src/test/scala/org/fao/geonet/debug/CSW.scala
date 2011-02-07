package org.fao.geonet
package debug

object CSW extends Application {

  //(Config.adminLogin then CswGetById("52def90a-4aad-4ee0-9b5c-bda113d6df2b", OutputSchemas.own)) { res => println(res.text)}
//  (Config.adminLogin then CswGetRecordsRequest()){response => println(response.xml.right)}
//  (Config.adminLogin then CswGetRecordsRequest(PropertyIsEqualTo("_isTemplate","y").xml)) {response => println(response.xml.right)}
 (Config.adminLogin then CswGetRecordsRequest(maxRecords=1,resultType = ResultTypes.results, elementSetName = ElementSetNames.full)) {
   response => println(response.xml.right)
 }
}
