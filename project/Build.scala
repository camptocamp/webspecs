import sbt._
import Keys._
import complete.DefaultParsers._


object WebSpecsBuild extends Build
{
	// All internal projects must be listed in `projects`.
	lazy val projects = Seq(root, core, geonetwork, geocat, apps)

	// Declare a project in the root directory of the build with ID "root".
	// Declare an execution dependency on sub1.
	lazy val root:Project = Project("root",file(".")) aggregate(core,geonetwork,geocat, apps) settings (commands ++= Seq(
      printState,
  	  runSpec,
  	  generateAccumClasses
	  )
	)

  val coreSettings = Defaults.defaultSettings ++ Seq(
    libraryDependencies ++= List(
      "org.specs2" %% "specs2" % "1.3" withSources (),
       "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2",
       "org.apache.httpcomponents" % "httpclient" % "4.1" withSources (),
       "org.apache.httpcomponents" % "httpmime" % "4.1" withSources (),
       "com.github.scala-incubator.io" %% "core" % "0.1.2" withSources (),
       "com.github.scala-incubator.io" %% "file" % "0.1.2" withSources ()
    )
  )
	lazy val core = Project("core", file("core"),settings = coreSettings, delegates = root::Nil)  

	lazy val geonetwork = Project("geonetwork", file("geonetwork/standard"), delegates = root::Nil) dependsOn core

	lazy val geocat = Project("geocat", file("geonetwork/geocat"), delegates = root::Nil) dependsOn geonetwork
	
  lazy val apps = Project("apps",file("apps"), delegates = root::Nil) dependsOn geocat
  
  
	def show[T](s: Seq[T]) =
		s.map("'" + _ + "'").mkString("[", ", ", "]")

	def printState = Command.command("print-state") { state =>
		import state._
		println(state.getClass())

		state
	}

  val generateAccumClasses = Command.command("gen-classes") { state =>
    val accumlatingRequestTemplate = """
class AccumulatingRequest%8$s[-In,%1s,+Out](
    last:Response[%2$s] => Request[%2$s,Out],
    elems:Elem*) 
  extends AccumulatingRequest [In,Out]{

  override def assertPassed(in: In)(implicit context: ExecutionContext):AccumulatedResponse%8$s[%3$s,Out] =
    super.assertPassed(in).asInstanceOf[AccumulatedResponse%8$s[%3$s,Out]]
  override def then [A,B] (next: Request[Out,A]) : AccumulatingRequest%8$s[In, %3$s,A] =
    then(new ConstantRequestFunction(next))
  override def then [A,B] (next: Response[Out] => Request[Out,A]) : AccumulatingRequest%8$s[In, %3$s, A] =
    new AccumulatingRequest%8$s(next, elems :+ new Elem(last,false) :_*)

%10$s

  override def setIn[A <: In](in: A) =
    new AccumulatingRequest%8$s[Any, %3$s,Out](last, Elem(Request.const(in),false) +: elems: _*)


  def apply(in: In)(implicit context: ExecutionContext):AccumulatedResponse%8$s[%3$s,Out] = {
    val ResultData(lastResponse,trackedResponses) = doApply(in,last.asInstanceOf[RequestFactory],elems)

    new AccumulatedResponse%8$s(
      %4$s, 
      lastResponse.asInstanceOf[Response[Out]]
    )
  }

  override def toString() = elems.mkString("(","->",")")+" -> "+last
}
"""
  val accumlatingResponseTemplate = """
case class AccumulatedResponse%8$s[%1$s,+Z](
    %5$s,
    val last:Response[Z])
  extends AccumulatedResponse[Z] {

  def tuple = (
    %6$s,
    last
  )

  def values = (
    %7$s,
    last.value
  )
}"""

  val trackThenTemplate = """def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest%9$s[In,%3$s,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest%9$s[In,%3$s,Out,A] =
  new AccumulatingRequest%9$s[In,%3$s,Out,A](next,elems :+ new Elem(last,true) :_*)
"""
  val importsTemplate = """package c2c.webspecs
package accumulating

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction

"""
    val dir = new java.io.File("core/src/main/scala/c2c/webspecs/accumulating/")
    Option(dir.listFiles).foreach{files => 
      files.foreach {_.delete()}
    }
    dir.delete()
    dir.mkdirs

    val numGenerated = 21
   1 to numGenerated foreach {i =>
      val decTypes = 1 to i map {j => "+T"+j} mkString ","
      val lastType = "T"+i
      val types = 1 to i map {j => "T"+j} mkString ","
      val responsesVals = 1 to i map {j => "trackedResponses("+(j-1)+").asInstanceOf[Response[T"+j+"]]"} mkString ",\n      "
      val responseDec = 1 to i map {j => "val _"+j+":Response[T"+j+"]"} mkString ",\n    "
      val tupleDec = 1 to i map {j => "_"+j} mkString ",\n    "
      val valuesTupleDec = 1 to i map {j => "_"+j+".value"} mkString ",\n    "
      val trackThen = if (i!=numGenerated) trackThenTemplate.format(decTypes,lastType,types,responsesVals,responseDec,tupleDec,valuesTupleDec,i,i+1)
                      else ""

      val filledRequest = accumlatingRequestTemplate.format(decTypes,lastType,types,responsesVals,responseDec,tupleDec,valuesTupleDec,i,i+1,trackThen)
      val out = new java.io.FileOutputStream(new java.io.File(dir,"AccumulatingRequest"+(i)+".scala"))
      out.write((importsTemplate+filledRequest).getBytes("UTF8"))
      out.close
      
      val filledResponse = accumlatingResponseTemplate.format(decTypes,lastType,types,responsesVals,responseDec,tupleDec,valuesTupleDec,i,i+1,trackThen)
      val out2 = new java.io.FileOutputStream(new java.io.File(dir,"AccumulatedResponse"+(i)+".scala"))
      out2.write((importsTemplate+filledResponse).getBytes("UTF8"))
      out2.close()
      
    }
    state
  }
  val runSpec = Command.args("run-specs","[specPattern]") {(state, args) => 
    val extracted = Project.extract(state)
    import extracted._
/*    for{cp <- fullClasspath 
        r <- runner } {
          r.run("c2c.webspecs.geonetwork.suites.RunSpecRunner",cp,Nil,log)
    }
*/    state
  }
}