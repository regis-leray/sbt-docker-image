
version := "0.1"
name := "my-name"
enablePlugins(DockerPlugin)

dockerTagNames := Seq("org.me/my-name:0.1", "org.me/my-name:latest")
dockerBuildOptions := Seq("--target mytarget")
dockerPushOptions := Seq("--disable-content-trust")
dockerOptions := Seq("--tlsverify")
dockerTagTargetImage := Seq("com.company/my-app:0.1", "com.company/my-app:latest")


TaskKey[Unit]("checkDockerBuildWithOptions") := {
  if (dockerBuildCmd.value.startsWith("docker --tlsverify build --target mytarget -t org.me/my-name:0.1 -t org.me/my-name:latest -f") == false) sys.error(s"unexpected value = ${dockerBuildCmd.value}")
  ()
}

TaskKey[Unit]("checkDockerPushWithOptions") := {
  if (dockerPushCmd.value.head.startsWith("docker --tlsverify push --disable-content-trust org.me/my-name:0.1") == false) sys.error(s"unexpected value = ${dockerPushCmd.value.head}")
  if (dockerPushCmd.value.last.startsWith("docker --tlsverify push --disable-content-trust org.me/my-name:latest")  == false) sys.error(s"unexpected value = ${dockerPushCmd.value.last}")
  ()
}

TaskKey[Unit]("checkTagCmdWithOptions") := {
  if (dockerTagCmd.value.head.startsWith("docker --tlsverify tag org.me/my-name:0.1 com.company/my-app:0.1") == false) sys.error(s"unexpected value = ${dockerTagCmd.value.head}")
  if (dockerTagCmd.value.last.startsWith("docker --tlsverify tag org.me/my-name:0.1 com.company/my-app:latest") == false) sys.error(s"unexpected value = ${dockerTagCmd.value.last}")
  ()
}
