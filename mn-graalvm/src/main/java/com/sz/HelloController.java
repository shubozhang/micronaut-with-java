package com.sz;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/hello")
public class HelloController {

    @Get("/")
    public String index() {
        return "hello from Micronaut + GraalVM";
    }
}
