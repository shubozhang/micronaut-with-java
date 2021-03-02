package com.sz.broker;

import com.sz.broker.model.Symbol;
import com.sz.broker.store.InMemoryStore;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Controller("/markets")
public class MarketsController {

    @Inject
    private InMemoryStore store;

    @Get("/")
    public List<Symbol> all() {
        return new ArrayList<>();
    }
}
