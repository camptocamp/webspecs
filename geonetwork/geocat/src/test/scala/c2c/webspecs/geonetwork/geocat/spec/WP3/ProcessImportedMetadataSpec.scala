package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import edit._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.NodeSeq
import org.specs2.execute.Result
import c2c.webspecs.geonetwork.edit.UpdateMetadata

@RunWith(classOf[JUnitRunner]) 
class ProcessImportedMetadataSpec extends GeocatSpecification { def is =
  "Shared Object Processing of Imported Metadata".title ^ Step(setup) ^
  "This specification tests how imported metadata are processed for shared objects"             ^
  "When a data metadata is imported"                                                            ^ importFragments(importedDataMetadata) ^ p ^
  "When a service metadata is imported"                                                         ^ importFragments(importedServiceMetadata) ^
  Step(tearDown)

  def importFragments(importRequest: => Response[TestData]) = {
       Step(importRequest) ^
      "The import must complete successfully"                                                   ! importHas200Response(importRequest) ^
      "All ${extent} must have ${several} xlink:href attributes"                                ! xlinked(importRequest) ^
      "All ${contact} must have ${several}  xlink:href attributes"                              ! xlinked(importRequest) ^
      "All ${contact} must have facsimile element"                                              ! facsimile(importRequest) ^
      "All ${descriptiveKeywords} must have ${several} xlink:href attributes"                   ! xlinked(importRequest) ^
      "All ${citedResponsibleParty} must have ${several} xlink:href attributes"                 ! xlinked(importRequest) ^
      "All ${parentResponsibleParty} must have ${several} xlink:href attributes"                ! xlinked(importRequest) ^
      "All ${pointOfContact} must have ${several} xlink:href attributes"                        ! xlinked(importRequest) ^
      "All ${resourceFormat} must have ${several} xlink:href attributes"                        ! xlinked(importRequest) ^
      "All ${userContactInfo} must have ${several} xlink:href attributes"                       ! xlinked(importRequest) ^
      "All ${distributionFormat} must have ${several} xlink:href attributes"                    ! xlinked(importRequest) ^
      "All ${distributorContact} must have ${several} xlink:href attributes"                    ! xlinked(importRequest) ^
      "All ${sourceExtent} must have ${1} xlink:href attributes"                                ! xlinked(importRequest) ^
      "All ${spatialExtent} must have ${2} xlink:href attributes"                               ! xlinked(importRequest) ^ p ^
      "Updating a shared contact and its parent contact through the geonetwork edit API"		^ updateContact(importRequest).toGiven ^
      	"Should be present in the metadata the next time the metadata is accessed"				^ newContact.toThen ^ 
      	("The update to the parent contact (which is a sub element of other updated " +
      	"shared contact must also be updated correctly") 										^ updatedParentPosition.toThen ^ 
      																							  end ^ Step(deleteMd(importRequest))
	}
                       
   case class TestData(id:IdValue, mdWithoutXLinks:NodeSeq, mdWithXLinks:NodeSeq)

   def deleteMd(testData: => Response[TestData]) = {
     val deleteResponse = DeleteMetadata.execute(testData.value.id)
     assert(deleteResponse.basicValue.responseCode == 200, "Failed to delete imported metadata")
   }
   def doImport(fileName:String):Response[TestData] = {
     val (_,importRequest) = ImportMetadata.defaults(uuid, "/geocat/data/"+fileName,false, classOf[ProcessImportedMetadataSpec])

     val importResponse = (UserLogin then importRequest).execute(None)
     val id = importResponse.value
     val mdWithXLinks =  GetEditingMetadataXml.execute(id).value.getXml
     val mdWithoutXLinks = GetRawMetadataXml.execute(id).value.getXml

     importResponse.map(mv => TestData(id,mdWithoutXLinks, mdWithXLinks)) 
	}
   lazy val importedDataMetadata = doImport("comprehensive-iso19139che.xml")
   lazy val importedServiceMetadata = doImport("comprehensive-service-iso19139che.xml")
   
  def importHas200Response(testData: => Response[TestData]) = testData must haveA200ResponseCode
  def xlinked(testData: => Response[TestData]) = (s:String) => {
    val mdWithXLinks = testData.value.mdWithXLinks
    val (node, number) = extract2(s)

    val elem = mdWithXLinks \\ node
    val countMatcher = if (number == "several") {
      (elem \@ "xlink:href" aka "href" must not beEmpty)
    } else {
      (elem \@ "xlink:href" aka "href" must haveSize(number.toInt))
    }

    (elem foldLeft (success: Result)) { (acc, next) => acc and (next.child must not beEmpty) } and
      (elem aka (node + " element") must not beEmpty) and
      countMatcher 
   }

  def facsimile(testData: => Response[TestData]) = (s:String) => {
    val contact = testData.value.mdWithoutXLinks \ "contact" \\ "facsimile" \ "CharacterString"
    
    contact.text must not (beEmpty)
  }
  def updateContact(importRequest: => Response[TestData]) = () => {
    val mdWithXLinks = importRequest.value.mdWithXLinks
    val id = importRequest.value.id
    
    val party = mdWithXLinks \\ "CHE_MD_Metadata" \ "contact" \ "CHE_CI_ResponsibleParty"

    val orgName = party \ "organisationName"
    
    val deRef = (orgName \\ "LocalisedCharacterString" \ "element" \@ "ref").head
    val orgRef = (orgName \ "element" \@ "ref").head
    
    val updateDe = "_"+deRef -> newDeOrgName
    val addFr = "_lang_FR_"+orgRef -> newFrOrgName
    val addEn = "_lang_EN_"+orgRef -> newEnOrgName
    val addIt = "_lang_IT_"+orgRef -> newItOrgName
    
    val parentPosition = party \\ "parentResponsibleParty" \ "CHE_CI_ResponsibleParty" \ "positionName"
    val parentPostitionRef = (parentPosition \\ "LocalisedCharacterString" \ "element" \@ "ref").head
    
    val updateParentPositionsName = "_"+parentPostitionRef -> newParentPositionName
    
    val editingResponse = (StartEditing() then UpdateMetadata(updateDe,addFr,addEn,addIt,updateParentPositionsName)).execute(id)
    EndEditing.execute(editingResponse.value)
    GetRawMetadataXml.execute(id).value.getXml
  }
  
  val newDeOrgName = "NewDeOrg"
  val newFrOrgName = "NewFrOrg"
  val newEnOrgName = "NewEnOrg"
  val newItOrgName = "NewItOrg"
    
  val newParentPositionName = "newParentPositionName"
  val newContact = (md:NodeSeq) => {
    
    val responsibleParty = md \\ "CHE_MD_Metadata" \ "contact" \ "CHE_CI_ResponsibleParty"
    val locales = responsibleParty \ "organisationName" \\ "LocalisedCharacterString"
    
    val de = locales find (l => (l @@ "locale").head == "#DE") map (_.text.trim)
    val fr = locales find (l => (l @@ "locale").head == "#FR") map (_.text.trim)
    val en = locales find (l => (l @@ "locale").head == "#EN") map (_.text.trim)
    val it = locales find (l => (l @@ "locale").head == "#IT") map (_.text.trim)
    
    (de must beSome(newDeOrgName)) and
    	(fr must beSome(newFrOrgName)) and
    	(en must beSome(newEnOrgName)) and
    	(it must beSome(newItOrgName))
  }
  
  val updatedParentPosition = (md:NodeSeq) => {
    val locales = md \\ "CHE_MD_Metadata" \ "contact" \ "CHE_CI_ResponsibleParty" \\ "parentResponsibleParty" \ "CHE_CI_ResponsibleParty" \\ "positionName" \\ "LocalisedCharacterString"

    locales.map(_.text.trim) must contain(newParentPositionName)
  }
  
}
