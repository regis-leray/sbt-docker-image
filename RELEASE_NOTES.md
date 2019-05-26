v0.6.0
======
- support `docker build` with multiple tags (build -t <tag1> -t <tag2>) 
- support `docker tag` [#15](https://github.com/regis-leray/sbt-docker/issues/15)

v0.5.0
======
- add docker options possibility [#14](https://github.com/regis-leray/sbt-docker/issues/14)

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
