resolvers += {
  val typesafeRepoUrl = new java.net.URL("http://repo.typesafe.com/typesafe/ivy-releases")
  Resolver.url("Typesafe Repository", typesafeRepoUrl)(Resolver.ivyStylePatterns)
}

resolvers += {
  val mapfishRepoUrl = new java.net.URL("http://dev.mapfish.org/ivy2")
  Resolver.url("Mapfish Ivy Repository", mapfishRepoUrl)(Resolver.ivyStylePatterns)
}

libraryDependencies <<= (libraryDependencies, sbtVersion) { (deps, version) => 
  deps :+ ("com.typesafe.sbteclipse" % "sbteclipse_2.8.1" % "1.1" extra("sbtversion" -> version))
  deps :+ ("org.sbtidea" % "xsbt-idea_2.8.1" % "0.1")
}