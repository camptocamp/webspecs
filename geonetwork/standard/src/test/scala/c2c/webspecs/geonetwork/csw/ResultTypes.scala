package c2c.webspecs
package geonetwork
package csw

object ResultTypes extends Enumeration {
  type ResultType = Value
  val hits,results = Value
  val resultsWithSummary = Value("results_with_summary")
}
