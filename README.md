# sbt-docker

sbt-docker is an sbt plugin that builds and pushes Docker images by using `Dockerfile`.


Requirements
------------

* sbt
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

sbt-docker is an auto plugin, this means that sbt version 0.13.5 or higher is required.

### Building an image

To build an image use the `dockerBuild` task.

Simply run `sbt dockerBuild` from your prompt or `dockerBuild` in the sbt console.

The Dockerfile should be located at the base directory of you project `./Dockerfile` 
(override key `dockerContextPath` if necessary) 

By default the docker image is using a tag `projectName:projectVersion` (override key `dockerTag` if necessary)

```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      dockerTag := "Hello:1.0",
      dockerBuildOptions := Seq("--no-cache"),
      dockerContextPath := baseDirectory.value.asPath,
      dockerfileName := "Dockerfile"
   )
  .enablePlugins(DockerPlugin)
```

### Pushing an image

An image that have already been built can be pushed with the `dockerPush` task.

To both build and push an image use the `dockerPush` task.

By default `dockerPush` will push your docker image build to dockerhub

`dockerTag` key is used to determine which image names to push.

```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      dockerPushOptions := Nil, 
      dockerTag := "Hello:1.0", 
   )
  .enablePlugins(DockerPlugin)
```

> You need to be authenticated by using `docker login -u $USER -p $PASS`, if not you can't push docker images 
