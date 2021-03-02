package com.sz.broker.store;

import com.sz.broker.model.Symbol;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class InMemoryStore {
    private final List<Symbol> symbols = new ArrayList<>();

    public List<Symbol> getAllSymbols() {
        return  symbols;
    }
}
