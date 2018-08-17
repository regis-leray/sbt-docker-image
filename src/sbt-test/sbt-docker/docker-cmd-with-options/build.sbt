version := "0.1"
name := "my-name"
enablePlugins(DockerPlugin)

dockerBuildOptions := Seq("--target mytarget")
dockerPushOptions := Seq("--disable-content-trust")

TaskKey[Unit]("checkDockerBuildWithOptions") := {
  if (dockerBuildCmd.value.startsWith("docker build --target mytarget -t my-name:0.1 -f") == false) sys.error("unexpected docker build")
  ()
}

TaskKey[Unit]("checkDockerPushWithOptions") := {
  streams.value.log.info(dockerPushCmd.value)
  if (dockerPushCmd.value.startsWith("docker push --disable-content-trust my-name:0.1") == false) sys.error("unexpected docker push")
  ()
}
