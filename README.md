# sbt-docker

sbt-docker is a thin wrapper over docker cli, for managing docker image 

[![CircleCI](https://circleci.com/gh/regis-leray/sbt-docker/tree/master.svg?style=svg)](https://circleci.com/gh/regis-leray/sbt-docker/tree/master)
[![codecov](https://codecov.io/gh/regis-leray/sbt-docker/branch/master/graph/badge.svg)](https://codecov.io/gh/regis-leray/sbt-docker)


Requirements
------------
* Sbt 
* Docker Cli

Setup
-----

Add sbt-docker as a dependency in `project/plugins.sbt`:
```scala
addSbtPlugin("com.github.regis-leray" % "sbt-docker" % "0.6.0")
```
in your `build.sbt` need to activate manually the plugin for each project

```scala
lazy val root = project.in(file("."))
  .enablePlugins(DockerPlugin)
```

sbt-docker is an auto plugin, this means that sbt version 0.13.5 or higher is required.
the only dependency is on docker cli, need to be available in your PATH follow instructions [here](https://docs.docker.com/v17.09/engine/installation/)

Docker command
--------------

* docker [OPTIONS] image push [ARGS]
* docker [OPTIONS] image build [ARGS]
* docker [OPTIONS] image tag [ARGS]
* docker [OPTIONS] image rm [ARGS]


At any time you can provide docker [OPTIONS] by overriding `dockerOptions` property


### Build an image

To build an image use the `dockerBuild` task 

Simply run `sbt dockerBuild` from your prompt or `dockerBuild` in the sbt console.

The Dockerfile should be located at the base directory of you project `./Dockerfile` 


```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(   
      //override default tag  :: default `${organization.value}/${name.value}:${version.value}`
      dockerTagNames := Seq("org.me/hello:1.0"),
      //default name of dockerfile :: default `Dockerfile`
      dockerfileName := "Dockerfile-custom"
      //provide OPTIONS :: default `Nil`
      dockerOptions := Seq("--tlsverify ")     
      //provide build OPTIONS :: default `Nil`
      dockerBuildOptions := Seq("--no-cache"),
      //default location of dockerfile :: default `baseDirectory`
      dockerBuildContextPath := baseDirectory.value.asPath.resolve("docker-dir"),      
   )
  .enablePlugins(DockerPlugin)
```

if you need to support private(google cloud / quai.io) repo only override `dockerTagNames`

```scala
lazy val root = project.in(file(".")) 
 .settings(dockerTagNames := Seq(s"quai.io/${name.value}:${version.value}"))
 .enablePlugins(DockerPlugin)
```


By default we are tagging the builded image (`build -t`) by using `$organization/$name:$version` as default docker image tag name, 
you can override, or add multiple tags by overriding `dockerBuildTags` or only override tag versions

```scala
lazy val root = project.in(file(".")) 
 .settings(dockerTagNames += s"quai.io/${name.value}:latest")
 .enablePlugins(DockerPlugin)
```

More informations here for the build [options](https://docs.docker.com/engine/reference/commandline/build/)

### Tag an image

Tag an existing docker image with the `dockerTag` task.

As source tag name we are using `dockerTagNames.head`

It is required to override `dockerTagTargetImages` to provide docker target tag name,

```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      //provide push OPTIONS :: default `Nil`
      dockerTagTargetImages := Seq("org.me/hello:latest")
   )
  .enablePlugins(DockerPlugin)
```

### Remove an image

Remove a docker image from local registry with the `dockerRmi` task.
By default we are removing all the build images define by the property `dockerTagNames` but you can override them with `dockerRmiImages`


```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      //provide push OPTIONS :: default `Nil`
      dockerRmiOptions := Seq("--no-prune"),
      //default value `dockerTagNames`
      dockerRmiImages := Seq("org.me/hello:1.0")
   )
  .enablePlugins(DockerPlugin)
```


### Pushing an image

An image that have already been built can be pushed with the `dockerPush` task.

To push an image use the `dockerPush` task.

By default `dockerPush` will push your docker image tags define by the `dockerBuildTags` property but you can still override

`dockerBuildTags` key is used to determine which image names to push

```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      //provide push OPTIONS :: default `Nil`
      dockerPushOptions := Seq("--disable-content-trust")
   )
  .enablePlugins(DockerPlugin)
```

With docker push you can override only `dockerPushTags`, if you need to have more control 


> You need to be authenticated by using `docker login -u $DOCKER_ID_USER`, if not you can't push docker images
> 
> https://docs.docker.com/docker-cloud/builds/push-images/

### Building & Pushing an image

To build and push an image use the `dockerBuildAndPush` task.
