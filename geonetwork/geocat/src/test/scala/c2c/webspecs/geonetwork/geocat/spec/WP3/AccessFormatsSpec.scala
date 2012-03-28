package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step
import org.specs2.execute.Result
import c2c.webspecs.{XmlValue, Response}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner]) 
class AccessFormatsSpec extends GeocatSpecification { def is =
  "This specification tests accessing shared formats"                              ^ Step(setup) ^
    "Listing all formats (search for ${ })"                                         ^ listFormats.toGiven ^
      "Should be a successful http request (200 response code)"                                         ^ l200Response ^
      "Should show format name"                                                    ^ listNames.toThen    ^
      "Should have version"                                                        ^ listVersions.toThen   ^
      "Should indicate validation"                                                 ^ listValidations.toThen ^
                                                                                     end ^
    "Searching for ${"+formatFixture.name.toLowerCase+"}"                          ^ listFormats.toGiven ^
      "Should be a successful http request (200 response code)"                                         ^ l200Response ^
      "Should returns results that all contain "+formatFixture.name                ^ allNames.toThen    ^
                                                                                     end ^
    "Gettings a format in iso xml"                                                 ^ formatInIso.toGiven  ^
      "Should be a successful http request (200 response code)"                                          ^ i200Response      ^
      "Should show name"                                                           ^ isoName.toThen      ^
      "Should have version"                                                        ^ isoVersion.toThen   ^
                                                                                     Step(tearDown)    ^
    "Format Fixture should be deleted"                                             ! noFormat

  val listFormats = (s:String) => ListFormats.setIn(extract1(s).trim()).execute()
  val l200Response = a200ResponseThen.narrow[Response[List[Format]]]
  val listNames = (response:Response[List[Format]], _:String) => {
    response.value.map{_.name} must contain (formatFixture.name)
  }
  val allNames = (response:Response[List[Format]], _:String) => {
    response.value.foldLeft(success:Result){(acc,next) => acc and (next.name must =~ (formatFixture.name)) }
  }
  val listVersions = (response:Response[List[Format]], _:String) => {
    response.value.map{_.version} must contain (formatFixture.version)
  }
  val listValidations = (response:Response[List[Format]], _:String) => {
    response.value.find{_.version == formatFixture.version}.map{_.validated} must beSome(false)
  }

  val formatInIso = (s:String) => (GetRequest("xml.format.get","id" -> formatFixture.id).execute()):Response[XmlValue]
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
      val response = GetRequest("xml.format.get!","id" -> formatFixture.id).execute()(c,uriResolver).value
      response.withXml(_ \\ "response" \\ "record" must beEmpty)
    }

  lazy val formatFixture = GeocatFixture.format
  override lazy val fixtures = Seq(formatFixture)
}