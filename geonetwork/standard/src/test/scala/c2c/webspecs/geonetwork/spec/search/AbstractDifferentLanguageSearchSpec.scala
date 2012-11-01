package c2c.webspecs.geonetwork
package spec.search

import org.specs2.specification.Step
import org.specs2.execute.Result

trait AbstractDifferentLanguageSearchSpec[SearchResult] {
  self: GeonetworkSpecification with AbstractSearchSpecification[SearchResult] =>
 def titleExtension:String
 def is =
    ("DifferentLanguageSearches"+titleExtension).title ^ Step(setup) ^
    "Import a metadata" ^ Step(importExtraMd(2, identifier=datestamp)) ^
    "Assert that the metadata is found when searching in ${eng}" ! search ^
    "Assert that the metadata is found when searching in ${fre}" ! search ^
    "Assert that the metadata is found when searching in ${ger}" ! search ^
    "Assert that the metadata is found when searching in ${ita}" ! search ^
                                                                   Step(tearDown)

  def search: String => Result
}