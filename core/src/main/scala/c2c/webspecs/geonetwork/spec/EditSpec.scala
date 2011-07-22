package c2c.webspecs
package geonetwork
package spec

import edit._
import org.specs2.specification.{Then, Given, Step}
import org.specs2.execute.Result
import geonetwork.ImportMetadata
import accumulating.{AccumulatedResponse2, AccumulatedResponse3}
import xml.{XML, NodeSeq}

class EditSpec extends GeonetworkSpecification { def is =

  "This specification tests editing metadata"                     ^ Step(setup) ^
    "based on a data template"                                    ^  addExtent("data") ^
    "based on a service template"                                 ^  addExtent("service")
    "based on a data template"                                    ^  addContact("data") ^
    "based on a service template"                                 ^  addContact("service")
                                                                  Step(tearDown)


  def addExtent(templateType:String) =
      "Import a ${"+templateType+"} "                              ^ EditExtent.toGiven   ^
      "The ${import} request should succeed"                       ^ GoodResponseCode.toThen ^
      "the ${update} metadata request should succeed"              ^ GoodResponseCode.toThen ^
      "And the get ${new} metadata request should succeed"         ^ GoodResponseCode.toThen ^
      "There must be one extra extent"                             ^ HaveNewExtent ^
      "The new extent must have 1 Geographic Extent"               ^ HasGeographicBoundingBox ^
      "The new extent must have 1 Geographic Identifier"           ^ HasGeographicIdentifier ^
      "The new extent must have 1 Bounding Polygon"                ^ HasBoundingPolygon ^
                                                                   end

  def addContact(templateType:String) =
      "Import a ${"+templateType+"} metadata"                      ^ EditContact.toGiven   ^
      "And the ${update} metadata request should succeed"          ^ GoodResponseCode.toThen ^
      "And the get ${new} metadata request should succeed"         ^ GoodResponseCode.toThen ^
      "There must be one new contact"                              ^ HaveNewContact ^
                                                                   end

  type EditResponse = AccumulatedResponse3[IdValue,AddValue, MetadataValue, IdValue]
  val EditExtent = edit(AddNewExtent())  
  val EditContact = edit(AddNewContact())
  
  def edit[A <: Add](added:Response[EditValue] => Add) = (text: String) => {
      val fileName = extract1(text) match {
        case "service" => "/data/wfs-service-metadata-template.xml"
        case "data" => "/data/vector-metadata-template.xml"
      }

      val (data,contentBody) = ResourceLoader.loadDataFromClassPath(fileName,classOf[EditSpec],uuid)
      val importMetadata:ImportMetadata = ImportMetadata.findGroupId(contentBody,ImportStyleSheets.NONE,false)

      val request = (
        config.login then
        importMetadata startTrackingThen
        StartEditing.chain() then
        added trackThen
        GetMetadataXml() trackThen
        DeleteMetadata)

      (XML.loadString(data):NodeSeq,request(None))
    }

  val GoodResponseCode = (data: (NodeSeq,EditResponse), text: String) => {
      val (_,accumulatedResponse) = data
      val response = extract1(text) match {
        case "import" => accumulatedResponse._2
        case "update" => accumulatedResponse._2
        case "new" => accumulatedResponse._3
      }

      response must haveA200ResponseCode
    }

  object HaveNewExtent extends ExtentThen {
    def check(originalExtents: NodeSeq, finalExtents: NodeSeq, addedExtent: NodeSeq): Result = {
      val numFinalExtents: Int = finalExtents.size
      val numOriginalExtents: Int = originalExtents.size
      (numFinalExtents must be_> (0)) and ((numOriginalExtents + 1) must_== numFinalExtents)
    }
  }

  object HasBoundingPolygon extends ExtentThen {
    def check(originalExtents: NodeSeq, finalExtents: NodeSeq, addedExtent: NodeSeq): Result =
      (addedExtent \\ "EX_BoundingPolygon") must haveSize (1)
  }

  object HasGeographicBoundingBox extends ExtentThen {
    def check(originalExtents: NodeSeq, finalExtents: NodeSeq, addedExtent: NodeSeq): Result =
      (addedExtent \\ "EX_GeographicBoundingBox") must haveSize (1)
  }

  object HasGeographicIdentifier extends ExtentThen {
    def check(originalExtents: NodeSeq, finalExtents: NodeSeq, addedExtent: NodeSeq): Result =
      (addedExtent \\ "MD_Identifier" \\ "CharacterString") must haveSize (1)
  }

  object HaveNewContact extends ContactThen {
    def check(originalContacts: NodeSeq, finalContacts: NodeSeq, addedContact: NodeSeq): Result = {
      val numFinalContacts: Int = finalContacts.size
      val numOriginalContacts: Int = originalContacts.size
      (numFinalContacts must be_> (0)) and ((numOriginalContacts + 1) must_== numFinalContacts)
    }
  }

  abstract class ExtentThen extends Then[(NodeSeq,EditResponse)] {
    def extract(data: (NodeSeq,EditResponse),
                text: String) = {
      val (originalXmlValue,accumulatedResponse) = data

      val (_, _, finalMetadata, _) = accumulatedResponse.tuple

      val finalMetadataValue = finalMetadata.value

      val originalExtents = originalXmlValue \\ "extent"
      val finalExtents = finalMetadataValue.withXml {_ \\ "extent"}

      val addedExtent = finalExtents filterNot {originalExtents contains _}

      check(originalExtents,finalExtents,addedExtent)
    }

    def check(originalExtents:NodeSeq, finalExtents:NodeSeq,addedExtent:NodeSeq):Result
  }

  abstract class ContactThen extends Then[(NodeSeq,EditResponse)] {
    def extract(data: (NodeSeq,EditResponse),
                text: String) = {
      val (originalXmlValue,accumulatedResponse) = data

      val (_, _, finalMetadata, _) = accumulatedResponse.tuple

      val finalMetadataValue = finalMetadata.value

      val originalExtents = originalXmlValue \\ "extent"
      val finalExtents = finalMetadataValue.withXml {_ \\ "extent"}

      val addedExtent = finalExtents filterNot {originalExtents contains _}

      check(originalExtents,finalExtents,addedExtent)
    }

    def check(originalContacts:NodeSeq, finalContacts:NodeSeq,addedContact:NodeSeq):Result
  }

}
