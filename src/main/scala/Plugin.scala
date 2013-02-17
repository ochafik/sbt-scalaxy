package scalaxy

import sbt._
import Keys._
import java.io.File
import scala.collection.mutable
import scala.io.Source
import Project.Initialize

object Plugin extends sbt.Plugin {
  val autoCompilets = SettingKey[Boolean]("autoCompilets", "Whether compilets should be automatically detected in library dependencies.")
  val scalaxyCompilets = SettingKey[Boolean]("scalaxyCompilets", "Whether this project defines compilets, which means it needs Scalaxy compile and test dependencies")
  val scalaxyVersion = SettingKey[String]("scalaxyVersion", "Version of Scalaxy to use.")
  val junitVersion = SettingKey[String]("junitVersion", "Version of JUnit to use.")
  val junitInterfaceVersion = SettingKey[String]("junitInterfaceVersion", "Version of JUnit interface to use.")
  
  def findCompilets(classpath: Seq[File]): Seq[File] = {
    for (file <- classpath) yield {
      import java.util.zip._
      try {
        val zf = new ZipFile(file)
        try {
          if (zf.getEntry("META-INF/services/scalaxy.compilets.Compilet") != null)
            Some(file)
          else
            None
        } finally {
          zf.close()
        }
      } catch { case _: Throwable => None }
    }
  }.flatten
  
  def addCompilets(dependency: ModuleID): Setting[Seq[ModuleID]] =
    addCompilerPlugin(dependency)
    
  def addDefaultCompilets(): Setting[Seq[ModuleID]] =
    libraryDependencies <+= scalaxyVersion(sv => compilerPlugin("com.nativelibs4java" %% "scalaxy-default-compilets" % sv))
    
  def getCompilets(report: UpdateReport, deps: Seq[File], auto: Boolean) =
    findCompilets(
      ((report matching configurationFilter(Configurations.CompilerPlugin.name)): Seq[File]) ++
      (if (auto) deps else Seq())
    )

  override lazy val settings = Seq(
    scalaxyVersion := "0.3-SNAPSHOT",
    scalaxyCompilets := false,
    junitVersion := "4.10",
    junitInterfaceVersion := "0.8",
    autoCompilets := false,
    autoCompilerPlugins <<= autoCompilerPlugins or autoCompilets,
    libraryDependencies <++= (scalaxyVersion, scalaxyCompilets, junitVersion, junitInterfaceVersion) { (sv, sc, jv, jiv) =>
      if (sc)
        Seq(
          "com.nativelibs4java" %% "scalaxy-compilets-api" % sv,
          "com.nativelibs4java" %% "scalaxy-compilets-plugin" % sv % "test" classifier("test"),
          "com.nativelibs4java" %% "scalaxy-compilets-plugin" % sv % "test",
          "junit" % "junit" % jv % "test",
          "com.novocode" % "junit-interface" % jiv % "test")
      else
        Nil
    },
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies <++= (scalaxyVersion, autoCompilets) { (sv, autoC) =>
      if (autoC)//getCompilets(report, deps.files, autoC).isEmpty)
        Seq(compilerPlugin("com.nativelibs4java" %% "scalaxy-compilets-plugin" % sv/* classifier("assembly")*/))
      else
        Nil
    },
    scalacOptions <++= (autoCompilets, dependencyClasspath in Compile, update) map { (autoC, deps, report) => {
      getCompilets(report, deps.files, autoC).map("-Xplugin:" + _.getAbsolutePath)
    }}
  )
}
