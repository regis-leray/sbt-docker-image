import sbt.Keys._
import Dependencies._
import sbt.ScriptedPlugin.autoImport.scriptedLaunchOpts
// Release
import ReleaseTransformations._

lazy val `sbt-docker-image` = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    organization := "com.github.regis-leray",
    name := "sbt-docker-image",
    description := "sbt docker image plugin",
    sbtPlugin := true,
    crossSbtVersions := Vector("1.2.8", "0.13.18"),
    releaseCrossBuild := true,

    coverageHighlighting := false,

    homepage := Some(url("https://github.com/regis-leray/sbt-docker-image")),
    scmInfo := Some(ScmInfo(url("https://github.com/regis-leray/sbt-docker-image"), "git@github.com:regis-leray/sbt-docker-image.git")),
    developers := List(Developer("username", "Regis Leray", "regis.leray at gmail dot com", url("https://github.com/regis-leray"))),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),

    publishMavenStyle := true,

    scalaVersion := "2.12.8",
    scalacOptions ++= Seq("-encoding", "UTF-8", "-unchecked", "-deprecation", "-feature", "-Xlint", "-Xfatal-warnings"),
    scalacOptions ++= Seq("-language:reflectiveCalls", "-language:implicitConversions"),

    javacOptions in(Compile, compile) ++= Seq("-source", "1.8", "-target", "1.8"),
    javacOptions in(Compile, doc) := Seq("-source", "1.8"),

    credentials += Credentials( Path.userHome / ".sbt" / "sonatype_credential" ),

    publishTo := {
      if (isSnapshot.value)
        Some(Opts.resolver.sonatypeSnapshots)
      else
        Some(Opts.resolver.sonatypeStaging)
    },

    releasePublishArtifactsAction := PgpKeys.publishSigned.value,

    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      releaseStepCommandAndRemaining("^ compile"),
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("^ publishSigned"),
      setNextVersion,
      commitNextVersion,
      releaseStepCommand("^ sonatypeReleaseAll"),
      pushChanges
    ),
  
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },

    scriptedBufferLog := false
  )
  .settings(
    libraryDependencies += scalaTest % Test    
  )
