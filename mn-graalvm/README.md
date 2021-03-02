# mn-graalvm

This is a POC to prove Micronaut + GraalVM in startup time and memory footprint.

### Create an app via Micronaut template
* version: 2.3.3
* language: Java 11
* build: gradle
* test: jUnit


### Build native-image via GraalVM (Dockerfile)
* add lib to build.gradle (or select native-image packaging in creation)
* add src/main/resources/META-INF/native-image/native-image.properties
* modify Dockerfile
* use `@Introspected` in pojo, since graalVM is Ahead Of Time (AOT) that does not use reflection.
* to build
    * `./gradlew clean assemble`
    * `docker build . -t mn-graalvm`

### Build raw docker image 
Rename Dockerfile.raw to Dockerfile and then build.
