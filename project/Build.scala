import sbt._
import Keys._


object WebSpecsBuild extends Build
{
  lazy val mapfishResolver = {
    val mapfishRepoUrl = new java.net.URL("http://dev.mapfish.org/ivy2")
    Resolver.url("Mapfish Ivy Repository", mapfishRepoUrl)(Resolver.ivyStylePatterns)
  }

  lazy val runTestSuite = TaskKey[Unit]("run-test-suite", "Run the main test suite for the current project")
  lazy val runTask = fullRunTask(runTestSuite , Test, "specs2.html", "c2c.webspecs.suite.AllSpecs")

  val sharedSettings = Seq[Setting[_]](
    resolvers := Seq(mapfishResolver),
    resolvers += ScalaToolsReleases,
    scalaVersion := "2.9.1",
    organization := "com.c2c",
    version := "1.0-SNAPSHOT",
    runTask
  )
  
  // ------------------------------ Root Project ------------------------------ //
	lazy val root:Project = Project("root",file(".")).
  aggregate(core,geonetwork,geocat, selenium, apps,geoserver, georchestra).
    aggregate(core, selenium).
    settings(publishArtifact := false)

  // ------------------------------ Core Project ------------------------------ //

  val coreDependencies = Seq(
    "org.specs2" %% "specs2" % "1.7.1",
    "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2",
    "org.apache.httpcomponents" % "httpclient" % "4.1.2",
    "org.apache.httpcomponents" % "httpmime" % "4.1.2",
    "com.github.scala-incubator.io" %% "scala-io-file" % "0.3.0",
    "org.pegdown" % "pegdown" % "1.0.2",
    "junit" % "junit" % "4.9",
		"net.liftweb" %% "lift-json" % "2.4-M5"
  )
  
  val coreSettings = Seq[Setting[_]](
	  libraryDependencies ++= coreDependencies,
	  commands ++= Seq(generateAccumClasses))
	
	lazy val core = Project("core", file("core")).
	  dependsOn(selenium).
	  settings( sharedSettings ++ coreSettings :_*)
  
  // ------------------------------ Geonetwork Project ------------------------------ //
	 
	lazy val geonetwork = Project("geonetwork", file("geonetwork/standard")).
	  dependsOn(core).settings(sharedSettings:_*)
	  
  // ------------------------------ Geocat Project ------------------------------ //

	lazy val geocat = Project("geocat", file("geonetwork/geocat")).
	  dependsOn (core, geonetwork % "test->test", selenium) settings (sharedSettings:_*)

  // ------------------------------ Geocat Project ------------------------------ //

	lazy val geoserver = Project("geoserver", file("geoserver")).
	  dependsOn (core, selenium) settings (sharedSettings:_*)

  // ------------------------------ Geocat Project ------------------------------ //

	lazy val georchestra = Project("georchestra", file("georchestra")).
	  dependsOn (geoserver % "test->test", selenium) settings (sharedSettings:_*)

  // ------------------------------ Selenium Project ------------------------------ //
	
  val seleniumVersion = "0.9.7376"
  val seleniumDependencies = Seq(
    "org.seleniumhq.selenium" % "selenium-java" % "2.15.0"
    )
  val seleniumSettings = Seq[Setting[_]](
      resolvers += mapfishResolver,
      resolvers += ("Selenium" at "http://repo1.maven.org/maven2/"),
  	  libraryDependencies ++= seleniumDependencies,
      libraryDependencies ++= coreDependencies
  )
  
  lazy val selenium = Project("selenium",file("selenium")).
    settings (sharedSettings ++ seleniumSettings :_*)

  // ------------------------------ Suites Project ------------------------------ //
  lazy val apps = Project("apps",file("apps")).
    dependsOn (geocat % "compile->test",geonetwork % "compile->test").
    settings (sharedSettings:_*)

  // ------------------------------ Docs Project ------------------------------ //
  lazy val Docs = config("docs") extend (Test)
  val docsSettings = Seq[Setting[_]](
      sources in Docs <<=
        (sources in (core,Compile),
        sources in (geonetwork,Test),
        sources in (geocat,Test),
        sources in (apps,Compile),
        sources in (geoserver,Test),
        sources in (georchestra,Test)
	   ) map { _ ++ _ ++ _ ++ _ ++ _ ++ _ filterNot {_.getPath matches """(\S+accumulating.Accumulat\S+\d+\.scala)"""}}
    )
  lazy val docsProj:Project = Project("documentation", file("docsProj")).
    dependsOn(core % "compile -> test").
    settings(sharedSettings ++ docsSettings :_*)

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