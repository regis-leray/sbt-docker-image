import sbt.Keys._
import Dependencies._
import sbt.ScriptedPlugin.autoImport.scriptedLaunchOpts
// Release
import ReleaseTransformations._

lazy val `sbt-docker` = project
  .in(file("."))
  .settings(
    organization := "com.github.regis-leray",
    name := "sbt-docker",
    description := "sbt docker file plugin",
    sbtPlugin := true,

    homepage := Some(url("https://github.com/regis-leray/sbt-docker")),
    scmInfo := Some(ScmInfo(url("https://github.com/regis-leray/sbt-docker"), "git@github.com:regis-leray/sbt-docker.git")),
    developers := List(Developer("username", "Regis Leray", "regis.leray at gmail dot com", url("https://github.com/regis-leray"))),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),

    publishMavenStyle := true,

    scalaVersion := "2.12.6",
    scalacOptions ++= Seq("-encoding", "UTF-8", "-unchecked", "-deprecation", "-feature", "-Xlint", "-Xfatal-warnings"),
    scalacOptions ++= Seq("-Ypartial-unification", "-Ypatmat-exhaust-depth", "30"),
    scalacOptions ++= Seq("-language:existentials", "-language:reflectiveCalls", "-language:implicitConversions"),

    javacOptions in(Compile, compile) ++= Seq("-source", "1.8", "-target", "1.8"),
    javacOptions in(Compile, doc) := Seq("-source", "1.8"),

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
        releaseStepCommandAndRemaining("test"),
        releaseStepCommandAndRemaining("scripted"),
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        releaseStepCommandAndRemaining("publishSigned"),
        setNextVersion,
        commitNextVersion,
        pushChanges
    ),


    scriptedLaunchOpts := scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    scriptedBufferLog := false
  )
  .settings(libraryDependencies += scalaTest % Test)