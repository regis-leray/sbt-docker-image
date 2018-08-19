version := "0.1"
name := "my-name"
enablePlugins(DockerPlugin)

dockerIdUserName := Some("toto")
dockerBuildOptions := Seq("--target mytarget")
dockerPushOptions := Seq("--disable-content-trust")

TaskKey[Unit]("checkDockerBuildWithOptions") := {
  if (dockerBuildCmd.value.startsWith("docker build --target mytarget -t toto/my-name:0.1 -f") == false) sys.error("unexpected docker build")
  ()
}

TaskKey[Unit]("checkDockerPushWithOptions") := {
  streams.value.log.info(dockerPushCmd.value)
  if (dockerPushCmd.value.startsWith("docker push --disable-content-trust toto/my-name:0.1") == false) sys.error("unexpected docker push")
  ()
}
