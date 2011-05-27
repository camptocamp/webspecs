package c2c.webspecs.geonetwork.geocat.spec

import org.specs2._
import execute.Result
import specification.{Then, Given, Step}

class SequentialStep extends Specification { def is =
  "Test sequential behavior of steps"                ^ Step(longStep) ^
    "Execute some given that takes time too"         ^ aGiven  ^
    "long step must be done but not second"          ^ aThenCheckingLongStep

  object aGiven extends Given[String] {
    def extract(text: String): String = {
      if(!longStepDone) {
        throw new Error("the longStep is not yet done and the Given is executing !!!!!")
      }
      Thread.sleep(2000)
      "hi"
    }
  }
  object aThenCheckingLongStep extends Then[String] {
    def extract(t: String, text: String): Result = {
      longStepDone
    }
  }
  var longStepDone = false
  def longStep = {
    Thread.sleep(10000)
    longStepDone = true
  }
}