
version := "0.1"
name := "my-name"
enablePlugins(DockerPlugin)
dockerTagNamespace := Some("toto")

TaskKey[Unit]("checkBuildCmd") := {
  if (dockerBuildCmd.value.startsWith("docker build -t toto/my-name:0.1 -f") == false) sys.error("unexpected docker build with tag")
  ()
}

TaskKey[Unit]("checkPushCmd") := {
  if (dockerPushCmd.value.startsWith("docker push toto/my-name:0.1") == false) sys.error("unexpected docker push")
  ()
}