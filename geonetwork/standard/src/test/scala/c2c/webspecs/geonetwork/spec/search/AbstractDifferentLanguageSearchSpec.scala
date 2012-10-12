package c2c.webspecs.geonetwork
package spec.search

import org.specs2.specification.Step
import org.specs2.execute.Result

trait AbstractDifferentLanguageSearchSpec[SearchResult] {
  self: GeonetworkSpecification with AbstractSearchSpecification[SearchResult] =>
 def is =
    "Different language searches" ^ Step(setup) ^
    "Import a metadata" ^ Step(importExtraMd(2, identifier=datestamp)) ^
    "Assert that the metadata is found when searching in ${eng}" ! search ^
    "Assert that the metadata is found when searching in ${fra}" ! search ^
    "Assert that the metadata is found when searching in ${deu}" ! search ^
    "Assert that the metadata is found when searching in ${ita}" ! search ^
                                                                   Step(tearDown)

  def search: String => Result
}