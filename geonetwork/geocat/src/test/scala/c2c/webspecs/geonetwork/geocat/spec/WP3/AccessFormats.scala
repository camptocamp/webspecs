package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import c2c.webspecs.{XmlValue, Response}

class AccessFormats extends GeonetworkSpecification { def is =
  "This specification tests accessing shared formats"    ^ Step(setup) ^
    "Listing all formats"                                ^ listFormats.give ^
      "Should suceed with a 200 response"                ^ l200Response ^
      "Should show format name"                          ^ listNames.then    ^
      "Should have version"                              ^ listVersions.then   ^
      "Should indicate validation"                       ^ listValidations.then ^
                                                         end ^
    "Gettings a format in iso xml"                       ^ formatInIso.give  ^
      "Should suceed with a 200 response"                ^ i200Response      ^
      "Should show name"                                 ^ isoName.then      ^
      "Should have version"                              ^ isoVersion.then   ^
                                                           Step(tearDown)    ^
    "Format Fixture should be deleted"                   ! noFormat

  val listFormats = (_:String) => ListFormats.setIn("")(None)
  val l200Response = a200ResponseThen.narrow[Response[List[Format]]]
  val listNames = (response:Response[List[Format]], _:String) => {
    response.value.map{_.name} must contain (formatFixture.name)
  }
  val listVersions = (response:Response[List[Format]], _:String) => {
    response.value.map{_.version} must contain (formatFixture.version)
  }
  val listValidations = (response:Response[List[Format]], _:String) => {
    response.value.find{_.version == formatFixture.version}.map{_.validated} must beSome(false)
  }

  val formatInIso = (s:String) => (GetRequest("xml.format.get","id" -> formatFixture.id)(None)):Response[XmlValue]
  val i200Response = a200ResponseThen.narrow[Response[XmlValue]]
  val isoName = (r:Response[XmlValue], _:String) =>
    r.value.withXml { xml =>
      val nameElems = xml \\ "name"
      ( (nameElems must haveSize (1)) and
        (nameElems.head.text.trim must_== formatFixture.name)
      )
    }
  val isoVersion = (r:Response[XmlValue], _:String) =>
    r.value.withXml { xml =>
      val nameElems = xml \\ "version"
      ( (nameElems must haveSize (1)) and
        (nameElems.head.text.trim must_== formatFixture.version)
      )
    }

  def noFormat =
    ExecutionContext.withDefault{c =>
      val response = GetRequest("xml.format.get!","id" -> formatFixture.id)(None)(c).value
      response.withXml(_ \\ "record" must beEmpty)
    }

  lazy val formatFixture = GeocatFixture.format
  override lazy val fixtures = Seq(formatFixture)
}