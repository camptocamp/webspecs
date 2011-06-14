package c2c.webspecs
package geonetwork


import org.specs2._
import specification._
import UserProfiles._
import c2c.webspecs.ExecutionContext
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.matcher.MatchResult
import org.specs2.execute.Result

@RunWith(classOf[JUnitRunner]) 
abstract class GeonetworkSpecification(userProfile: UserProfile = Editor) extends Specification {
  implicit val config = GeonetConfig(userProfile, getClass().getSimpleName)
  implicit val context = new DefaultExecutionContext()
  implicit val resourceBase = getClass

  object template extends Given[String] {
    def extract(text: String): String = extract1(text)
  }
  object randomTemplate extends Given[String] {
    def extract(text: String): String = config.sampleDataTemplateIds(0)
  }

  lazy val UserLogin = config.login


  def setup = ExecutionContext.withDefault {
    implicit context => config.setUpTestEnv
  }
  def tearDown = ExecutionContext.withDefault[Unit] {
    implicit context =>
      context.close()
      config.tearDownTestEnv
  }

  def haveResponseCode(code:Int) = ((_:Response[Any]).basicValue.responseCode == code, (resp:Response[Any]) => "Response code was expected to be "+code+" but was "+resp.basicValue.responseCode)
  def have200ResponseCode = haveResponseCode(200)
  
  /* Support for creating given and then's */
  object Extracts extends RegexStep[Unit, Any]("")
  def extract1(text:String) = Extracts.extract1(text)
  def extract2(text:String) = Extracts.extract2(text)
  def extract3(text:String) = Extracts.extract3(text)
  def extract4(text:String) = Extracts.extract4(text)
  def extract5(text:String) = Extracts.extract5(text)
  def extract6(text:String) = Extracts.extract6(text)
  def extract7(text:String) = Extracts.extract7(text)
  def extract8(text:String) = Extracts.extract8(text)
  def extract9(text:String) = Extracts.extract9(text)
  def extractAll(text:String) = Extracts.extractAll(text)
  
  implicit def functionToAsGiven[A](function:Function1[String,A]) = new {
    def give = new Given[A] {
      def extract(text: String):A = function(text)
    }
  }

  implicit def resultFunctionToAsWhen[A,B](function:Function2[A,String,B])= new {
    def when = new When[A,B]("") {
     def extract(given: A,text: String) = function(given,text)
    }
  }

  implicit def resultFunctionToAsThen[A,R <% Result](function:Function2[A,String,R])= new {
    def then = new Then[A]("") {
     def extract(given: A,text: String) = function(given,text)
    }
  }
}
