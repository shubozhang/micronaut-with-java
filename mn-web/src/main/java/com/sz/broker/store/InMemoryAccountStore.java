package com.sz.broker.store;


import com.sz.broker.model.WatchList;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;

@Singleton
public class InMemoryAccountStore {

    private final HashMap<UUID, WatchList> watchListsPerAccount = new HashMap<>();

    public WatchList getWatchList(final UUID accountId) {
        return watchListsPerAccount.getOrDefault(accountId, new WatchList());
    }

    public WatchList updateWatchList(final UUID accountId, final WatchList watchList) {
        watchListsPerAccount.put(accountId, watchList);
        return getWatchList(accountId);
    }

    public void deleteWatchList(final UUID accountId) {
        watchListsPerAccount.remove(accountId);
    }
}
