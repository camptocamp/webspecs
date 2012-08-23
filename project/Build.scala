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
    scalaVersion := "2.9.1",
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
  aggregate(core, selenium, ign).
    aggregate(core, selenium).
    settings(publishArtifact := false)

  // ------------------------------ Core Project ------------------------------ //

  val coreDependencies = Seq(
    "org.specs2" %% "specs2" % "1.12",
    "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2",
    "org.apache.httpcomponents" % "httpclient" % "4.1.2",
    "org.apache.httpcomponents" % "httpmime" % "4.1.2",
    "postgresql" % "postgresql" % "9.1-901.jdbc4",
    "com.github.scala-incubator.io" %% "scala-io-file" % "0.3.0",
    "org.pegdown" % "pegdown" % "1.0.2",
    "junit" % "junit" % "4.9",
		"net.liftweb" %% "lift-json" % "2.4-M5"
  )
  
  val coreSettings = Seq[Setting[_]](
	  libraryDependencies ++= coreDependencies)
	
	lazy val core = Project("core", file("core")).
	  dependsOn(selenium).
	  settings( sharedSettings ++ coreSettings :_*)
  
	lazy val ign = Project("ign", file("ign")).
	  dependsOn (core, selenium) settings (sharedSettings:_*)

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

}