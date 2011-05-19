package c2c.webspecs
package geonetwork
package stress

class ConcurrentSearchSpec extends StressSpecification(40) {def spec =

  "This specification performs multiple concurrent searches"      ^
    "example 1"                                                   ! success

/*  "Geocat" should {

    "be able to handle multiple users searching" in {
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
          sortBy = Some(SortBy("relevance",false))
        )

      run(UserLogin then MainPageSearch then RasterTextSearch then RasterTextSearchSortByPopularity)
    }
  }   */
}
