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
import java.util.Date


@RunWith(classOf[JUnitRunner]) 
class NonSpatialSearchQuerySpec extends SearchSpecification {  def is =
  "Non-spatial search queries".title ^
  "This specification tests how non-spatial search queries"             					          													 				^ Step(setup)               ^
      "First import several metadata that are to be searched for" 								  													 				^ Step(importedMetadataId)  ^
      "When searching for a term that is in several metadata; the results having the term in the search language should appear first in the results"        		! currentLanguageFirst ^
      "Searching for ${"+time+"NonSpatialSearchQuerySpec} in ${AnyText} with a maxResults limit of 2 should return ${FR and XX} should be the hits" 				! basicSearch(2) ^
      "Searching for ${XX-"+uuid+"} in ${fileId} should return the ${XX} md"                                           				    ! basicSearch() ^
      "Searching for ${"+time+"NonSpatialSearchQuerySpec FRA} in ${AnyText} should return the ${FR and XX} should be the hits" 										! basicSearch ^
      "Searching for ${FRA "+time+"NonSpatialSearchQuerySpec} as seperate terms in ${AnyText} should return the ${FR and XX} as the hits" 							! basicSearch(split=Some(' ')) ^
      "Searching for ${"+time+"NonSpatialSearchQuerySpec} in ${AnyText} should return ${all} imported md" 												    	    ! basicSearch ^
      "Searching for ${"+time+"NonSpatialSearchQuerySpec} in ${title} should return ${all} imported md"    												  			! basicSearch  ^ 
      "Searching for ${"+time+"NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md"    												        ! basicSearch  ^
      "Searching for ${"+time+"NonXpatialSearchQuerySpec} in ${abstract} should return ${all} imported md when similarity is set to .8" 							! basicSearch(similarity = 0.8) ^
      "Searching for ${"+time+"NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md"    												        ! basicSearch  ^
      "Searching for ${NonXXXXXXXSearchQuerySpec} in ${abstract} should return ${no} imported md even when similarity is 0.8"    									! basicSearch(similarity = 0.8)  ^
      "Searching for ${"+time+"NonSpätialSearchQuerySpec} in ${abstract} should return ${all} imported md because accents are ignored"								! basicSearch  ^
      "Searching for ${"+time+"NonSpatialSèarchQuerySpec} in ${abstract} should return ${all} imported md because accents are ignored"								! basicSearch  ^
      "Searching for ${polluée} in ${title} should return ${DE and FR and EN} imported md because accents are ignored"												! basicSearch  ^
      "Searching for ${polluee} in ${title} should return ${DE and FR and EN} imported md because accents are ignored"												! basicSearch  ^
      "Searching for ${pollüee} in ${title} should return ${DE and FR and EN} imported md because accents are ignored"												! basicSearch  ^
      "Searching for ${le "+time+"NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in french"					! basicSearch(lang = "fra")  ^
      "Searching for ${the "+time+"NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in english"					! basicSearch(lang = "eng")  ^
      "Searching for ${einem "+time+"NonSpatialSearchQuerySpec} in ${abstract} should return ${all} imported md because le is a stop word in german"				! basicSearch(lang = "deu")  ^
      "Searching for ${"+time+"-hyphen} in ${abstract} should return ${FR} imported md because the '-' is ignored during indexing"   	                          	! basicSearch(split = Some('-'))  ^
      "Searching for ${"+time+"-hyphen} in ${AnyText} should return ${FR} imported md because the '-' is ignored during indexing"   	                          	! basicSearch(split = Some('-'))  ^
      "Searching for ${"+time+" hyphen} in ${abstract} should return ${FR} imported md because the '-' is ignored during indexing"   	                          	! basicSearch(split = Some('-'))  ^
      "Searching for ${"+time+" space} in ${abstract} should return ${FR} imported md because the ' ' is ignored during indexing"          	                		! basicSearch(split = Some(' '))  ^
      "Searching for ${"+time+"_underscore} in ${abstract} should return ${FR} imported md because the '_' is ignored during indexing"         	                 	! basicSearch(split = Some('_'))  ^
      "Searching for ${"+time+"_underscore} in ${AnyText} should return ${FR} imported md because the '_' is ignored during indexing"         	                 	! basicSearch(split = Some('_'))  ^
      "Searching for ${"+time+" underscore} in ${abstract} should return ${FR} imported md because the '_' is ignored during indexing"         	                 	! basicSearch(split = Some('_'))  ^
      "Searching for ${"+time+"/forwardSlash} in ${abstract} should return ${FR} imported md because the '/' is ignored during indexing"           	               	! basicSearch(split = Some('/'))  ^
      "Searching for ${"+time+"/forwardSlash} in ${AnyText} should return ${FR} imported md because the '/' is ignored during indexing"           	               	! basicSearch(split = Some('/'))  ^
      "Searching for ${"+time+" forwardSlash} in ${abstract} should return ${FR} imported md because the '/' is ignored during indexing"           	               	! basicSearch(split = Some('/'))  ^
      "Searching for ${"+time+"\\backSlash} in ${AnyText} should return ${FR} imported md because the '\' is ignored during indexing"                  	        	! basicSearch(split = Some('\\'))  ^
      "Searching for ${"+time+"\\backSlash} in ${abstract} should return ${FR} imported md because the '\' is ignored during indexing"                  	        ! basicSearch(split = Some('\\'))  ^
      "Searching for ${"+time+" backSlash} in ${abstract} should return ${FR} imported md because the '\' is ignored during indexing"                  	        	! basicSearch(split = Some('\\'))  ^
      "Searching for ${"+time+",comma} in ${AnyText} should return ${FR} imported md because the ',' is ignored during indexing"                          			! basicSearch(split = Some(','))  ^
      "Searching for ${"+time+",comma} in ${abstract} should return ${FR} imported md because the ',' is ignored during indexing"                          			! basicSearch(split = Some(','))  ^
      "Searching for ${"+time+" comma} in ${AnyText} should return ${FR} imported md because the ',' is ignored during indexing"                          			! basicSearch(split = Some(','))  ^
      "Searching for ${"+time+" comma} in ${abstract} should return ${FR} imported md because the ',' is ignored during indexing"                          			! basicSearch(split = Some(','))  ^
      "Searching for ${"+time+".point} in ${abstract} should return ${FR} imported md because the '.' is ignored during indexing"                          			! basicSearch(split = Some('.'))  ^
      "Searching for ${"+time+".point} in ${AnyText} should return ${FR} imported md because the '.' is ignored during indexing"                          			! basicSearch(split = Some('.'))  ^
      "Searching for ${"+time+" point} in ${abstract} should return ${FR} imported md because the '.' is ignored during indexing"                          			! basicSearch(split = Some('.'))  ^
      "Searching for ${"+time+"nonspatialsearchqueryspec} in ${abstract} should return ${all} imported md because the case is ignored"                          	! basicSearch  ^
      "Searching for ${"+time+"NONSPATIALSEARCHQUERYSPEC} in ${abstract} should return ${all} imported md because the case is ignored"                          	! basicSearch  ^
      "Searching for ${'"+time+"NonSpatialSearchQuerySpec'} in ${abstract} should return ${all} imported md because the ''' is ignored"                             ! basicSearch(split = Some('\''))  ^
      "Searching for ${\""+time+"NonSpatialSearchQuerySpec\"} in ${abstract} should return ${all} imported md because the '\"' is ignored"                          ! basicSearch(split = Some('"'))  ^
      "Searching for ${'ENx"+time+"'} in ${abstract} should return ${EN} imported md because it is the only MD with that string in abstract"	        			! basicSearch  ^
      "Searching for ${'FRx"+time+"'} in ${abstract} should return ${FR} imported md because it is the only MD with that string in abstract"	        			! basicSearch  ^
      "Searching for ${'DEx"+time+"'} in ${abstract} should return ${DE} imported md because it is the only MD with that string in abstract"	        			! basicSearch  ^
      "Searching for ${'FRxDEx"+time+"'} in ${abstract} should return ${XX and FR} imported md because they both have the string in the abstract"	    			! basicSearch  ^
      "Searching for ${dataset} in ${type} should return ${EN and DE and XX} imported md "	        																! basicSearch  ^
      "Searching for ${service} in ${type} should return ${FR} imported md "	        																			! basicSearch  ^
      "Searching for ${service-OGC:WMS} in ${type} should return ${FR} imported md "	        																	! basicSearch  ^
      "Searching for ${testGroup} in ${_groupOwner} should return ${all} imported md "	        																	! basicSearch  ^
                                                                                                  													   		 		  Step(tearDown)

  def basicSearch(implicit maxRecords:Int = 10000, similarity:Double = 1,lang:String = "fra", split:Option[Char]=None) = (s: String) => {
    val (searchTerm, field, expectedMetadata) = extract3(s)
    val allSearchTerms = split map {div => searchTerm.split(div)} getOrElse Array(searchTerm) collect {
      case "testGroup" => config.groupId
      case any if any.trim.length > 0 => any.trim
    }
    val similarityProperty = PropertyIsEqualTo("similarity", similarity.toString)
    
    val filter = (allSearchTerms foldLeft (similarityProperty:OgcFilter)) { (acc,next) => acc and PropertyIsEqualTo(field,next)}
    
    val xmlResponse = CswGetRecordsRequest(filter.xml, 
    									   resultType=ResultTypes.resultsWithSummary, 
    									   outputSchema = OutputSchemas.Record, 
    									   maxRecords = maxRecords,
    									   url = lang+"/csw")().value.getXml
    

    find(xmlResponse, expectedMetadata)
 }
  
  def currentLanguageFirst = {
    pending
  }
}