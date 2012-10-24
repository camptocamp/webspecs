package c2c.webspecs
package geonetwork
package spec.search
import org.specs2.specification.Step
import org.specs2.specification.Fragments

trait AbstractNonSpatialSearchQuerySpec[SearchResult] extends SearchSettingsSpecification {
  self: GeonetworkSpecification with AbstractSearchSpecification[SearchResult] =>
    
  def titleExtension:String
  def is =
    ("NonSpatialSearchQuery"+titleExtension).title ^
      "This specification tests how non-spatial search queries" ^ Step(setup) ^ Step(setSearchSetting(only="prefer_docLocale", sorted = false, ignored = false)) ^
      "First import several metadata that are to be searched for" ^ Step(importedMetadataId) ^
          "When searching for a term that is in several metadata; the results having the term in the search language should appear first in the results" ! currentLanguageFirst ^
          "When searching for a term that is in several metadata; one should be able to sort by ${title}" ! sortBy ^
          "When searching for a term that is in several metadata; one should be able to sort by ${date}" ! sortBy ^
          "Searching for ${XX-" + uuid + "} in ${fileId} should return the ${XX} md" ! basicSearch() ^
          "Searching for ${" + time + "NonSpatialSearchQuerySpec FRA} in ${"+anyFieldName+"} should return the ${FR and XX} should be the hits" ! basicSearch ^
          "Searching for ${FRA " + time + "NonSpatialSearchQuerySpec} as seperate terms in ${"+anyFieldName+"} should return the ${FR and XX} as the hits" ! basicSearch(split = Some(' ')) ^
          "Searching for ${" + time + "NonSpatialSearchQuerySpec} in ${"+anyFieldName+"} should return ${all} imported md" ! basicSearch ^
          "Searching for ${" + time + "NonSpatialSearchQuerySpec} in ${title} should return ${all} imported md" ! basicSearch ^
          "Searching for ${" + time + "NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md" ! basicSearch ^
          "Searching for ${" + time + "NonXpatialSearchQuerySpec} in ${abstract} should return ${all} imported md when similarity is set to .8" ! basicSearch(similarity = 0.8) ^
          "Searching for ${" + time + "NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md" ! basicSearch ^
          "Searching for ${NonXXXXXXXSearchQuerySpec} in ${abstract} should return ${no} imported md even when similarity is 0.8" ! basicSearch(similarity = 0.8) ^
          "Searching for ${" + time + "NonSpätialSearchQuerySpec} in ${abstract} should return ${all} imported md because accents are ignored" ! basicSearch ^
          "Searching for ${" + time + "NonSpatialSèarchQuerySpec} in ${abstract} should return ${all} imported md because accents are ignored" ! basicSearch ^
          "Searching for ${polluée} in ${title} should return ${DE and FR and EN} imported md because accents are ignored" ! basicSearch ^
          "Searching for ${polluee} in ${title} should return ${DE and FR and EN} imported md because accents are ignored" ! basicSearch ^
          "Searching for ${pollüee} in ${title} should return ${DE and FR and EN} imported md because accents are ignored" ! basicSearch ^
          "Searching for ${le " + time + "NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in french" ! basicSearch(lang = "fre") ^
          "Searching for ${the " + time + "NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in english" ! basicSearch(lang = "eng") ^
          "Searching for ${einem " + time + "NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in german" ! basicSearch(lang = "ger") ^
          "Searching for ${" + time + " space} in ${abstract} should return ${FR} imported md because the ' ' is ignored during indexing" ! basicSearch(split = Some(' ')) ^
          "Searching for ${" + time + "\\backSlash} in ${"+anyFieldName+"} should return ${FR} imported md because the '\' is ignored during indexing" ! basicSearch(split = Some('\\')) ^
          "Searching for ${" + time + "\\backSlash} in ${abstract} should return ${FR} imported md because the '\' is ignored during indexing" ! basicSearch(split = Some('\\')) ^
          "Searching for ${" + time + " backSlash} in ${abstract} should return ${FR} imported md because the '\' is ignored during indexing" ! basicSearch(split = Some('\\')) ^
          "Searching for ${" + time + ",comma} in ${"+anyFieldName+"} should return ${FR} imported md because the ',' is ignored during indexing" ! basicSearch(split = Some(',')) ^
          "Searching for ${" + time + ",comma} in ${abstract} should return ${FR} imported md because the ',' is ignored during indexing" ! basicSearch(split = Some(',')) ^
          "Searching for ${" + time + " comma} in ${"+anyFieldName+"} should return ${FR} imported md because the ',' is ignored during indexing" ! basicSearch(split = Some(',')) ^
          "Searching for ${" + time + " comma} in ${abstract} should return ${FR} imported md because the ',' is ignored during indexing" ! basicSearch(split = Some(',')) ^
          "Searching for ${" + time + ".point} in ${abstract} should return ${FR} imported md because the '.' is ignored during indexing" ! basicSearch(split = Some('.')) ^
          "Searching for ${" + time + ".point} in ${"+anyFieldName+"} should return ${FR} imported md because the '.' is ignored during indexing" ! basicSearch(split = Some('.')) ^
          "Searching for ${" + time + " point} in ${abstract} should return ${FR} imported md because the '.' is ignored during indexing" ! basicSearch(split = Some('.')) ^
          "Searching for ${" + time + "nonspatialsearchqueryspec} in ${abstract} should return ${all} imported md because the case is ignored" ! basicSearch ^
          "Searching for ${" + time + "NONSPATIALSEARCHQUERYSPEC} in ${abstract} should return ${all} imported md because the case is ignored" ! basicSearch ^
          "Searching for ${'" + time + "NonSpatialSearchQuerySpec'} in ${abstract} should return ${all} imported md because the ''' is ignored" ! basicSearch(split = Some('\'')) ^
          "Searching for ${\"" + time + "NonSpatialSearchQuerySpec\"} in ${abstract} should return ${all} imported md because the '\"' is ignored" ! basicSearch(split = Some('"')) ^
          "Searching for ${'ENx" + time + "'} in ${abstract} should return ${EN} imported md because it is the only MD with that string in abstract" ! basicSearch ^
          "Searching for ${'FRx" + time + "'} in ${abstract} should return ${FR} imported md because it is the only MD with that string in abstract" ! basicSearch ^
          "Searching for ${'DEx" + time + "'} in ${abstract} should return ${DE} imported md because it is the only MD with that string in abstract" ! basicSearch ^
          "Searching for ${'FRxDEx" + time + "'} in ${abstract} should return ${XX and FR} imported md because they both have the string in the abstract" ! basicSearch ^
          "Searching for ${dataset} in ${type} should return ${EN and DE and XX} imported md " ! basicSearch ^
          "Searching for ${service} in ${type} should return ${FR} imported md " ! basicSearch ^
          "Searching for ${service-OGC:WMS} in ${type} should return ${FR} imported md " ! basicSearch ^
          "Searching for ${testGroup} in ${_groupOwner} should return ${all} imported md " ! basicSearch ^
          extraTests ^
          Step(resetSearchSetting) ^ Step(tearDown)

  def anyFieldName = "any"

  /**
   * @param maxRecords max results to return
   * @param sortyByField (field to sortby, if true then sort low to high)
   * @param properties (similarity, fieldname, value)
   */
  def searchRequest(maxRecords:Int, sortyByField: Option[(String,Boolean)], properties:(Double, String, String)*):Request[Any, SearchResult]
  
  def extraTests:Fragments = "no extra tests" ^ endp

  protected def basicSearch(implicit maxRecords: Int = -1, similarity: Double = 1, lang: String = "fre", split: Option[Char] = None) = (s: String) => {
    val (searchTerm, field, expectedMetadata) = extract3(s)
    val allSearchTerms = split map { div => searchTerm.split(div) } getOrElse Array(searchTerm) collect {
      case "testGroup" => config.groupId
      case any if any.trim.length > 0 => any.trim
    }
    val properties = allSearchTerms.toList map (n => (similarity, field, n))

    val resolver = new GeonetworkURIResolver(){
      override def locale = lang
    }

    val results = searchRequest(if (maxRecords == -1) 100 else maxRecords, Some("date", false), properties:_*).execute()(context, resolver).value
    
    find(results, expectedMetadata, maxRecords)
  }

  protected def sortBy = (s: String) => {
    val field = extract1(s)
    val sortedDescRequest = searchRequest(100, Some(field -> false), (1, "abstract",(time+"NonSpatialSearchQuerySpec")))
    val sortedAscRequest = searchRequest(100, Some(field -> true), (1, "abstract",(time+"NonSpatialSearchQuerySpec")))

    val sortedAscResults = findCodesFromResults(sortedAscRequest.execute().value)
    val sortedDescResults = findCodesFromResults(sortedDescRequest.execute().value)

    (sortedDescResults must contain("DE", "EN", "FR", "XX")) and
      (sortedDescResults must contain("XX", "FR", "EN", "DE"))
  }
  protected def currentLanguageFirst = {
    
    implicit val resolver = new GeonetworkURIResolver(){
      var lang = "fre"
      override def locale = lang
    }

    val request = searchRequest(100, None, (1, "abstract", ("FRxDEx" + time)))
    val frResults = findCodesFromResults(request.execute()(context, resolver).value)
    resolver.lang = "ger"
    val deResults = findCodesFromResults(request.execute()(context, resolver).value)

    (frResults must contain("FR", "XX").only.inOrder) and
      (deResults must contain("XX", "FR").only.inOrder)
  }
}