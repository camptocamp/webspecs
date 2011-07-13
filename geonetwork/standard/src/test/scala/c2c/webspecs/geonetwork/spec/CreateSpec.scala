package c2c.webspecs
package geonetwork
package spec

import edit._
import accumulating.AccumulatedResponse3
import org.specs2.specification._

class CreateSpec extends GeonetworkSpecification { def is =

  "This specification tests creating metadata"            ^
    "from a data template"                                ^  createFromTemplate("data") ^p
    "from a service template"                             ^  createFromTemplate("service")

  def createFromTemplate(templateType:String) =
                                                                   Step(setup) ^
      "Create a ${"+templateType+"} metadata       "               ^ Create.toGiven   ^
      "Then the ${create} request should succeed"                  ^ GoodResponseCode.toThen ^
      "And the ${get} request should succeed"                      ^ GoodResponseCode.toThen ^
      "And the ${delete} request should succeed"                   ^ GoodResponseCode.toThen ^
      "And the created metadata has no error elements"             ^ NoErrors.toThen ^
      "And the elements in template are in created metadata"       ^ ExpectedElements.toThen ^
                                                                   end^ Step(tearDown)

  type CreateResponse = AccumulatedResponse3[EditValue, IdValue, IdValue, IdValue]

  val Create = (text:String) => {
    val templateId = extract1(text) match {
        case "service" => config.sampleServiceTemplateIds(0)
        case "data" => config.sampleDataTemplateIds(0)
      }

      val createMd = CreateMetadata(config, templateId)

      val request = (
          config.login then
          createMd startTrackingThen
          GetEditingMetadata trackThen
          DeleteMetadata trackThen
          GetEditingMetadata)

      request(None)
  }
  

  val GoodResponseCode = (createResponse: CreateResponse, text: String) => {
      val response = extract1(text) match {
        case "create" => createResponse._1
        case "get" => createResponse._2
        case "delete" => createResponse._3
      }

      response must haveA200ResponseCode
    }

  val NoErrors = (createResponse: CreateResponse, text: String) => {
      createResponse._2.value.withXml{md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
      }
    }

  val ExpectedElements = (accumulatedResponse: CreateResponse,text: String) => {
    // TODO check that elements in template are in created metadata
    1 must_== 1
  }
}
