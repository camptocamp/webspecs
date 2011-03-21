import sbt._
import Keys._
import complete.DefaultParsers._


object WebSpecsBuild extends Build
{
	// All internal projects must be listed in `projects`.
	lazy val projects = Seq(root, core, geonetwork, geocat, apps)

	// Declare a project in the root directory of the build with ID "root".
	// Declare an execution dependency on sub1.
	lazy val root = Project("root",file(".")) aggregate(core,geonetwork,geocat, apps) settings (commands ++= Seq(
      printState,
  	  runSpec,
  	  generateAccumClasses
	  )
	)

  val coreSettings = Defaults.defaultSettings ++ Seq(
    libraryDependencies ++= List(
      "org.scala-tools.testing" %% "specs" % "1.6.6"  withSources (),
       "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2",
       "org.apache.httpcomponents" % "httpclient" % "4.1" withSources (),
       "org.apache.httpcomponents" % "httpmime" % "4.1" withSources (),
       "com.github.scala-incubator.io" %% "core" % "0.2.0-SNAPSHOT" withSources (),
       "com.github.scala-incubator.io" %% "file" % "0.2.0-SNAPSHOT" withSources ()
    )
  )
	lazy val core = Project("core", file("core"),settings = coreSettings)  

	lazy val geonetwork = Project("geonetwork", file("geonetwork/standard")) dependsOn core

	lazy val geocat = Project("geocat", file("geonetwork/geocat")) dependsOn geonetwork
	
  lazy val apps = Project("apps",file("apps")) dependsOn geocat
  
  
	def show[T](s: Seq[T]) =
		s.map("'" + _ + "'").mkString("[", ", ", "]")

	def printState = Command.command("print-state") { state =>
		import state._
		println(state.getClass())

		state
	}

  val generateAccumClasses = Command.command("gen-classes") { state =>
    val template = """
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
}
"""
    val trackThenTemplate = """def trackThen [A,B] (next: Request[Out,A]):AccumulatingRequest%9$s[In,%3$s,Out,A] =
  trackThen(new ConstantRequestFunction(next))
def trackThen [A,B] (next: Response[Out] => Request[Out,A]):AccumulatingRequest%9$s[In,%3$s,Out,A] =
  new AccumulatingRequest%9$s[In,%3$s,Out,A](next,elems :+ new Elem(last,true) :_*)
"""
    val numGenerated = 21
    val code = 1 to numGenerated map {i =>
      val decTypes = 1 to i map {j => "+T"+j} mkString ","
      val lastType = "T"+i
      val types = 1 to i map {j => "T"+j} mkString ","
      val responsesVals = 1 to i map {j => "trackedResponses("+(j-1)+").asInstanceOf[Response[T"+j+"]]"} mkString ",\n      "
      val responseDec = 1 to i map {j => "val _"+j+":Response[T"+j+"]"} mkString ",\n    "
      val tupleDec = 1 to i map {j => "_"+j} mkString ",\n    "
      val valuesTupleDec = 1 to i map {j => "_"+j+".value"} mkString ",\n    "
      val trackThen = if (i!=numGenerated) trackThenTemplate.format(decTypes,lastType,types,responsesVals,responseDec,tupleDec,valuesTupleDec,i,i+1)
                      else ""
      template.format(decTypes,lastType,types,responsesVals,responseDec,tupleDec,valuesTupleDec,i,i+1,trackThen)
    }

    val finalCode = """package c2c.webspecs

import AccumulatingRequest._
import ChainedRequest.ConstantRequestFunction
    
"""+(code.mkString("\n\n"))
    val file = new java.io.File("core/src/main/scala/c2c/webspecs/generated-accumulated-requests.scala")
    file.delete()
    val out = new java.io.FileOutputStream(file)
    out.write(finalCode.getBytes("UTF8"))
    out.close
    
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