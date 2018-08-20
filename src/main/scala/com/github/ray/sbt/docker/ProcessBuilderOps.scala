package com.github.ray.sbt.docker

import scala.sys.process

class ProcessBuilderOps(builder: process.ProcessBuilder){
  def exec(log: sbt.Logger): Unit = {
    val exitCode = builder.run(new scala.sys.process.ProcessLogger {
      override def out(s: => String): Unit = log.info(s)
      override def err(s: => String): Unit = log.error(s)
      override def buffer[T](f: => T): T = log.buffer(f)
    }).exitValue()

    if (exitCode != 0) scala.sys.error("Nonzero exit value: " + exitCode)
  }
}

object ProcessBuilderOps {
  implicit def executeProc(proc: process.ProcessBuilder): ProcessBuilderOps = new ProcessBuilderOps(proc)
}
