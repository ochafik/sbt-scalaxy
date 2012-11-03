//package scalaxy

import sbt._
import Keys._
import java.io.File
import scala.collection.mutable
import scala.io.Source
import Project.Initialize

/*
autoCompilets := true

addCompilet("com.nativelibs4java" %% "custom-compilets-example" % "1.0-SNAPSHOT")

scalaxySettings
*/
object Plugin extends sbt.Plugin {
  val autoCompilets = SettingKey[Boolean]("autoCompilets", "Whether compilets should be automatically detected in library dependencies.")
  val scalaxyVersion = SettingKey[String]("scalaxyVersion", "Version of Scalaxy to use.")
	
	def findCompilets(classpath: Seq[File]): Seq[File] = {
		for (file <- classpath) yield {
		  import java.util.zip._
		  try {
		    val zf = new ZipFile(file)
		    try {
		      if (zf.getEntry("META-INF/services/scalaxy.Compilet") != null)
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
	  libraryDependencies <+= scalaxyVersion(sv => compilerPlugin("com.nativelibs4java" %% "scalaxy-compilets" % sv))
	  
  def getCompilets(report: UpdateReport, deps: Seq[File], auto: Boolean) =
    findCompilets(
      ((report matching configurationFilter(Configurations.CompilerPlugin.name)): Seq[File]) ++
      (if (auto) deps else Seq())
    )

	lazy val scalaxySettings = Seq(
	  scalaxyVersion := "0.3-SNAPSHOT",
	  autoCompilerPlugins <<= autoCompilerPlugins or autoCompilets,
	  resolvers += Resolver.sonatypeRepo("snapshots"),
	  libraryDependencies <+= scalaxyVersion(sv => compilerPlugin("com.nativelibs4java" %% "scalaxy-plugin" % sv)),
		scalacOptions <<= (scalacOptions, autoCompilets, dependencyClasspath in Compile, update) map { (options, autoC, deps, report) =>
			options ++ getCompilets(report, deps.files, autoC).map("-Xplugin:" + _.getAbsolutePath)
		}
	)
}
