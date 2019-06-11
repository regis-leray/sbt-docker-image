# sbt-docker

sbt-docker is a thin wrapper over docker cli, for managing docker image 

[![CircleCI](https://circleci.com/gh/regis-leray/sbt-docker-image/tree/master.svg?style=svg)](https://circleci.com/gh/regis-leray/sbt-docker/tree/master)
[![codecov](https://codecov.io/gh/regis-leray/sbt-docker-image/branch/master/graph/badge.svg)](https://codecov.io/gh/regis-leray/sbt-docker-image)


Requirements
------------
* Sbt 
* Docker Cli

Setup
-----

Add sbt-docker as a dependency in `project/plugins.sbt`:
```scala
addSbtPlugin("com.github.regis-leray" % "sbt-docker-image" % "0.7.0")
```
in your `build.sbt` need to activate manually the plugin for each project

```scala
lazy val root = project.in(file("."))
  .enablePlugins(DockerImagePlugin)
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

Simply run `sbt dockerImg:build` from your prompt or `dockerImg:build` in the sbt console.

The Dockerfile should be located at the base directory of you project `./Dockerfile` 


```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(   
      //override default tag  :: default `${organization.value}/${name.value}:${version.value}`
      tagNames in DockerImg := Seq("org.me/hello:1.0"),
      //default name of dockerfile :: default `Dockerfile`
      dockerfileName in DockerImg := "Dockerfile-custom"
      //provide OPTIONS :: default `Nil`
      dockerOptions in DockerImg := Seq("--tlsverify ")     
      //provide build OPTIONS :: default `Nil`
      dockerCmdOptions in DockerImg in build := Seq("--no-cache"),
      //default location of dockerfile :: default `baseDirectory`
      buildContextPath in DockerImg in build := baseDirectory.value.asPath.resolve("docker-dir"),      
   )
  .enablePlugins(DockerImagePlugin)
```

By default we are tagging the builded image (`build -t`) by using `$organization/$name:$version` as default docker image tag name, 
you can override, or add multiple tags by overriding `tagNames`

```scala
lazy val root = project.in(file(".")) 
 .settings(tagNames in DockerImg in build += s"quai.io/${name.value}:latest")
 .enablePlugins(DockerImagePlugin)
```

More informations here for the build [options](https://docs.docker.com/engine/reference/commandline/build/)

### Tag an image

Tag an existing docker image with the `dockerImg:tag` task.

As source tag name we are using `tagNames.head`

It is required to override `targetImages` to provide docker target tag name,

```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      //provide push OPTIONS :: default `Nil`
      targetImages in DockerImg in tag := Seq("org.me/hello:latest")
   )
  .enablePlugins(DockerImagePlugin)
```

### Remove an image

Remove a docker image from local registry with the `dockerImg:rm` task.
By default we are removing all the build images define by the property `tagNames`


```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      //provide rm OPTIONS :: default `Nil`
      cmdOptions in DockerImg in rm := Seq("--no-prune"),
      //default value `dockerTagNames`
      tagNames in DockerImg in rm := Seq("org.me/hello:1.0")
   )
  .enablePlugins(DockerImagePlugin)
```


### Pushing an image

An image that have already been built can be pushed with the `dockerImg:push` task.

By default `dockerImg:push` will push your docker image tags define by the `tagNames` property but you can still override

`tagNames` key is used to determine which image names to push

```scala
// Example if you need to override keys

lazy val root = project.in(file("."))
  .settings(
      //provide push OPTIONS :: default `Nil`
      cmdOptions in DockerImg in push := Seq("--disable-content-trust"),
      tagNames in DockerImg in push := Seq("org.me/hello:1.0")
   )
  .enablePlugins(DockerImagePlugin)
```

With `docker push` you can specify which image to push by overriding settings `tagNames in DockerImg in push`, it will only apply this settings for the task `push`


> You need to be authenticated by using `docker login -u $DOCKER_ID_USER`, if not you can't push docker images
> 
> https://docs.docker.com/docker-cloud/builds/push-images/
