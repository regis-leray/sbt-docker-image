
version := "0.1"
name := "my-name"
enablePlugins(DockerPlugin)
dockerTagNames := Seq("org.me/my-name:0.1")
dockerTagTargetImages := Seq("com.company/my-app:0.1")

TaskKey[Unit]("checkBuildCmd") := {
  if (dockerBuildCmd.value.startsWith("docker image build -t org.me/my-name:0.1 -f") == false) sys.error(s"unexpected value = ${dockerBuildCmd.value}")
  ()
}

TaskKey[Unit]("checkPushCmd") := {
  if (dockerPushCmd.value.mkString.startsWith("docker image push org.me/my-name:0.1") == false) sys.error(s"unexpected value = ${dockerPushCmd.value.mkString}")
  ()
}


TaskKey[Unit]("checkTagCmd") := {
  if (dockerTagCmd.value.mkString.startsWith("docker image tag org.me/my-name:0.1 com.company/my-app:0.1") == false) sys.error(s"unexpected value = ${dockerTagCmd.value.mkString}")
  ()
}

TaskKey[Unit]("checkRmiCmd") := {
  if (dockerRmiCmd.value.startsWith("docker image rm org.me/my-name:0.1") == false) sys.error(s"unexpected value = ${dockerRmiCmd.value.mkString}")
  ()
}