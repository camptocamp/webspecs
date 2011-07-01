package c2c.webspecs.geonetwork.geocat.spec.WP1

import org.specs2.specification.Step
import c2c.webspecs.geonetwork.ImportStyleSheets.NONE
import c2c.webspecs.geonetwork._
import c2c.webspecs.{XmlValue, Response, IdValue, GetRequest}

/**
 * Created by IntelliJ IDEA.
 * User: pmauduit
 * Date: 28/06/11
 * Time: 16:06
 */

class CheSchemaMetadatas  extends GeonetworkSpecification {  def is =
  "This specification tests using the iso19139.CHE schema"                  ^ Step(setup)               ^
    "Inserting a CHE metadata"                                              ^ importISO19139CCHE.give  ^
    "Should suceed with a 200 response"                                     ^ import200Response         ^
       "Should returned the id of the inserted MD"                          ^ getInsertedMdId.then           ^
                                                                            end

  "Getting the previously inserted CHE metadata"                            ^ GetMetadataXml.give    ^
    "Should suceed with a 200 response"                                     ^ i200XmlResponse      ^
    "Should return the MD in XML"                                           ^ VerifyMetadataXml.then      ^
                                                                            Step(tearDown)

  val importISO19139CCHE = (_:String) => {
    val name = "metadata.iso19139.che.xml"
    val (_,content) = ImportMetadata.importDataFromClassPath("/data/"+name, getClass)
    val ImportMd = ImportMetadata.findGroupId(content,NONE,true)

    ImportMd(ImportStyleSheets.NONE):Response[IdValue]
  }
  val GetMetadataXml = () => null.asInstanceOf[Response[XmlValue]]

  val i200XmlResponse = a200ResponseThen.narrow[Response[XmlValue]]

  val i200Response = a200ResponseThen.narrow[Response[XmlValue]]

  val VerifyMetadataXml = (r:Response[XmlValue]) => pending

  val import200Response = a200ResponseThen.narrow[Response[IdValue]]


  val getInsertedMdId = (idRes:Response[IdValue]) => {
    val xmlResponse = GetRequest("xml.metadata.get", "id" -> idRes.value.id)(None)
    xmlResponse.value.withXml{md =>
      val node = md \\ "some elem to check for"
      node must not beEmpty
    }
  }
}