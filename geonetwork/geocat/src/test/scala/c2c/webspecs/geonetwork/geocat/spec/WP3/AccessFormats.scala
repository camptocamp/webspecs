package c2c.webspecs
package geonetwork
package geocat
package spec.WP3

import org.specs2.specification.Step

class AccessFormats extends GeonetworkSpecification { def is =
  "This specification tests accessing shared formats"    ^ Step(setup) ^
    "Listing all formats"                                ^ listFormats.give ^
      "Should suceed with a 200 response"                ^ l200Response.then ^
      "Should show format name"                          ^ listNames.then    ^
      "Should have version"                              ^ listVersions.then   ^
      "Should indicate validation"                       ^ listValidations.then ^
                                                         end ^
    "Gettings a ${non-validated} format in iso xml"      ^ formatInIso.give ^
      "Should suceed with a 200 response"                ^ i200Response.then ^
      "Should show name"                                 ^ isoName.then      ^
      "Should have xlink"                                ^ isoxlink.then     ^
      "Should have version"                              ^ isoVersion.then   ^
      "Should be ${non-validated}"                       ^ isoValidation.then

  val listFormats = (_:String) => ListFormats.setIn("")(None)
  val l200Response = (r:Response[List[Format]], _:String) => pending
  val listNames = (response:Response[List[Format]], _:String) => pending
  val listVersions = (response:Response[List[Format]], _:String) => pending
  val listValidations = (response:Response[List[Format]], _:String) => pending

  val formatInIso = (s:String) => extract1(s) match {
    case "non-validated" => null.asInstanceOf[Response[XmlValue]]
    case "validated" => null.asInstanceOf[Response[XmlValue]]
  }
  val i200Response = (r:Response[XmlValue], _:String) => pending
  val isoName = (r:Response[XmlValue], _:String) => pending
  val isoxlink = (r:Response[XmlValue], _:String) => pending
  val isoVersion = (r:Response[XmlValue], _:String) => pending
  val isoValidation = (r:Response[XmlValue], _:String) => pending


  override lazy val fixtures = Seq(Fixture.Format)
}