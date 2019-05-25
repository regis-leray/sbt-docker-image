
version := "0.1"
name := "my-name"
enablePlugins(DockerPlugin)

dockerTagNamespace := Some("toto")
dockerBuildOptions := Seq("--target mytarget")
dockerPushOptions := Seq("--disable-content-trust")
dockerOptions := Seq("--tlsverify")

TaskKey[Unit]("checkDockerBuildWithOptions") := {
  if (dockerBuildCmd.value.startsWith("docker --tlsverify build --target mytarget -t toto/my-name:0.1 -f") == false) sys.error("unexpected docker build")
  ()
}

TaskKey[Unit]("checkDockerPushWithOptions") := {
  streams.value.log.info(dockerPushCmd.value)
  if (dockerPushCmd.value.startsWith("docker --tlsverify push --disable-content-trust toto/my-name:0.1") == false) sys.error("unexpected docker push")
  ()
}
