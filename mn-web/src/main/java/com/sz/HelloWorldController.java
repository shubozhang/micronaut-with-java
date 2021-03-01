package com.sz;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import javax.inject.Inject;

@Controller("${hello.controller.path}")
public class HelloWorldController {

    @Inject
    private HelloWorldService helloWorldService;

    @Inject
    private GreetingConfig greetingConfig;

    @Get("/")
    public String index(){
        return "hello world";
    }

    @Get("/inject_value")
    public String injectValue(){
        return helloWorldService.getValue();
    }

    @Get("/inject_property")
    public String injectProperty(){
        return helloWorldService.getProperty();
    }

    @Get("/de")
    public String getDe() {
        return greetingConfig.getDe();
    }

    @Get("/en")
    public String geten() {
        return greetingConfig.getEn();
    }
}
