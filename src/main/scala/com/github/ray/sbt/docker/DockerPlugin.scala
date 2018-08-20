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
    val dockerBuildAndPush = taskKey[Unit]("build & push docker image task")

    val dockerTagNamespace = settingKey[Option[String]]("docker tag namespace")
    val dockerImageName = settingKey[String]("docker image name")
    val dockerImageVersion = settingKey[String]("docker image version")
    val dockerContextPath = settingKey[Path]("docker build context path :: docker build PATH")
    val dockerTag = settingKey[String]("docker tag")
    val dockerfileName = settingKey[String]("docker build file name :: docker build -f")
    val dockerfilePath = settingKey[Path]("docker file path")
    val dockerBuildOptions = taskKey[Seq[String]]("docker build options arguments :: docker build [OPTIONS]")
    val dockerBuildCmd = taskKey[String]("docker build command")
    val dockerPushOptions = taskKey[Seq[String]]("docker push options arguments :: docker push [OPTIONS]")
    val dockerPushCmd = taskKey[String]("docker push command")
  }

  import autoImport._

  lazy val packagingSettings: Seq[Def.Setting[_]] = Seq[Def.Setting[_]](
    dockerImageName := name.value,
    dockerImageVersion := version.value,
    dockerContextPath := baseDirectory.value.toPath,
    dockerfileName := "Dockerfile",
    dockerfilePath := dockerContextPath.value.resolve(dockerfileName.value),
    dockerTagNamespace := sys.env.get("DOCKER_ID_USER").orElse(sys.env.get("DOCKER_TAG_NAMESPACE")),
    dockerTag := {
      val ns = dockerTagNamespace.value.map(_+"/").getOrElse("")
      s"$ns${dockerImageName.value}:${dockerImageVersion.value}"
    },
    dockerBuildOptions := Nil,
    dockerBuildCmd := {
      val contextPath = dockerContextPath.value
      val dockerfilePath: Path = contextPath.resolve(dockerfileName.value)
      (Seq("docker build") ++ dockerBuildOptions.value ++ Seq(s"-t ${dockerTag.value}", s"-f ${dockerfilePath.toString} ${contextPath.toString}")).mkString(" ")
    },
    dockerPushOptions := Nil,
    dockerPushCmd := (Seq("docker push") ++ dockerPushOptions.value ++ Seq(dockerTag.value)).mkString(" "),

    dockerBuild := {
      val log = streams.value.log
      val dockerFile = dockerfilePath.value
      val cmd = dockerBuildCmd.value

      if (Files.exists(dockerFile)) {
        log.info(s"Build docker image :: ${dockerTag.value}")
        Process(cmd).exec(log)
      } else {
        sys.error(s"Docker file not provided: ${dockerFile.toString}")
      }
    },

    dockerPush := {
      val log = streams.value.log
      log.info(s"Push docker image :: ${dockerTag.value}")
      Process(dockerPushCmd.value).exec(log)
    },

    dockerBuildAndPush := {
      dockerBuild.value
      dockerPush.value
    }
  )
}
