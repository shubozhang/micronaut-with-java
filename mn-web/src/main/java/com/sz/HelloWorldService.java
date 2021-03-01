package com.sz;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class HelloWorldService {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldService.class);

    @Value("${hello.service.greeting.value}")
    private String greeting;

    @Property(name="hello.service.greeting.property", defaultValue = "US")
    private String country;

    public String getValue() {
        return greeting;
    }

    public String getProperty() {
        return country;
    }

    @EventListener
    public void onStartup(StartupEvent startupEvent) {
        log.debug("Startup: {}", HelloWorldService.class.getSimpleName());
    }
}
