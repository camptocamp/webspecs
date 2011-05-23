package c2c.webspecs
package geonetwork
package spec

import accumulating.AccumulatedResponse3
import org.specs2.specification._
class CreateSpec extends GeonetworkSpecification { def is =

  "This specification tests creating metadata"            ^
    "from a data template"                                ^  createFromTemplate("data") ^p
    "from a service template"                             ^  createFromTemplate("service")

  def createFromTemplate(templateType:String) =
                                                                   Step(setup) ^
      "Given a ${"+templateType+"} metadata template"              ^ SampleTemplate   ^
      "Creating a new metadata based on that template"             ^ Create           ^
      "Then the ${create} request should succeed"                  ^ GoodResponseCode ^
      "And the ${get} request should succeed"                      ^ GoodResponseCode ^
      "And the ${delete} request should succeed"                   ^ GoodResponseCode^
      "And the created metadata has no error elements"             ^ NoErrors ^
      "And the elements in template are in created metadata"       ^ ExpectedElements ^
                                                                   end^ Step(tearDown)

  object Create extends When[String, AccumulatedResponse3[EditValue, IdValue, IdValue, IdValue]] {
    def extract(templateId: String, text: String) = {
      val createMd = CreateMetadata(config, templateId)

      val request = (
          config.login then
          createMd startTrackingThen
          GetEditingMetadata trackThen
          DeleteMetadata trackThen
          GetEditingMetadata)

      request(None)
    }
  }

  object GoodResponseCode extends Then[AccumulatedResponse3[EditValue, IdValue, IdValue, IdValue]] {
    def extract(accumulatedResponse: AccumulatedResponse3[EditValue, IdValue, IdValue, IdValue],
                text: String) = {
      val response = extract1(text) match {
        case "succeed" => accumulatedResponse._1
        case "get" => accumulatedResponse._2
        case "delete" => accumulatedResponse._3
      }

      response.basicValue.responseCode must_== 200
    }
  }

  object NoErrors extends Then[AccumulatedResponse3[EditValue, IdValue, IdValue, IdValue]] {
    def extract(accumulatedResponse: AccumulatedResponse3[EditValue, IdValue, IdValue, IdValue],
                text: String) = {
      accumulatedResponse._2.value.withXml{md =>
          md \\ "ERROR" must beEmpty
          // TODO better checks
      }
    }
  }

  object ExpectedElements extends Then[AccumulatedResponse3[EditValue, IdValue, IdValue, IdValue]] {
    def extract(accumulatedResponse: AccumulatedResponse3[EditValue, IdValue, IdValue, IdValue],
                text: String) = {
      // TODO check that elements in template are in created metadata
      1 must_== 1
    }
  }
}
