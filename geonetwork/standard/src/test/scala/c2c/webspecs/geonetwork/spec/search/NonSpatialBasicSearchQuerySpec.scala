package c2c.webspecs
package geonetwork
package spec.search

import org.specs2.specification.Step
import c2c.webspecs.geonetwork._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._

@RunWith(classOf[JUnitRunner])
class NonSpatialBasicSearchQuerySpec extends GeonetworkSpecification with SearchSpecification {
  def is =
    "Non-spatial search queries".title ^
      "This specification tests how non-spatial search queries" ^ Step(setup) ^
      "First import several metadata that are to be searched for" ^ Step(importedMetadataId) ^
          "When searching for a term that is in several metadata; the results having the term in the search language should appear first in the results" ! currentLanguageFirst ^
          "When searching for a term that is in several metadata; one should be able to sort by ${title}" ! sortBy ^
          "When searching for a term that is in several metadata; one should be able to sort by ${date}" ! sortBy ^
          "Searching for ${XX-" + uuid + "} in ${fileId} should return the ${XX} md" ! basicSearch() ^
          "Searching for ${" + time + "NonSpatialSearchQuerySpec FRA} in ${AnyText} should return the ${FR and XX} should be the hits" ! basicSearch ^
          "Searching for ${FRA " + time + "NonSpatialSearchQuerySpec} as seperate terms in ${AnyText} should return the ${FR and XX} as the hits" ! basicSearch(split = Some(' ')) ^
          "Searching for ${" + time + "NonSpatialSearchQuerySpec} in ${AnyText} should return ${all} imported md" ! basicSearch ^
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
          "Searching for ${le " + time + "NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in french" ! basicSearch(lang = "fra") ^
          "Searching for ${the " + time + "NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in english" ! basicSearch(lang = "eng") ^
          "Searching for ${einem " + time + "NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in german" ! basicSearch(lang = "deu") ^
          "Searching for ${" + time + "-hyphen} in ${abstract} should return ${FR} imported md because the '-' is ignored during indexing" ! basicSearch(split = Some('-')) ^
          "Searching for ${" + time + "-hyphen} in ${AnyText} should return ${FR} imported md because the '-' is ignored during indexing" ! basicSearch(split = Some('-')) ^
          "Searching for ${" + time + " hyphen} in ${abstract} should return ${FR} imported md because the '-' is ignored during indexing" ! basicSearch(split = Some('-')) ^
          "Searching for ${" + time + " space} in ${abstract} should return ${FR} imported md because the ' ' is ignored during indexing" ! basicSearch(split = Some(' ')) ^
          "Searching for ${" + time + "_underscore} in ${abstract} should return ${FR} imported md because the '_' is ignored during indexing" ! basicSearch(split = Some('_')) ^
          "Searching for ${" + time + "_underscore} in ${AnyText} should return ${FR} imported md because the '_' is ignored during indexing" ! basicSearch(split = Some('_')) ^
          "Searching for ${" + time + " underscore} in ${abstract} should return ${FR} imported md because the '_' is ignored during indexing" ! basicSearch(split = Some('_')) ^
          "Searching for ${" + time + "/forwardSlash} in ${abstract} should return ${FR} imported md because the '/' is ignored during indexing" ! basicSearch(split = Some('/')) ^
          "Searching for ${" + time + "/forwardSlash} in ${AnyText} should return ${FR} imported md because the '/' is ignored during indexing" ! basicSearch(split = Some('/')) ^
          "Searching for ${" + time + " forwardSlash} in ${abstract} should return ${FR} imported md because the '/' is ignored during indexing" ! basicSearch(split = Some('/')) ^
          "Searching for ${" + time + "\\backSlash} in ${AnyText} should return ${FR} imported md because the '\' is ignored during indexing" ! basicSearch(split = Some('\\')) ^
          "Searching for ${" + time + "\\backSlash} in ${abstract} should return ${FR} imported md because the '\' is ignored during indexing" ! basicSearch(split = Some('\\')) ^
          "Searching for ${" + time + " backSlash} in ${abstract} should return ${FR} imported md because the '\' is ignored during indexing" ! basicSearch(split = Some('\\')) ^
          "Searching for ${" + time + ",comma} in ${AnyText} should return ${FR} imported md because the ',' is ignored during indexing" ! basicSearch(split = Some(',')) ^
          "Searching for ${" + time + ",comma} in ${abstract} should return ${FR} imported md because the ',' is ignored during indexing" ! basicSearch(split = Some(',')) ^
          "Searching for ${" + time + " comma} in ${AnyText} should return ${FR} imported md because the ',' is ignored during indexing" ! basicSearch(split = Some(',')) ^
          "Searching for ${" + time + " comma} in ${abstract} should return ${FR} imported md because the ',' is ignored during indexing" ! basicSearch(split = Some(',')) ^
          "Searching for ${" + time + ".point} in ${abstract} should return ${FR} imported md because the '.' is ignored during indexing" ! basicSearch(split = Some('.')) ^
          "Searching for ${" + time + ".point} in ${AnyText} should return ${FR} imported md because the '.' is ignored during indexing" ! basicSearch(split = Some('.')) ^
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
          "An And Search with a single element that is a full search should return all terms" ! singleAnd ^
          Step(tearDown)

  def basicSearch(implicit maxRecords: Int = -1, similarity: Double = 1, lang: String = "fra", split: Option[Char] = None) = (s: String) => {
    val (searchTerm, field, expectedMetadata) = extract3(s)
    val allSearchTerms = split map { div => searchTerm.split(div) } getOrElse Array(searchTerm) collect {
      case "testGroup" => config.groupId
      case any if any.trim.length > 0 => any.trim
    }
    val similarityProperty = "similarity" -> similarity.toString

    val filter = similarityProperty :: (allSearchTerms.toList map (n => field -> n))

    val sortAndFilter = ('sortBy -> 'date) :: ('sortOrder -> 'reverse) :: filter

    implicit val resolver = new GeonetworkURIResolver(){
      override def locale = lang
    }

    val results = XmlSearch(if (maxRecords == -1) 100 else maxRecords, sortAndFilter : _*).execute().value

      
    find(results, expectedMetadata, maxRecords)
  }

  def sortBy = (s: String) => {
    val field = extract1(s)
    val filter = PropertyIsEqualTo("abstract", time+"NonSpatialSearchQuerySpec")
    val sortedDescRequest = XmlSearch(100, "abstract" -> (time+"NonSpatialSearchQuerySpec")).sortBy(field, false)
    val sortedAscRequest = sortedDescRequest.sortBy(field, true)

    val sortedAscResults = findCodesFromResults(sortedAscRequest.execute().value)
    val sortedDescResults = findCodesFromResults(sortedDescRequest.execute().value)

    (sortedDescResults must contain("DE", "EN", "FR", "XX")) and
      (sortedDescResults must contain("XX", "FR", "EN", "DE"))
  }
  def currentLanguageFirst = {
    
    implicit val resolver = new GeonetworkURIResolver(){
      var lang = "fra"
      override def locale = lang
    }

    val request = XmlSearch(100, "abstract" -> ("FRxDEx" + time))
    val frResults = findCodesFromResults(request.execute().value)
    resolver.lang = "deu"
    val deResults = findCodesFromResults(request.execute().value)

    (frResults must contain("FR", "XX").only.inOrder) and
      (deResults must contain("XX", "FR").only.inOrder)
  }
  def singleAnd = {
    val response = XmlSearch(30).sortBy("date", false).execute().value

    find(response, "all")
  }
}