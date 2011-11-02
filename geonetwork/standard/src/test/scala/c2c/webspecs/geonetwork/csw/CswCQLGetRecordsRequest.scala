package c2c.webspecs
package geonetwork
package csw

import c2c.webspecs.AbstractGetRequest
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
      SP("REQUEST", "GetRecords"),
      SP("VERSION", "2.0.2"),
      SP("CONSTRAINT_LANGUAGE_VERSION", "1.1.0"),
      SP("CONSTRAINTLANGUAGE", "CQL_TEXT"),
      SP("constraint", cql),
      SP("resultType", resultType),
      SP("outputSchema", outputSchema),
      SP("maxRecords", maxRecords),
      SP("typeNames", "csw:Record"),
      SP("elementSetName", elementSetName))