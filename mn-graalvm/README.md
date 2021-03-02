# mn-graalvm

### Build native-image via GraalVM
* add lib to build.gradle (or select native-image packaging in creation)
* add src/main/resources/META-INF/native-image/native-image.properties
* modify Dockerfile
* use `@Introspected` in pojo, since graalVM is Ahead Of Time (AOT) that does not use reflection.
* to build
    * `./gradlew clean assemble`
    * `docker build . -t mn-graalvm`
