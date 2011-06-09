import sbt._
import Keys._
import complete.DefaultParsers._


object WebSpecsBuild extends Build
{

  val coreResolvers = Seq(
    "Mapfish Repo" at "http://dev.mapfish.org/maven/repository",
    ScalaToolsSnapshots
  )

  val sharedSettings = Seq(
    resolvers ++= coreResolvers,
    scalaVersion := "2.9.0-1",
    organization := "com.c2c",
    version := "1.0-SNAPSHOT"
  )
  
  // ------------------------------ Root Project ------------------------------ //
	lazy val root:Project = Project("root",file(".")).
	  aggregate(core,geonetwork,geocat, selenium, apps).
	  settings(
	   commands += runSpec
	  )

  lazy val runSpec = Command.command("run-spec") { state =>
    val classpath = Keys.fullClasspath in apps
    println(classpath)
    state
  }
  // ------------------------------ Core Project ------------------------------ //

  val coreDependencies = Seq(
    "org.specs2" %% "specs2" % "1.4" withSources (),
    "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2",
    "org.apache.httpcomponents" % "httpclient" % "4.1" withSources (),
    "org.apache.httpcomponents" % "httpmime" % "4.1" withSources (),
    "com.github.scala-incubator.io" %% "core" % "0.2.0-SNAPSHOT" withSources (),
    "com.github.scala-incubator.io" %% "file" % "0.2.0-SNAPSHOT" withSources ()
  )
  
  val coreSettings = Seq[Setting[_]](
	  libraryDependencies ++= coreDependencies,
	  commands ++= Seq(generateAccumClasses))
	
	lazy val core = Project("core", file("core")).settings( sharedSettings ++ coreSettings :_*)
  
  // ------------------------------ Geonetwork Project ------------------------------ //
	 
	lazy val geonetwork = Project("geonetwork", file("geonetwork/standard")).
	  dependsOn(core % "test->test").settings(sharedSettings:_*)
	  
  // ------------------------------ Geocat Project ------------------------------ //

	lazy val geocat = Project("geocat", file("geonetwork/geocat")).
	  dependsOn (core % "test->test", geonetwork % "test->test") settings (sharedSettings:_*)

  // ------------------------------ Selenium Project ------------------------------ //
	
  val seleniumVersion = "0.9.7376"
  val seleniumDependencies = Seq(
    "org.seleniumhq.selenium" % "selenium-remote-control" % "2.0rc2" withSources
    )
  val seleniumSettings = Seq[Setting[_]](
      resolvers ++= coreResolvers,
      resolvers += ("Selenium" at "http://repo1.maven.org/maven2/"),
  	  libraryDependencies ++= seleniumDependencies,
      libraryDependencies ++= coreDependencies
  )
  
  lazy val selenium = Project("selenium",file("selenium")).
    dependsOn(core % "test->test").
    settings (sharedSettings ++ seleniumSettings :_*)

  // ------------------------------ Apps Project ------------------------------ //
  lazy val apps = Project("apps",file("apps")).
    dependsOn (geocat % "compile->test") settings (sharedSettings:_*)
  
  // ------------------------------ GenerateAccumClasses Command (Part of Core) ------------------------------ //
  
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

}