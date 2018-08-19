# sbt-docker

sbt-docker is an sbt plugin that builds and pushes Docker images by using `Dockerfile`.


Requirements
------------
* Sbt
* Docker

Setup
-----

Add sbt-docker as a dependency in `project/plugins.sbt`:
```scala
addSbtPlugin("com.github.regis-leray" % "sbt-docker" % "0.1.0")
```
in your `build.sbt` need to activate manually the plugin for each project

```scala
lazy val root = project.in(file(".")) 
  .enablePlugins(DockerPlugin)
```

> Don't forget to export your `DOCKER_ID_USER` as environment variable, do be able to build and push docker image
> You can override `dockerIdUserName` to provide it
> 
> https://docs.docker.com/docker-cloud/builds/push-images/

sbt-docker is an auto plugin, this means that sbt version 0.13.5 or higher is required.

### Building an image

To build an image use the `dockerBuild` task.

Simply run `sbt dockerBuild` from your prompt or `dockerBuild` in the sbt console.

The Dockerfile should be located at the base directory of you project `./Dockerfile` 

```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      //override default dockerIdUserName :: default `sys.env.get("DOCKER_ID_USER")`     
      dockerIdUserName := "toto",
      //override default tag  :: default `$dockerIdUserName/$name:$version`
      dockerTag := "quai.io/mycompany/hello:1.0",     
      //provide build OPTIONS :: default `Nil`
      dockerBuildOptions := Seq("--no-cache"),
      //default location of dockerfile :: default `baseDirectory`
      dockerContextPath := baseDirectory.value.asPath,
      //default name of dockerfile :: default `Dockerfile`
      dockerfileName := "Dockerfile-custom"
   )
  .enablePlugins(DockerPlugin)
```

### Pushing an image

An image that have already been built can be pushed with the `dockerPush` task.

To push an image use the `dockerPush` task.

By default `dockerPush` will push your docker image build to dockerhub

`dockerTag` key is used to determine which image names to push.

```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      //provide push OPTIONS :: default `Nil`
      dockerPushOptions := Seq("--disable-content-trust"),
      //override default tag `$dockerIdUserName/$name:$version`
      dockerTag := "quai.io/mycompany/hello:1.0", 
   )
  .enablePlugins(DockerPlugin)
```

> You need to be authenticated by using `docker login -u $DOCKER_ID_USER`, if not you can't push docker images
> 
> https://docs.docker.com/docker-cloud/builds/push-images/

### Building & Pushing an image

To build and push an image use the `dockerBuildAndPush` task.