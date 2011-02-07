import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val specs = "org.scala-tools.testing" % "specs_2.8.1" % "1.6.6"  withSources ()
  val tagSoup = "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2"
}
