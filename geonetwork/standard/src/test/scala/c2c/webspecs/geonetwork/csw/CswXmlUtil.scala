package c2c.webspecs
package geonetwork
package csw

import ResultTypes._
import ElementSetNames._
import xml.NodeSeq

object CswXmlUtil {
  def getByIdXml(fileIds: Seq[String], resultType: ResultType, outputSchema: OutputSchemas.OutputSchema) =
    <csw:GetRecordById xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW" version="2.0.2" resultType={ resultType.toString } outputSchema={ outputSchema.toString }>
      {
        fileIds.map(id => <csw:Id>{ id }</csw:Id>)
      }
    </csw:GetRecordById>

  def getRecordsXml(filter: NodeSeq = Nil,
    resultType: ResultType = hits,
    outputSchema: OutputSchemas.OutputSchema = OutputSchemas.IsoRecord,
    startPosition: Int = 1,
    maxRecords: Int = 50,
    elementSetName: ElementSetName = full,
    typeNames: List[String] = List("csw:Record"),
    sortBy: Seq[SortBy] = Nil) = {
    <csw:GetRecords xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW" version="2.0.2" resultType={ resultType.toString } startPosition={ startPosition.toString } maxRecords={ maxRecords.toString } outputSchema={ outputSchema.toString }>
      <csw:Query typeNames={typeNames.mkString(",")}>
        <csw:ElementSetName>{ elementSetName }</csw:ElementSetName>
        {
          if (sortBy.nonEmpty) {
            <ogc:SortBy xmlns:ogc="http://www.opengis.net/ogc">
              {
                sortBy.map { sortBy =>
                  <ogc:SortProperty>
                    <ogc:PropertyName>{ sortBy.key }</ogc:PropertyName>
                    <ogc:SortOrder>{ if (sortBy.asc) 'A' else 'D' }</ogc:SortOrder>
                  </ogc:SortProperty>
                }
              }
            </ogc:SortBy>
          }
        }
        {
          if (filter.nonEmpty) {
            <csw:Constraint version="1.0.0">
              <ogc:Filter xmlns:ogc="http://www.opengis.net/ogc">
                { filter }
              </ogc:Filter>
            </csw:Constraint>
          } else Nil
        }
      </csw:Query>
    </csw:GetRecords>
  }
}


