version := "0.1"
name := "my-name"

targetImages in DockerImg in tag := Seq("com.company/my-app:latest")

enablePlugins(DockerImagePlugin)