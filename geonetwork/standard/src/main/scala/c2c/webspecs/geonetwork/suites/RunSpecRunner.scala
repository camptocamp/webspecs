package c2c.webspecs.geonetwork.suites

import org.specs.runner.{SpecificationsFinder, SpecsFinder}

object RunSpecRunner {
  object Finder extends SpecificationsFinder {
      def exec(testPath:String,pattern:String) = {
        val specs = specificationNames ("**/*",pattern)

        specs foreach {s =>
          createSpecification(s).foreach{
            _.main(Array())
          }
        }
      }
    }
  def main(args:Array[String]) {
    val pattern = args.headOption getOrElse ".*Spec"
    val testPath = args.drop(1).headOption getOrElse "**/*"
    Finder.exec(testPath,pattern)
  }
}