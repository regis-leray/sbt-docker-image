package com.github.ray.sbt.docker

import java.nio.file.{Files, Path}

import sbt.Keys._
import sbt.{Def, _}

import scala.sys.process.Process
import ProcessBuilderOps._

object DockerPlugin extends sbt.AutoPlugin {

  override def trigger: PluginTrigger = noTrigger

  override def projectSettings: Seq[Def.Setting[_]] = packagingSettings

  object autoImport {
    val dockerBuild = taskKey[Unit]("build docker image task")
    val dockerPush = taskKey[Unit]("push docker image task")
    val dockerTag = taskKey[Unit]("tag docker image task")

    val dockerBuildAndPush = taskKey[Unit]("build & push docker image task")

    val dockerfileName = settingKey[String]("docker build file name :: docker build -f")
    val dockerfilePath = settingKey[Path]("docker file path")
    val dockerOptions = settingKey[Seq[String]]("docker options arguments :: docker [OPTIONS] COMMAND [ARG...]")
    val dockerTagNames = settingKey[Seq[String]](" docker tag used during build / push / tag / rmi ")

    val dockerBuildContextPath = settingKey[Path]("docker build context path :: docker build PATH")
    val dockerBuildTags = settingKey[Seq[String]]("docker build -t (tag)")
    val dockerBuildOptions = settingKey[Seq[String]]("docker build options arguments :: docker build [OPTIONS]")
    val dockerBuildCmd = settingKey[String]("do not override")

    val dockerPushTags = settingKey[Seq[String]]("docker push name/tag arguments :: docker push NAME[:TAG]")
    val dockerPushOptions = settingKey[Seq[String]]("docker push options arguments :: docker push [OPTIONS]")
    val dockerPushCmd = settingKey[Seq[String]]("do not override")

    val dockerTagTargetImage = settingKey[Seq[String]]("docker target image")
    val dockerTagCmd = settingKey[Seq[String]]("do not override")
  }

  import autoImport._

  lazy val packagingSettings: Seq[Def.Setting[_]] = Seq[Def.Setting[_]](

    dockerfileName := "Dockerfile",
    dockerfilePath := dockerBuildContextPath.value.resolve(dockerfileName.value),
    dockerOptions := Nil,
    dockerTagNames := Seq(
      s"${Some(organization.value).filter(_.nonEmpty).map(_ + "/").getOrElse("")}${name.value}:${version.value}"
    ),

    dockerBuildContextPath := baseDirectory.value.toPath,
    dockerBuildTags := dockerTagNames.value,
    dockerBuildOptions := Nil,
    dockerBuildCmd := {
      val contextPath = dockerBuildContextPath.value
      val dockerfileFullPath = contextPath.resolve(dockerfileName.value)
      (Seq("docker") ++ dockerOptions.value ++ Seq("build") ++ dockerBuildOptions.value ++ dockerBuildTags.value.map(t => s"-t $t") ++ Seq(s"-f ${dockerfileFullPath.toString} ${contextPath.toString}")).mkString(" ")
    },
    dockerBuild := {
      val log = streams.value.log
      val dockerFile = dockerfilePath.value
      val cmd = dockerBuildCmd.value

      if (Files.exists(dockerFile)) {
        log.info(s"Build docker image :: $cmd")
        Process(cmd).exec(log)
      } else {
        sys.error(s"Docker file not found: ${dockerFile.toString}")
      }
    },

    dockerPushTags := dockerTagNames.value,
    dockerPushOptions := Nil,
    dockerPushCmd := dockerPushTags.value.map(tag => (Seq("docker") ++ dockerOptions.value ++ Seq("push") ++ dockerPushOptions.value ++ Seq(tag)).mkString(" ")),
    dockerPush := {
      val log = streams.value.log

      dockerPushCmd.value.foreach{cmd =>
        log.info(s"Push docker image :: $cmd")
        Process(cmd).exec(log)
      }
    },

    dockerBuildAndPush := {
      dockerBuild.value
      dockerPush.value
    },

    dockerTagTargetImage := Nil,
    dockerTagCmd := {
      val sourceTag = dockerTagNames.value.headOption.getOrElse("`dockerTagNames property is empty`")
      dockerTagTargetImage.value.map(targetTag => (Seq("docker") ++ dockerOptions.value ++ Seq("tag") ++ Seq(sourceTag, targetTag)).mkString(" "))
    },
    dockerTag := {
      val log = streams.value.log

      if(dockerTagTargetImage.value.isEmpty){
        sys.error("Need to override `dockerTagTargetImage`")
      }

      dockerTagCmd.value.foreach{cmd =>
        log.info(s"Tag docker image :: $cmd")
        Process(cmd).exec(log)
      }
    }
  )
}
