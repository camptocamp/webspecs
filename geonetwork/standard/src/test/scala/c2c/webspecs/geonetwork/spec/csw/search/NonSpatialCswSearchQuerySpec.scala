package c2c.webspecs
package geonetwork
package spec.csw.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import c2c.webspecs.geonetwork.spec.search.AbstractNonSpatialSearchQuerySpec

@RunWith(classOf[JUnitRunner])
class NonSpatialCswSearchQuerySpec extends GeonetworkSpecification with SearchSpecification with AbstractNonSpatialSearchQuerySpec[XmlValue]{
  override def anyFieldName = "AnyText"

  override def searchRequest(maxRecords: Int, sortByField: Option[(String, Boolean)], properties: (Double, String, String)*) = {
    val propertiesIsEqualToList:Seq[OgcFilter] = properties.flatMap(p => List(PropertyIsEqualTo("similarity", p._1.toString), PropertyIsEqualTo(p._2, p._3)))
    val filter = if (properties.isEmpty) {
      <ogc:PropertyIsLike wildCard="*" singleChar="." escape="!">
            <ogc:PropertyName>anyText</ogc:PropertyName>
            <ogc:Literal>*</ogc:Literal>
          </ogc:PropertyIsLike>
    } else {
      propertiesIsEqualToList.reduce(_ and _).xml
    }
    val sortBy = sortByField.toList.flatMap{p => List(SortBy(p._1,p._2))}
    
    CswGetRecordsRequest(filter,
      resultType = ResultTypes.resultsWithSummary,
      outputSchema = OutputSchemas.Record,
      maxRecords = if (maxRecords == -1) 100 else maxRecords,
      sortBy = sortBy)
  }
  
  override def extraTests = "An And Search with a single element that is a full search should return all terms" ! singleAnd
  
  def singleAnd = {
    val filter =
        <ogc:And>
          <ogc:PropertyIsLike wildCard="*" singleChar="." escape="!">
            <ogc:PropertyName>anyText</ogc:PropertyName>
            <ogc:Literal>*</ogc:Literal>
          </ogc:PropertyIsLike>
        </ogc:And>

    val xmlResponse = CswGetRecordsRequest(filter,
      resultType = ResultTypes.resultsWithSummary,
      maxRecords = 30,
      sortBy = List(SortBy("date", false)),
      outputSchema = OutputSchemas.Record).execute().value

    find(xmlResponse, "all")
  }
}