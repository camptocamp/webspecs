package c2c.webspecs
package geonetwork
package csw

import ResultTypes._
import ElementSetNames._

case class CswCQLGetRecordsRequest(
	  cql: String,
	  resultType: ResultType = hits,
	  outputSchema: OutputSchemas.OutputSchema = OutputSchemas.IsoRecord,
	  startPosition: Int = 1,
	  maxRecords: Int = 50,
	  url: String = "csw",
	  elementSetName: ElementSetName = full)
  extends AbstractFormPostRequest(
      url,
      XmlValueFactory,
      SP("service", "CSW"),
      SP("request", "GetRecords"),
      SP("version", "2.0.2"),
      SP("constraint_language_version", "1.1.0"),
      SP("constraintlanguage", "CQL_TEXT"),
      SP("constraint", cql),
      SP("resultType", resultType),
      SP("outputSchema", outputSchema),
      SP("maxRecords", maxRecords),
      SP("typeNames", "csw:Record"),
      SP("elementSetName", elementSetName))