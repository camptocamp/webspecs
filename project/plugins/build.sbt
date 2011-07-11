//eclipse project generator plugins

resolvers += {
  val typesafeRepoUrl = new java.net.URL("http://repo.typesafe.com/typesafe/releases")
  val pattern = Patterns(false, "[organisation]/[module]/[sbtversion]/[revision]/[type]s/[module](-[classifier])-[revision].[ext]")
  Resolver.url("Typesafe Repository", typesafeRepoUrl)(pattern)
}

libraryDependencies <<= (libraryDependencies, sbtVersion) { (deps, version) => 
  deps :+ ("com.typesafe.sbteclipse" %% "sbteclipse" % "1.1" extra("sbtversion" -> version))
}

//idea project generator plugins

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

libraryDependencies += "com.github.mpeltonen" %% "sbt-idea" % "0.10.0-SNAPSHOT"

resolvers += {
  val mapfishRepoUrl = new java.net.URL("http://dev.mapfish.org/ivy2")
  Resolver.url("Mapfish Ivy Repository", mapfishRepoUrl)(Resolver.ivyStylePatterns)
}
