# sbt-scalaxy

Sbt plugin to help use and define [Scalaxy](http://github.com/ochafik/Scalaxy) compilets.

# Usage

`sbt-scalaxy` currently requires [sbt](http://www.scala-sbt.org/) 0.12.x. 

If you're happily using [paulp's sbt script](https://github.com/paulp/sbt-extras) (as you probably should), please make sure you have the following line in `project/build.properties`:

    sbt.version=0.12.1

Put the following in `project/plugins.sbt`:

    resolvers += Resolver.sonatypeRepo("snapshots")
    
    addSbtPlugin("com.nativelibs4java" % "sbt-scalaxy" % "0.3-SNAPSHOT")

And the following in `build.sbt`:

	scalaVersion := "2.10.0-RC1"

	// If any library dependency includes a compilet, it will be automatically detected and used.
	// If this is not set, compilets must be added explicitly with:
	//
	//     addCompilets("com.nativelibs4java" %% "some-other-compilets" % "1.0-SNAPSHOT")
	//
	autoCompilets := true
	
	// Enable Scalaxy's basic loop & numerics rewrites.
	addDefaultCompilets()
	
	scalaxySettings
