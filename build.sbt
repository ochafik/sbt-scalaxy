name := "sbt-scalaxy"

organization := "com.nativelibs4java"

version := "0.3-SNAPSHOT"

sbtPlugin := true

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.9.2"

//CrossBuilding.crossSbtVersions := Seq("0.11.3", "0.11.2" ,"0.12")

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked")

licenses := Seq("BSD-3-Clause" -> url("http://www.opensource.org/licenses/BSD-3-Clause"))

homepage := Some(url("https://github.com/ochafik/Scalaxy"))

pomIncludeRepository := { _ => false }

pomExtra := (
  <scm>
    <url>git@github.com:ochafik/sbt-scalaxy.git</url>
    <connection>scm:git:git@github.com:ochafik/sbt-scalaxy.git</connection>
  </scm>
  <developers>
    <developer>
      <id>ochafik</id>
      <name>Olivier Chafik</name>
      <url>http://ochafik.com/</url>
    </developer>
  </developers>
)

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("-SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

(LsKeys.docsUrl in LsKeys.lsync) <<= homepage

(LsKeys.tags in LsKeys.lsync) := 
   Seq("sbt-plugin", "compiler-plugin", "rewrite", "ast", "transform", "optimization", "optimisation")

(description in LsKeys.lsync) :=
  "An sbt plugin to help use or define Scalaxy compilets."
  
LsKeys.ghUser := Some("ochafik")

LsKeys.ghRepo := Some("Scalaxy")

seq(lsSettings: _*)

