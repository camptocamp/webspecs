package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import shared._
import shared._
import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import scala.xml.Node
import scala.xml.Elem
import scala.xml.NodeSeq

@RunWith(classOf[JUnitRunner]) 
class UpdateXlinksCachingSpec extends GeocatSpecification { def is = 
  "Update Xlinks and Cache".title 												                    ^ Step(setup) ^ 
  "Imports a metadata which contains the same contact twice and update one of two contacts"   		^ Step(ImportMdId)  ^
    "getting metadata again should indicate that both contacts have been updated"                   ! bothUsersUpdated ^ 
                                                           											  Step(tearDown)


  def findNamesWith(xml:NodeSeq, prefix:String) = {
    def text(node:Node) = (node \\ "individualFirstName"\\ "CharacterString").text.trim
    (xml \\ "_").filter(n => (n \ "CHE_CI_ResponsibleParty").nonEmpty).filter {n => text(n) startsWith prefix}
  }

  lazy val ImportMdId = {
    config.adminLogin.execute()
    val importRequest = ImportMetadata.defaults(uuid,"/geocat/data/contact_has_repeated_contact.xml",false,getClass,GeocatConstants.GM03_2_TO_CHE_STYLESHEET)._2
    
    val response = importRequest.execute().value
    registerNewMd(Id(response.id))
    
    val mdXml = GetEditingMetadataXml.execute(Id(response.id)).value.getXml

    findNamesWith(mdXml, "firstname").headOption foreach {node =>
      val data:Node = node.asInstanceOf[Elem].copy(child = newUser)
      UpdateSharedObject(data).execute()
    }
    
    response.id
  }

  def bothUsersUpdated = {
    val xml = GetRawMetadataXml.execute(Id(ImportMdId)).value.getXml
    
    findNamesWith(xml, "newFirstName") must haveSize(2)
  }
  
  val newUser: Node = 
    <che:CHE_CI_ResponsibleParty gco:isoType="gmd:CI_ResponsibleParty">
  <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">
    <gmd:PT_FreeText>
      <gmd:textGroup>
        <gmd:LocalisedCharacterString locale="#DE">swisstopo</gmd:LocalisedCharacterString>
      </gmd:textGroup>
    </gmd:PT_FreeText>
  </gmd:organisationName>
  <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
    <gco:CharacterString/>
  </gmd:positionName>
  <gmd:contactInfo>
    <gmd:CI_Contact>
      <gmd:phone>
        <che:CHE_CI_Telephone gco:isoType="gmd:CI_Telephone">
          <gmd:voice gco:nilReason="missing">
            <gco:CharacterString/>
          </gmd:voice>
          <gmd:facsimile gco:nilReason="missing">
            <gco:CharacterString/>
          </gmd:facsimile>
          <che:directNumber>
            <gco:CharacterString>+41319632423</gco:CharacterString>
          </che:directNumber>
          <che:mobile gco:nilReason="missing">
            <gco:CharacterString/>
          </che:mobile>
        </che:CHE_CI_Telephone>
      </gmd:phone>
      <gmd:address>
        <che:CHE_CI_Address gco:isoType="gmd:CI_Address">
          <gmd:city gco:nilReason="missing">
            <gco:CharacterString/>
          </gmd:city>
          <gmd:administrativeArea gco:nilReason="missing">
            <gco:CharacterString/>
          </gmd:administrativeArea>
          <gmd:postalCode gco:nilReason="missing">
            <gco:CharacterString/>
          </gmd:postalCode>
          <gmd:country xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
            <gco:CharacterString/>
          </gmd:country>
          <gmd:electronicMailAddress>
            <gco:CharacterString>{uuid}@c2c.com</gco:CharacterString>
          </gmd:electronicMailAddress>
          <che:streetName gco:nilReason="missing">
            <gco:CharacterString/>
          </che:streetName>
          <che:streetNumber gco:nilReason="missing">
            <gco:CharacterString/>
          </che:streetNumber>
          <che:addressLine gco:nilReason="missing">
            <gco:CharacterString/>
          </che:addressLine>
          <che:postBox gco:nilReason="missing">
            <gco:CharacterString/>
          </che:postBox>
        </che:CHE_CI_Address>
      </gmd:address>
      <gmd:onlineResource>
        <gmd:CI_OnlineResource>
          <gmd:linkage xsi:type="che:PT_FreeURL_PropertyType">
            <gmd:URL/>
          </gmd:linkage>
          <gmd:protocol>
            <gco:CharacterString>text/html</gco:CharacterString>
          </gmd:protocol>
          <gmd:name xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
            <gco:CharacterString/>
          </gmd:name>
          <gmd:description xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
            <gco:CharacterString/>
          </gmd:description>
        </gmd:CI_OnlineResource>
      </gmd:onlineResource>
      <gmd:hoursOfService>
        <gco:CharacterString>08h30 - 11h00 / 14h00 - 16h00 GMT+1</gco:CharacterString>
      </gmd:hoursOfService>
      <gmd:contactInstructions>
        <gco:CharacterString>niemals telefonieren</gco:CharacterString>
      </gmd:contactInstructions>
    </gmd:CI_Contact>
  </gmd:contactInfo>
  <gmd:role>
    <gmd:CI_RoleCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" codeListValue="originator"/>
  </gmd:role>
  <che:individualFirstName>
    <gco:CharacterString>newFirstName{uuid}</gco:CharacterString>
  </che:individualFirstName>
  <che:individualLastName>
    <gco:CharacterString>newLastName{uuid}</gco:CharacterString>
  </che:individualLastName>
  <che:organisationAcronym xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
    <gco:CharacterString/>
  </che:organisationAcronym>
</che:CHE_CI_ResponsibleParty>
}