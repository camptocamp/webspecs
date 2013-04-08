package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2._
import specification._
import java.util.UUID
import scala.xml.NodeSeq
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import shared._

@RunWith(classOf[JUnitRunner]) 
class AddSharedContactsSpec extends GeocatSpecification() { def is =
  "This specification tests creating shared contacts by passing in a contact xml"                               ^ Step(setup) ^ t ^
    "Calling shared.process with the xml snippet for adding a contact"                                          ^ contactAdd(true).toGiven ^
    "Should be a successful http request (200 response code)"                                                   ^ a200ResponseThen.narrow[Response[NodeSeq]] ^
    "Contact node should have an xlink href"                                                                    ^ hrefInElement("contact").toThen ^
    "Should have the correct ${host} in the xlink created during processing of shared object"                   ^ hrefHost("contact").toThen ^ 
    "Should have the correct ${port} in the xlink created during processing of shared object"                   ^ hrefHost("contact").toThen ^ 
    "xlink href should retrieve the full contact"                                                               ^ xlinkGetElement.toThen ^
    "Will result in a new shared contact"                                                                       ! newContact  ^
    "Will result in a new parent contact"                                                                       ! newParent   ^
                                                                                                                  endp ^
    "Updating an existing contact with new XML which does not have the parent contact"                          ^ updateContact.toGiven ^
      "must result in the contact retrieved from the xlink also not having the parent"                          ^ noParent.toThen ^
                                                                                                                  endp^
    "Adding same user should return same xlink"										                            ^ Step(contactAdd(true)) ^
      "must result in the contact retrieved from the xlink also not having the parent"                          ! newContact ^
                                                                                                                  endp^
    "Deleting all contacts"                                                                                     ^ Step(deleteNewContacts) ^
      "must ensure that the contact is in fact correctly deleted"                                                 ! noContacts ^
                                                                                                                  Step(tearDown)

  val originalOrg = "swisstopo"
  val newOrg = "camptocamp"
  var href:String = _
  def contactAdd(withParent:Boolean) = () => {
    config.adminLogin.execute()
    val result = ProcessSharedObject(contactXML(withParent,originalOrg)).execute()
    UserLogin.execute()
    result
  }
  val xlinkGetElement = (result:Response[NodeSeq]) => {
    href = (result.value \\ "contact" \@ "xlink:href")(0)
    val xlink = ResolveXLink.execute(href)

    val xml = xlink.value.withXml{ i => i}
    (xlink must haveA200ResponseCode) and
      (xml \\ "organisationName" map (_.text.trim) must contain (originalOrg)) and
      (xml \\ "individualFirstName" map (_.text.trim) must contain (contactFirstName)) and
      ((xml \\ "parentResponsibleParty" \@ "xlink:href") must not beEmpty)

  }

  def newContact = {
    val contacts = GeocatListUsers.execute(contactFirstName).value
    contacts.filter(_.name == contactFirstName) must haveSize(1)
  }
  def newParent = {
    val contacts = GeocatListUsers.execute(parentFirstName).value
    contacts.find(_.name == parentId+"FirstName*automated*") must beSome
  }

  val updateContact = () => {
    val id = GeocatListUsers.execute(contactFirstName).value.find(_.name == contactFirstName).get.userId
    val xml =
      <gmd:contact xmlns:xlink="http://www.w3.org/1999/xlink" xlink:show="embed" xlink:role="http://www.geonetwork.org/non_valid_obj" xlink:href={"local://xml.user.get?id="+id+"&amp;schema=iso19139.che&amp;role=originator"} gco:isotype="gmd:CI_ResponsibleParty" gco:isoType="gmd:CI_ResponsibleParty" xmlns:che="http://www.geocat.ch/2008/che" xmlns:xalan="http://xml.apache.org/xalan" xmlns:comp="http://www.geocat.ch/2003/05/gateway/GM03Comprehensive" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gml="http://www.opengis.net/gml" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd" >
        {contactXML(false,newOrg).child}
        </gmd:contact>
    val response = (config.adminLogin then UpdateSharedObject(xml)).execute()
    UserLogin.execute()
    response
  }
  val noParent = (result:Response[NodeSeq]) => {
    val xlink = GetRequest(href).execute()

    val xml = xlink.value.withXml{ i => i }
    (xlink must haveA200ResponseCode) and
      (xml \\ "individualFirstName" map (_.text.trim) must contain (contactFirstName)) and
      (xml \\ "organisationName" map (_.text.trim) must contain (newOrg)) and
      (xml \\ "parentResponsibleParty" must beEmpty)
  }

  def deleteNewContacts = {
    config.adminLogin.execute()
    (GeocatListUsers.execute(contactId).value ++ GeocatListUsers.execute(parentId).value).foreach{c =>
      assert(c.name contains "FirstName*automated*", c.userId+" -> "+c.username+" was to be deleted but is not part of the automated testing")
      val response = (DeleteSharedUser(c.userId,true)).execute()
      assert(response.basicValue.responseCode == 200, "DeleteSharedUser("+c.userId+" had a "+response.basicValue.responseCode +" response code")
    }
  }
  def noContacts = (GeocatListUsers.execute(contactId).value ++ GeocatListUsers.execute(parentId).value) must beEmpty

  lazy val contactId = uuid.toString
  lazy val parentId = UUID.randomUUID().toString
  lazy val contactFirstName = contactId+"FirstName*automated*"
  lazy val parentFirstName = parentId+"FirstName*automated*"
  def contactXML(withParent:Boolean,orgName:String) =  <gmd:contact gco:isoType="gmd:CI_ResponsibleParty" xmlns:che="http://www.geocat.ch/2008/che" xmlns:xalan="http://xml.apache.org/xalan" xmlns:comp="http://www.geocat.ch/2003/05/gateway/GM03Comprehensive" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:gml="http://www.opengis.net/gml" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
    <che:CHE_CI_ResponsibleParty>
      <gmd:organisationName xsi:type="gmd:PT_FreeText_PropertyType">
        <gmd:PT_FreeText>
          <gmd:textGroup>
            <gmd:LocalisedCharacterString locale="#DE">{orgName}</gmd:LocalisedCharacterString>
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
        <gco:CharacterString>{contactFirstName}</gco:CharacterString>
      </che:individualFirstName>
      <che:individualLastName>
        <gco:CharacterString>{contactId}LastName*automated*</gco:CharacterString>
      </che:individualLastName>
      <che:organisationAcronym xsi:type="gmd:PT_FreeText_PropertyType" gco:nilReason="missing">
        <gco:CharacterString />
      </che:organisationAcronym>{
      if (withParent) {
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
              <gco:CharacterString>{parentFirstName}</gco:CharacterString>
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
      } else {
        Nil
      }
    }</che:CHE_CI_ResponsibleParty>
  </gmd:contact>

}