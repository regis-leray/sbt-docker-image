v0.7.0
======

- rename DockerPlugin as DockerImagePlugin
- rename tasks
 
dockerBuild => dockerImgBuild
dockerPush => dockerImgPush
dockerRmi => dockerImgRm
dockerTag => dockerImgTag

- delete duplicate settings, and provide scope for dockerOptions and dockerCmdOptions 

v0.6.0
======
- support `docker build` with multiple tags (build -t <tag1> -t <tag2>) 
- support `docker image tag` [#15](https://github.com/regis-leray/sbt-docker/issues/15)
- support `docker image rm` [#16](https://github.com/regis-leray/sbt-docker/issues/16)

v0.5.0
======
- add docker options for all commands [#14](https://github.com/regis-leray/sbt-docker/issues/14)

v0.4.0
======
- tag namespace are not mandatory anymore - rename `dockerIdUsername` to `dockerTagNamespace`
- no more buffering when logging output from Process

v0.3.0
======
- support sbt cross version

v0.2.0
======
- add settings key `dockerIdUserName`, by default resolving by from environment variable `DOCKER_ID_USER`

v0.1.0
======

- first release
