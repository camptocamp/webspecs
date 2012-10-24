package c2c.webspecs
package debug
import c2c.webspecs.geonetwork.csw.CswGetRecordsRequest
import c2c.webspecs.geonetwork.csw.PropertyIsEqualTo
import c2c.webspecs.geonetwork.csw.ResultTypes
import c2c.webspecs.geonetwork.csw.OutputSchemas
import c2c.webspecs.geonetwork.GeonetworkSpecification



object KeywordSearchApp extends WebspecsApp {
  	val filter = PropertyIsEqualTo("keyword", "e-geo.ch Geoportal") 
    val result = CswGetRecordsRequest(filter.xml, 
        resultType=ResultTypes.results, 
        outputSchema=OutputSchemas.DublinCore).execute()
    val records = result.value.getXml \\ "Record" map{ p =>
	      (p \\ "title" text) +": "+(p \\ "subject" filter(s => s.text contains("géoportail e-geo.ch")) text)
	    }
  	println(records.size)
    println(records mkString ("\n"))
}