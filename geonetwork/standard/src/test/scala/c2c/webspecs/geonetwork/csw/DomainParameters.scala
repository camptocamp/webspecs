package c2c.webspecs
package geonetwork
package csw

object DomainParameters {
  abstract class DomainParameter(val name:String) extends DomainParam
  case object DescribeRecordOutputFormat extends DomainParameter("DescribeRecord.outputFormat")
  case object GetRecordsResultType extends DomainParameter("GetRecords.resultType")
}
