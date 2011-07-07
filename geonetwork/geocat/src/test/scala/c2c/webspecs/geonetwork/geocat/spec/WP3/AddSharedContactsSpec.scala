package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2._
import specification._
import java.util.UUID
import scala.xml.NodeSeq

class AddSharedContactsSpec extends GeonetworkSpecification { def is =
  "This specification tests creating shared contacts by passing in a contact xml"                               ^ Step(setup) ^ t ^
    "Calling shared.process with the xml snippet for adding a contact"                                          ^ contactAdd.toGiven ^
    "Should have 200 result"                                                                                    ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
    "Contact node should have an xlink href"                                                                    ^ hrefInElement.toThen ^
    "xlink href should retrieve the full contact"                                                               ^ xlinkGetElement.toThen ^
    "Will result in a new shared contact"                                                                       ! newContact  ^
    "Will result in a new parent contact"                                                                       ! newParent   ^
                                                                                                                  end ^
                                                                                                                  Step(deleteNewContacts) ^
                                                                                                                  Step(tearDown)

  val contactAdd = () => (config.adminLogin then ProcessSharedObject(contactXML))(None)
  val hrefInElement = (result:Response[NodeSeq]) => (result.value \\ "contact" \@ "xlink:href") must not beEmpty
  val xlinkGetElement = (result:Response[NodeSeq]) => {
    val href = (result.value \\ "contact" \@ "xlink:href")(0)
    val xlink = GetRequest(href)(None)
    (xlink must haveA200ResponseCode) and
      (xlink.value.withXml{_ \\ "individualLastName" map (_.text.trim) must contain (contactFirstName,parentFirstName)})
  }

  def newContact = GeocatListUsers(contactFirstName).value.find(_.name == contactFirstName) must beSome
  def newParent = GeocatListUsers(parentFirstName).value.find(_.name == parentId+"FirstName*automated*") must beSome

  def deleteNewContacts = GeocatListUsers("FirstName*automated*").value.foreach{c => DeleteUser(c.userId)(None)}

  lazy val contactId = UUID.randomUUID().toString
  lazy val parentId = UUID.randomUUID().toString
  lazy val contactFirstName = contactId+"FirstName*automated*"
  lazy val parentFirstName = parentId+"FirstName*automated*"
  lazy val contactXML =   <gmd:contact gco:isoType="gmd:CI_ResponsibleParty" xmlns:che="http://www.geocat.ch/2008/che" xmlns:xalan="http://xml.apache.org/xalan" xmlns:comp="http://www.geocat.ch/2003/05/gateway/GM03Comprehensive" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gml="http://www.opengis.net/gml" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
    <che:CHE_CI_ResponsibleParty>
      <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">
        <gmd:PT_FreeText>
          <gmd:textGroup>
            <gmd:LocalisedCharacterString locale="#DE">swisstopo</gmd:LocalisedCharacterString>
          </gmd:textGroup>
        </gmd:PT_FreeText>
      </gmd:organisationName>
      <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
        <gco:CharacterString />
      </gmd:positionName>
      <gmd:contactInfo>
        <gmd:CI_Contact>
          <gmd:phone>
            <che:CHE_CI_Telephone gco:isoType="gmd:CI_Telephone">
              <gmd:voice gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:voice>
              <gmd:facsimile gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:facsimile>
              <che:directNumber>
                <gco:CharacterString>+41319632423</gco:CharacterString>
              </che:directNumber>
              <che:mobile gco:nilReason="missing">
                <gco:CharacterString />
              </che:mobile>
            </che:CHE_CI_Telephone>
          </gmd:phone>
          <gmd:address>
            <che:CHE_CI_Address gco:isoType="gmd:CI_Address">
              <gmd:city gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:city>
              <gmd:administrativeArea gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:administrativeArea>
              <gmd:postalCode gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:postalCode>
              <gmd:country xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:country>
              <gmd:electronicMailAddress gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:electronicMailAddress>
              <che:streetName gco:nilReason="missing">
                <gco:CharacterString />
              </che:streetName>
              <che:streetNumber gco:nilReason="missing">
                <gco:CharacterString />
              </che:streetNumber>
              <che:addressLine gco:nilReason="missing">
                <gco:CharacterString />
              </che:addressLine>
              <che:postBox gco:nilReason="missing">
                <gco:CharacterString />
              </che:postBox>
            </che:CHE_CI_Address>
          </gmd:address>
          <gmd:onlineResource>
            <gmd:CI_OnlineResource>
              <gmd:linkage xsi:type="che:PT_FreeURL_PropertyType">
                <gmd:URL />
              </gmd:linkage>
              <gmd:protocol>
                <gco:CharacterString>text/html</gco:CharacterString>
              </gmd:protocol>
              <gmd:name xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
                <gco:CharacterString />
              </gmd:name>
              <gmd:description xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
                <gco:CharacterString />
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
        <gmd:CI_RoleCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" codeListValue="originator" />
      </gmd:role>
      <che:individualFirstName>
        <gco:CharacterString>{contactId}FirstName*automated*</gco:CharacterString>
      </che:individualFirstName>
      <che:individualLastName>
        <gco:CharacterString>{contactId}LastName*automated*</gco:CharacterString>
      </che:individualLastName>
      <che:organisationAcronym xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
        <gco:CharacterString />
      </che:organisationAcronym>
      <che:parentResponsibleParty>
        <che:CHE_CI_ResponsibleParty gco:isoType="gmd:CI_ResponsibleParty">
          <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">
            <gmd:PT_FreeText>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#DE">Bundesamt für Landestopografie</gmd:LocalisedCharacterString>
              </gmd:textGroup>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#FR">Office federal de topographie</gmd:LocalisedCharacterString>
              </gmd:textGroup>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#IT">Ufficio federale da topografia</gmd:LocalisedCharacterString>
              </gmd:textGroup>
            </gmd:PT_FreeText>
          </gmd:organisationName>
          <gmd:positionName xsi:type="gmd:PT_FreeText_PropertyType">
            <gmd:PT_FreeText>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#DE">tester</gmd:LocalisedCharacterString>
              </gmd:textGroup>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#FR">tester</gmd:LocalisedCharacterString>
              </gmd:textGroup>
            </gmd:PT_FreeText>
          </gmd:positionName>
          <gmd:contactInfo>
            <gmd:CI_Contact>
              <gmd:phone>
                <che:CHE_CI_Telephone gco:isoType="gmd:CI_Telephone">
                  <gmd:voice>
                    <gco:CharacterString>+41 31 963 21 11</gco:CharacterString>
                  </gmd:voice>
                  <gmd:facsimile>
                    <gco:CharacterString>+41 31 963 24 59</gco:CharacterString>
                  </gmd:facsimile>
                  <che:directNumber gco:nilReason="missing">
                    <gco:CharacterString />
                  </che:directNumber>
                  <che:mobile gco:nilReason="missing">
                    <gco:CharacterString />
                  </che:mobile>
                </che:CHE_CI_Telephone>
              </gmd:phone>
              <gmd:address>
                <che:CHE_CI_Address gco:isoType="gmd:CI_Address">
                  <gmd:city>
                    <gco:CharacterString>Wabern</gco:CharacterString>
                  </gmd:city>
                  <gmd:administrativeArea>
                    <gco:CharacterString>Köniz</gco:CharacterString>
                  </gmd:administrativeArea>
                  <gmd:postalCode>
                    <gco:CharacterString>3084</gco:CharacterString>
                  </gmd:postalCode>
                  <gmd:country xsi:type="gmd:PT_FreeText_PropertyType">
                    <gco:CharacterString>CH</gco:CharacterString>
                  </gmd:country>
                  <gmd:electronicMailAddress>
                    <gco:CharacterString>{parentId}@swisstopo.ch</gco:CharacterString>
                  </gmd:electronicMailAddress>
                  <che:streetName>
                    <gco:CharacterString>Seftigenstrasse</gco:CharacterString>
                  </che:streetName>
                  <che:streetNumber>
                    <gco:CharacterString>264</gco:CharacterString>
                  </che:streetNumber>
                  <che:addressLine>
                    <gco:CharacterString>ccc</gco:CharacterString>
                  </che:addressLine>
                  <che:postBox>
                    <gco:CharacterString>12345</gco:CharacterString>
                  </che:postBox>
                </che:CHE_CI_Address>
              </gmd:address>
              <gmd:onlineResource>
                <gmd:CI_OnlineResource>
                  <gmd:linkage xsi:type="che:PT_FreeURL_PropertyType">
                    <che:PT_FreeURL>
                      <che:URLGroup>
                        <che:LocalisedURL locale="#DE">http://www.swisstopo.admin.ch</che:LocalisedURL>
                      </che:URLGroup>
                    </che:PT_FreeURL>
                  </gmd:linkage>
                  <gmd:protocol>
                    <gco:CharacterString>text/html</gco:CharacterString>
                  </gmd:protocol>
                  <gmd:name xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
                    <gco:CharacterString />
                  </gmd:name>
                  <gmd:description xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
                    <gco:CharacterString />
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
            <gmd:CI_RoleCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#CI_RoleCode" codeListValue="distributor" />
          </gmd:role>
          <che:individualFirstName>
            <gco:CharacterString>{parentId}FirstName*automated*</gco:CharacterString>
          </che:individualFirstName>
          <che:individualLastName>
            <gco:CharacterString>{parentId}LastName*automated*</gco:CharacterString>
          </che:individualLastName>
          <che:organisationAcronym xsi:type="gmd:PT_FreeText_PropertyType">
            <gmd:PT_FreeText>
              <gmd:textGroup>
                <gmd:LocalisedCharacterString locale="#DE">swisstopo</gmd:LocalisedCharacterString>
              </gmd:textGroup>
            </gmd:PT_FreeText>
          </che:organisationAcronym>
        </che:CHE_CI_ResponsibleParty>
      </che:parentResponsibleParty>
    </che:CHE_CI_ResponsibleParty>
  </gmd:contact>

}