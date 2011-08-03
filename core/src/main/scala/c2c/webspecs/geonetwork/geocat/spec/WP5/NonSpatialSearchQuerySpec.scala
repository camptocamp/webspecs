package c2c.webspecs
package geonetwork
package geocat
package spec.WP5

import csw._
import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{XmlValue, Response, IdValue, GetRequest}
import accumulating._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import csw._
import scala.xml.transform.BasicTransformer
import c2c.webspecs.geonetwork.geocat.spec.WP3.ProcessImportedMetadataSpec
import scala.xml.Node
import scala.xml.XML
import scala.xml.Elem
import org.specs2.execute.Result


@RunWith(classOf[JUnitRunner]) 
class NonSpatialSearchQuerySpec extends GeocatSpecification(UserProfiles.Editor) {  def is =
  "Non-spatial search queries".title ^
  "This specification tests how nonspatiel search queries"             					          													 ^ Step(setup)               ^
      "First import several metadata that are to be searched for" 								  													 ^ Step(importedMetadataId)  ^
      "When searching for a term that is in several metadata; the results having the term in the search language should appear first in the results"       ! currentLanguageFirst ^
      "Searching for ${NonSpatialSearchQuerySpec} in ${allText} should return ${all} imported md" 												            ! basicSearch ^
/*      "Searching for ${NonSpatialSearchQuerySpec} in ${title} should return ${all} imported md"    												            ! basicSearch  ^ 
      "Searching for ${NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md"    												        ! basicSearch  ^
      "Searching for ${NonXpatialSearchQuerySpec} in ${abstract} should return ${all} imported md"    												        ! basicSearch  ^
      "Searching for ${NonXXXXXXXSearchQuerySpec} in ${abstract} should return ${no} imported md"    												        ! basicSearch  ^
      "Searching for ${NonSpatialSearchQuerySpec-abstract} in ${abstract} should return ${all} imported md because the '-' is ignored"                          ! basicSearch  ^
      "Searching for ${NonSpatialSearchQuerySpec abstract} in ${abstract} should return ${all} imported md because the ' ' is ignored"                          ! basicSearch  ^
      "Searching for ${NonSpatialSearchQuerySpec_abstract} in ${abstract} should return ${all} imported md because the '_' is ignored"                          ! basicSearch  ^
      "Searching for ${NonSpatialSearchQuerySpec/abstract} in ${abstract} should return ${all} imported md because the '/' is ignored"                          ! basicSearch  ^
      "Searching for ${NonSpatialSearchQuerySpec,abstract} in ${abstract} should return ${all} imported md because the ',' is ignored"                          ! basicSearch  ^
      "Searching for ${NonSpatialSearchQuerySpec.abstract} in ${abstract} should return ${all} imported md because the '.' is ignored"                          ! basicSearch  ^
      "Searching for ${'NonSpatialSearchQuerySpec'} in ${abstract} should return ${all} imported md because the ''' is ignored"                             ! basicSearch  ^
      "Searching for ${\"NonSpatialSearchQuerySpec\"} in ${abstract} should return ${all} imported md because the '\"' is ignored"                          ! basicSearch  ^
      "Searching for ${\"NonSpatialSearchQuerySpec,abstract\"} in ${allText} should return ${no} imported md because the quotes forces entire term to be used"  ! basicSearch ^ 
      "Searching for ${'NonSpatialSearchQuerySpec,abstract'} in ${allText} should return ${no} imported md because the quotes forces entire term to be used"    ! basicSearch  ^
      "Searching for ${'EN_"+uuid+"'} in ${abstract} should return ${EN} imported md because it is the only MD with that string in abstract"	        ! basicSearch  ^
      "Searching for ${'FR_"+uuid+"'} in ${abstract} should return ${FR} imported md because it is the only MD with that string in abstract"	        ! basicSearch  ^
      "Searching for ${'DE_"+uuid+"'} in ${abstract} should return ${DE} imported md because it is the only MD with that string in abstract"	        ! basicSearch  ^
      "Searching for ${'FR_DE_"+uuid+"'} in ${abstract} should return ${DE,FR} imported md because they both have the string in the abstract"	    ! basicSearch  ^*/
                                                                                                  													   		 Step(tearDown)

  lazy val importedMetadataId = {
  def performImport(lang:String) = lang -> ImportMetadata.defaults(uuid, "/geocat/data/"+lang+"_Search_MD.iso19139.che.xml", false, classOf[ProcessImportedMetadataSpec])._2().value.id 
    val idsAndLangCodes = List( "FR", "DE", "EN", "**") map (performImport)
    idsAndLangCodes foreach {case (_,id) => registerNewMd(Id(id))} 
    
    Map(idsAndLangCodes :_*)
  }

  val basicSearch = (s: String) => {
    val (searchTerm, field, expectedCode) = extract3(s)
    val filter = PropertyIsEqualTo(field, searchTerm)

    val xmlResponse = CswGetRecordsRequest(filter.xml)().value.getXml
    
    val records = xmlResponse \\ "Record"

    val recordIds =  records \ "info" \ "id" map (_.text.trim)
    val importedIds = importedMetadataId.values.toSet
    val foundIdsMatchingImportedMd = recordIds filter{id => importedIds contains id} 
    
    expectedCode match {
      case "all" => foundIdsMatchingImportedMd must haveTheSameElementsAs (importedMetadataId.values)
      case "no" | "none" => foundIdsMatchingImportedMd must beEmpty
      case locales => 
        val localeSet = locales.split(",").toSet.map{(_:String).trim.toUpperCase}
        val ids = importedMetadataId.collect{case (key,id) if localeSet contains key => id }
        foundIdsMatchingImportedMd must haveTheSameElementsAs(ids)
    }
  }

  def currentLanguageFirst = {
    pending
  }
}