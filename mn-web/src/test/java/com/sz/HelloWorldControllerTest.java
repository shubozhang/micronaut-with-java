package com.sz;

import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class HelloWorldControllerTest {

    @Inject
    EmbeddedApplication application;

    @Inject
    @Client("/hello")
    RxHttpClient client;

    @Test
    void testIndex() {
        final String result = client.toBlocking().retrieve("/");
        assertEquals("hello world", result);
        assertNotEquals("hello", result);
    }

    @Test
    void testInjectValue() {
        final String result = client.toBlocking().retrieve("/inject_value");
        assertEquals("say hi from service", result);
    }

    @Test
    void testInjectProperty() {
        final String result = client.toBlocking().retrieve("/inject_property");
        assertEquals("UN", result);
    }

    @Test
    void testGetDe(){
        final String result = client.toBlocking().retrieve("/de");
        assertEquals("Hallo", result);
    }

    @Test
    void testGetEn(){
        final String result = client.toBlocking().retrieve("/en");
        assertEquals("Hello", result);
    }
}