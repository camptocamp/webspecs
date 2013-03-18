import sbt._
import Keys._


object WebSpecsBuild extends Build
{
  lazy val mapfishResolver = {
    val mapfishRepoUrl = new java.net.URL("http://dev.mapfish.org/ivy2")
    Resolver.url("Mapfish Ivy Repository", mapfishRepoUrl)(Resolver.ivyStylePatterns)
  }

  lazy val runTestSuiteHtml = TaskKey[Unit]("run-test-suite-html", "Run the main test suite for the current project")
  lazy val runTaskHtml = fullRunTask(runTestSuiteHtml, Test, "specs2.html", "c2c.webspecs.suite.AllSpecs")
  lazy val runTestSuiteXml = TaskKey[Unit]("run-test-suite-xml", "Run the main test suite for the current project")
  lazy val runTaskXml = fullRunTask(runTestSuiteXml, Test, "specs2.junitxml", "c2c.webspecs.suite.AllSpecs")

  val sharedSettings = Seq[Setting[_]](
    resolvers := Seq(mapfishResolver),
    resolvers += ScalaToolsReleases,
    scalaVersion := "2.10.1",
    organization := "com.c2c",
    version := "1.0-SNAPSHOT",
    runTaskHtml,
    runTaskXml,
    parallelExecution in Test := false,
    logBuffered in Test := true,
    testOptions in Test += Tests.Argument("html", "console", "junitxml", "sequential"),
    testOptions in Test += Tests.Setup {() =>
      val key = "specs2.junit.outDir"
      val default = "target/surefire-reports"
      val property = System.getProperty(key, default)
      if (property.trim.isEmpty)
        System.setProperty(key, default)
      else
        System.setProperty(key, property)

      println("Starting webspecs tests")
    }
  )
  
  // ------------------------------ Root Project ------------------------------ //
	lazy val root:Project = Project("root",file(".")).
  aggregate(core,geonetwork,geocat, selenium, apps,geoserver, georchestra).
    aggregate(core, selenium).
    settings(publishArtifact := false)

  // ------------------------------ Core Project ------------------------------ //

  val coreDependencies = Seq(
    "org.specs2" %% "specs2" % "1.12.3",
    "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2",
    "org.apache.httpcomponents" % "httpclient" % "4.1.2",
    "org.apache.httpcomponents" % "httpmime" % "4.1.2",
    "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2",
    "org.pegdown" % "pegdown" % "1.0.2",
    "junit" % "junit" % "4.9",
		"net.liftweb" %% "lift-json" % "2.5-RC2"
  )
  
  val coreSettings = Seq[Setting[_]](
	  libraryDependencies ++= coreDependencies)
	
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
	   ) map { _ ++ _ ++ _ ++ _ ++ _ ++ _ }
    )
  lazy val docsProj:Project = Project("documentation", file("docsProj")).
    dependsOn(core % "compile -> test").
    settings(sharedSettings ++ docsSettings :_*)

}