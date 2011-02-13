import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val specs = "org.scala-tools.testing" %% "specs" % "1.6.6"  withSources ()
  val tagSoup = "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2"
  val httpClient =   "org.apache.httpcomponents" % "httpclient" % "4.1" withSources ()
  val scalaiocore = "com.github.scala-incubator.io" %% "core" % "0.1.0" withSources ()
  val scalaiofile = "com.github.scala-incubator.io" %% "file" % "0.1.0" withSources ()
}
