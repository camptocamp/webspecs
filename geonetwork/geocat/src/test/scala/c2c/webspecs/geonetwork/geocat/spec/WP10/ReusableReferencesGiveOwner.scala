package c2c.webspecs
package geonetwork
package geocat
package spec.WP10

import shared._
import org.specs2.specification.Step
import scala.xml.Elem
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ReusableReferencesGiveOwner extends GeocatSpecification { def is =
  Step(setup) ^ Step(importExampleMetadata) ^ Step(config.adminLogin.execute()) ^ 
  partialSpec("keyword") ^ partialSpec("format") ^ Step(tearDown)
  
  def partialSpec(soType: String) = {
	val accessor = () => soType match {
	  case "keyword" => keywordReferences
	  case "format" => formatReferences
	}
    "When finding the references of a Shared ${"+soType+"} object" ^
    "the result should have the metadata Id" ! hasId(accessor) ^
    "the result should have the title" ! hasTitle(accessor) ^
    "and should have the owner name" ! hasName(accessor) ^
    "and should have the owner email" ! hasEmail(accessor) ^ endp
  }
  
  lazy val importExampleMetadata = {
    importMd(1,"/geocat/data/comprehensive-iso19139che.xml", datestamp)
  }
  
  def listRefs(baseRequest: AbstractGetRequest[Any, SharedObjectList]) = baseRequest.execute().value.find(_.description contains datestamp).toList.flatMap{elem => 
    ListReferencingMetadata(elem.id, elem.objType).execute().value
  }
  lazy val keywordReferences = listRefs(ListNonValidatedKeywords)
  lazy val formatReferences = listRefs(ListNonValidatedFormats)

  def hasId (accessor: () => List[ReferencingMetadata]) = {
    val refIds = accessor().map(_.mdId)
    
    refIds must not(beEmpty)
  }
  def hasTitle (accessor: () => List[ReferencingMetadata]) = {
    val refTitle = accessor().map(_.title)
    
    (refTitle must not(beEmpty)) and (refTitle must (beMatching(".*\\S+.*")).forall)
  }
  def hasName (accessor: () => List[ReferencingMetadata]) = {
    val refNames = accessor().map(_.ownerName)
    
    (refNames must not(beEmpty)) and (refNames must (contain("{automated_test_metadata}")).forall)
  }
  def hasEmail (accessor: () => List[ReferencingMetadata]) = {
    val refEmails = accessor().map(_.email)
    
    (refEmails must not(beEmpty)) and (refEmails must (contain("@")).forall)
  }
  
  class Reference(mdId: String, title: String, name: String, email: String)
}