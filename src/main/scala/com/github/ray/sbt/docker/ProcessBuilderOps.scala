package com.github.ray.sbt.docker

import scala.sys.process
import scala.sys.process.ProcessLogger

class ProcessBuilderOps(builder: process.ProcessBuilder){
  def exec(log: ProcessLogger) = {
    val exitCode = builder.!(log)
    if (exitCode != 0) scala.sys.error("Nonzero exit value: " + exitCode)
  }
}

object ProcessBuilderOps {
  implicit def executeProc(proc: process.ProcessBuilder): ProcessBuilderOps = new ProcessBuilderOps(proc)
}
