import sbt._

class WebSpecsProject(info: ProjectInfo) extends ParentProject(info) {


  val core = project("core","CoreWebSpec",new CoreProject(_))
  val geonetworkSpecs:GeonetworkSpecProject = project("geonetwork","GeonetworkParent", new GeonetworkSpecProject(_))

  val debug = project("apps","apps", geonetworkSpecs.geocat)

  class CoreProject(info: ProjectInfo) extends DefaultProject(info) {
    val specs = "org.scala-tools.testing" %% "specs" % "1.6.6"  withSources ()
    val tagSoup = "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2"
    val httpClient =   "org.apache.httpcomponents" % "httpclient" % "4.1" withSources ()
    val httpMime =   "org.apache.httpcomponents" % "httpmime" % "4.1" withSources ()
    val scalaioCore = "com.github.scala-incubator.io" %% "core" % "0.2.0-SNAPSHOT" withSources ()
    val scalaioFile = "com.github.scala-incubator.io" %% "file" % "0.2.0-SNAPSHOT" withSources ()

    override def compileOptions = super.compileOptions ++ List(CompileOptions.Unchecked,CompileOptions.Deprecation)
  }
  class GeonetworkSpecProject(info: ProjectInfo) extends ParentProject(info) {
    val standard = project("standard","StandardGeonetwork", core)
    val geocat = project("geocat","Geocat", standard)
  }
}
