package com.sz;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        final ApplicationContext context = Micronaut.run(Application.class, args);

        // example: get a bean from context and invoke a method at start
        runAfterStart(context);

        // shutdown application
        // shutDown(context);
    }


    private static void runAfterStart(ApplicationContext context) {
        final HelloWorldService helloWorldService = context.getBean(HelloWorldService.class);
        System.out.println(helloWorldService.getValue());
    }


    private static void shutDown(ApplicationContext context) {
        context.close();
    }
}
