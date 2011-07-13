package c2c.webspecs
package geonetwork
package stress

import csw._

class ConcurrentSearchSpec extends StressSpecification(40) { def is =

  "This specification performs multiple concurrent searches" ^
    "Do search for all then raster then sort by popularity" ! exec

  def exec = {
    val MainPageSearch =
      CswGetRecordsRequest(
        <ogc:PropertyIsLike wildCard="*" singleChar="." escape="!"><ogc:PropertyName>anyText</ogc:PropertyName><ogc:Literal>*</ogc:Literal></ogc:PropertyIsLike>,
        ResultTypes.resultsWithSummary,
        maxRecords = 10)
    val RasterTextSearch =
      CswGetRecordsRequest(
        <ogc:PropertyIsEqualTo><ogc:PropertyName>AnyText</ogc:PropertyName><ogc:Literal>raster</ogc:Literal></ogc:PropertyIsEqualTo>,
        ResultTypes.resultsWithSummary,
        maxRecords = 10)
    val RasterTextSearchSortByPopularity =
      CswGetRecordsRequest(
        <ogc:PropertyIsEqualTo><ogc:PropertyName>AnyText</ogc:PropertyName><ogc:Literal>raster</ogc:Literal></ogc:PropertyIsEqualTo>,
        ResultTypes.resultsWithSummary,
        maxRecords = 10,
        sortBy = Some(SortBy("relevance", false)))

    run(UserLogin then MainPageSearch then RasterTextSearch then RasterTextSearchSortByPopularity)
  }

}
