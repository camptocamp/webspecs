package c2c.webspecs.geonetwork.spec.search

import c2c.webspecs._
import geonetwork._

import org.specs2.specification.Step
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BasicXmlSearchSpec extends SampleDataGeonetworkSpecification {
  def is =
    "Search using XML fast search".title ^ Step(setup) ^ Step(config.adminLogin.execute() must haveA200ResponseCode) ^
      "Full Search returns all results" ! fullSearch ^
      "Equals search on denominator ${5000000}" ! equalsDenominator ^
      "Range search ${5000001} - ${5000002} on denominator will ${fail}" ! rangeDenominator ^
      "Range search ${4999999} - ${5000002} on denominator will ${pass}" ! rangeDenominator ^
      "BBox ${default} search" ! bboxSearch ^
      "BBox ${overlaps} search" ! bboxSearch ^
      "BBox ${equal} search" ! bboxSearch ^
                                                            Step (tearDown)

  def fullSearch = {
    val response = XmlSearch(1, 100).execute()
    
    (response must haveA200ResponseCode) and
    	((response.value.xml \ "summary" \ "@count" text).toInt must be_> (0))
  }
  val equalsDenominator = (s:String) => {
    val response = XmlSearch(1, 100, 'denominator -> extract1(s)).execute()

    (response must haveA200ResponseCode) and
      (response.value.xml.text must contain("da165110-88fd-11da-a88f-000d939bc5d8"))
  }

  val rangeDenominator = (s:String) => {
    val (from,to,pass) = extract3(s)
    val response = XmlSearch(1, 100, 'denominatorFrom -> from, 'denominatorTo -> to).execute()

    (response must haveA200ResponseCode) and
      (if(pass == "pass") response.value.xml.text must contain("da165110-88fd-11da-a88f-000d939bc5d8")
       else response.value.xml.text must not contain("da165110-88fd-11da-a88f-000d939bc5d8"))
  }

  val bboxSearch = (s:String) => {
    val relationParam = extract1(s) match {
      case "default" => None
      case relationship => Some('relation -> relationship)
    }

    val params = List('westBL -> -17.3, 'eastBL -> 51.1, 'southBL -> -34.6, 'northBL -> 38.2) ++ relationParam
    val result = XmlSearch(1, 50, params.toList:_*).execute()

    (result must haveA200ResponseCode) and
      (result.value.xml.text must contain("da165110-88fd-11da-a88f-000d939bc5d8"))
  }
}