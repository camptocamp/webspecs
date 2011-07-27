package c2c.webspecs

import org.specs2.Specification
import org.specs2.specification.{Then, When, Given, RegexStep}
import org.specs2.execute.Result
import xml.{Node, NodeSeq}
import java.net.URLEncoder
import java.net.URLDecoder

/**
 * Contains methods commons to many WebSpecs Specifications
 */

trait WebSpecsSpecification[C <: Config] extends Specification {
  implicit val config:C
  implicit val context = new DefaultExecutionContext()
  implicit val resourceBase = getClass

  lazy val fixtures:Traversable[Fixture[C]] = Nil

  def setup = ExecutionContext.withDefault { context2 =>
    config.setUpTestEnv(context2)
    fixtures.foreach{_.create(config, context2)}
    extraSetup(context2)
  }
  def extraSetup(setupContext:ExecutionContext):Unit = {}
  def tearDown = ExecutionContext.withDefault[Unit] {
    context2 =>
      try {
	      fixtures.foreach{_.delete(config, context2)}
	      extraTeardown(context2)
	      config.tearDownTestEnv (context2)
      } finally {
    	  context.close()
      }
  }
  
  def extraTeardown(teardownContext:ExecutionContext):Unit = {}

  def haveAResponseCode(code:Int) = ((_:Response[Any]).basicValue.responseCode == code, (resp:Response[Any]) => "Response code was expected to be "+code+" but was "+resp.basicValue.responseCode)
  def beAResponseCode(code:Int) = ((_:Response[Any]).basicValue.responseCode == code, (resp:Response[Any]) => "Response code was expected to be "+code+" but was "+resp.basicValue.responseCode)
  def haveA200ResponseCode = haveAResponseCode(200)
  def beA200ResponseCode = haveAResponseCode(200)
  val a200ResponseThen = (r:Response[Any], _:String) => r must haveA200ResponseCode

  /* Support for creating given and toThen's */
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

  implicit def functionWithStringToAsGiven[A](function:Function1[String,A]) = new {
    def toGiven = new Given[A] {
      def extract(text: String):A = function(text)
    }
  }
  implicit def functionToAsGiven[A](function:Function0[A]) = new {
    def toGiven = new Given[A] {
      def extract(text: String):A = function()
    }
  }

  implicit def resultFunctionWithStringToAsWhen[A,B](function:Function2[A,String,B])= new {
    def toWhen = new When[A,B]("") {
     def extract(given: A,text: String) = function(given,text)
    }
  }
  implicit def resultFunctionToAsWhen[A,B](function:Function1[A,B])= new {
    def toWhen = new When[A,B]("") {
     def extract(given: A,text: String) = function(given)
    }
  }

  implicit def resultFunctionWithStringToAsThen[A,R <% Result](function:Function2[A,String,R])= new {
    def toThen = new Then[A]("") {
     def extract(given: A,text: String) = function(given,text)
    }
    def narrow[B <: A] = new Then[B] {
      def extract(given: B,text: String) = function(given,text)
    }
  }
  implicit def resultFunctionToAsThen[A,R <% Result](function:Function1[A,R])= new {
    def toThen = new Then[A]("") {
     def extract(given: A,text: String) = function(given)
    }
    def narrow[B <: A] = new Then[B] {
      def extract(given: B,text: String) = function(given)
    }
  }
}