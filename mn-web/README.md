# mn-web

### micronaut template via IDEA
* version: 2.3.3
* language: Java 11
* build: gradle
* test: jUnit
* feature: netty-server

### features
* Inject service -- HelloWorldService.java
* Inject @Value and @Property -- HelloWorldService.java
* Inject config -- GreetingConfig
* @EventListener method will be triggered at startup


### test features

```
@Inject
EmbeddedApplication application;

@Inject
@Client("/hello")
RxHttpClient client;
```
