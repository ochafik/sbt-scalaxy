resolvers += Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.4")

addSbtPlugin("com.jsuereth" % "xsbt-gpg-plugin" % "0.6")

// ls.implicit.ly
addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.2")

resolvers ++= Seq(
  Classpaths.sbtPluginReleases,
  Opts.resolver.sonatypeReleases
)
