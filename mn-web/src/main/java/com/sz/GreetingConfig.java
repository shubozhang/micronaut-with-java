package com.sz;

import io.micronaut.context.annotation.ConfigurationInject;
import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties("hello.config.greeting")
public class GreetingConfig {

    private final String de;
    private final String en;

    @ConfigurationInject
    public GreetingConfig(@NotBlank final String de, @NotBlank final String en) {
        this.de = de;
        this.en = en;
    }

    public String getDe() {
        return de;
    }

    public String getEn() {
        return en;
    }
}
