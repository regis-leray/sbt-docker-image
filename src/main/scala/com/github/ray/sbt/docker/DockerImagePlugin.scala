package com.github.ray.sbt.docker

import java.nio.file.{Files, Path}

import sbt.Keys._
import sbt.{Def, _}

import scala.sys.process.Process
import ProcessBuilderOps._

object DockerImagePlugin extends sbt.AutoPlugin {

  object autoImport {
    lazy val DockerImg = config("dockerImg")

    val build = taskKey[Unit]("docker image build [OPTIONS] PATH | URL | -")
    val push = taskKey[Unit]("docker image push [OPTIONS] NAME[:TAG]")
    val tag = taskKey[Unit]("docker image tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]")
    val rm = taskKey[Unit]("docker image rm [OPTIONS] IMAGE [IMAGE...]")

    val dockerfileName = settingKey[String]("docker build file name :: docker build -f")
    val dockerfilePath = settingKey[Path]("docker file path")
    val dockerOptions = settingKey[Seq[String]]("docker options arguments :: docker [OPTIONS] COMMAND [ARG...]")
    val cmdOptions = settingKey[Seq[String]]("docker command options arguments :: docker COMMAND [OPTIONS]")
    val tagNames = settingKey[Seq[String]](" docker tags for tasks : build / push / tag / rm ")
    val buildContextPath = settingKey[Path]("docker build context path :: docker build PATH")
    val targetImages = taskKey[Seq[String]]("docker tag option :: TARGET_IMAGE[:TAG]")
  }

  import autoImport._

  override def trigger: PluginTrigger = noTrigger

  override def projectSettings: Seq[Def.Setting[_]] = inConfig(DockerImg)(pluginSettings)

  override def projectConfigurations = Seq(DockerImg)

  lazy val pluginSettings: Seq[Def.Setting[_]] = Seq[Def.Setting[_]](
    dockerfileName in DockerImg := "Dockerfile",
    dockerfilePath in DockerImg := buildContextPath.value.resolve(dockerfileName.value),
    dockerOptions in DockerImg := Nil,
    tagNames in DockerImg := Seq(s"${Some(organization.value).filter(_.nonEmpty).map(_ + "/").getOrElse("")}${name.value}:${version.value}"),
    cmdOptions in DockerImg := Nil,
    buildContextPath in DockerImg := baseDirectory.value.toPath,

    fork in DockerImg := true,

    build in DockerImg := {
      val log = streams.value.log
      val dockerFile = dockerfilePath.value

      val contextPath = (buildContextPath in build).value
      val dockerfileFullPath = contextPath.resolve(dockerfileName.value)
      val cmd = (Seq("docker") ++ (dockerOptions in build).value ++ Seq("image build") ++ (cmdOptions in build).value ++ (tagNames in build).value.map(t => s"-t $t") ++ Seq(s"-f ${dockerfileFullPath.toString} ${contextPath.toString}")).mkString(" ")

      if (Files.exists(dockerFile)) {
        log.info(s"docker execute: $cmd")
        Process(cmd).exec(log)
      } else {
        sys.error(s"Docker file not found: ${dockerFile.toString}")
      }
    },

    push in DockerImg := {
      val log = streams.value.log

      (tagNames in push).value
        .map(tag => (Seq("docker") ++ (dockerOptions in push).value ++ Seq("image push") ++ (cmdOptions in push).value ++ Seq(tag)).mkString(" "))
        .foreach { cmd =>
          log.info(s"docker execute: $cmd")
          Process(cmd).exec(log)
        }
    },

    targetImages in DockerImg := sys.error("Need to override `targetImages in DockerImg in tag` property setting"),

    tag in DockerImg := {
      val log = streams.value.log
      val sourceTag = (tagNames in tag).value.headOption.getOrElse(sys.error("dockerTagNames property is empty"))

      (targetImages in tag).value
        .map(targetTag => (Seq("docker") ++ (dockerOptions in tag).value ++ Seq("image tag") ++ Seq(sourceTag, targetTag)).mkString(" "))
        .foreach { cmd =>
          log.info(s"docker execute: $cmd")
          Process(cmd).exec(log)
        }
    },

    rm in DockerImg := {
      val log = streams.value.log
      val cmd = (Seq("docker") ++ (dockerOptions in rm).value ++ Seq("image rm") ++ (cmdOptions in rm).value ++ tagNames.value).mkString(" ")
      log.info(s"docker execute: $cmd")
      Process(cmd).exec(log)
    }
  )
}
