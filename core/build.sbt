resolvers ++= Seq(
  "Mapfish Repo" at "http://dev.mapfish.org/maven/repository"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2" % "1.4-SNAPSHOT" withSources (),
  "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2",
  "org.apache.httpcomponents" % "httpclient" % "4.1" withSources (),
  "org.apache.httpcomponents" % "httpmime" % "4.1" withSources (),
  "com.github.scala-incubator.io" %% "core" % "0.1.2" withSources (),
  "com.github.scala-incubator.io" %% "file" % "0.1.2" withSources ()
)
