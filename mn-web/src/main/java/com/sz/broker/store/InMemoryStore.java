package com.sz.broker.store;

import com.sz.broker.model.Quote;
import com.sz.broker.model.Symbol;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class InMemoryStore {
    private final List<Symbol> symbols;

    private static final ThreadLocalRandom localRandom = ThreadLocalRandom.current();
    private final Map<String, Quote> cachedQuotes = new HashMap<>();

    public InMemoryStore() {
        symbols = Stream.of("AAPL", "AMZN", "FB", "GOOG", "MSFT", "NFLX", "TSLA")
                .map(Symbol::new)
                .collect(Collectors.toList());
        symbols.forEach(symbol ->
                cachedQuotes.put(symbol.getValue(), initRandomQuote(symbol))
        );
    }

    public List<Symbol> getAllSymbols() {
        return  symbols;
    }


    public Optional<Quote> fetchQuote(String symbol) {
        return Optional.ofNullable(cachedQuotes.get(symbol));
    }

    public Quote initRandomQuote(final Symbol symbol) {
        return Quote.builder().symbol(symbol).bid(randomValue())
                .ask(randomValue())
                .lastPrice(randomValue())
                .volume(randomValue())
                .build();
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(localRandom.nextDouble(1, 100));
    }

    public void update(final Quote quote) {
        cachedQuotes.put(quote.getSymbol().getValue(), quote);
    }
}
