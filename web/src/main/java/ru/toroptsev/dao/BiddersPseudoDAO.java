package ru.toroptsev.dao;

import auction.Bidder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Pseudo repository for DB imitation while application is running
 */
@ApplicationScope
@Repository
public class BiddersPseudoDAO implements BiddersDAO {

    private AtomicLong idCounter = new AtomicLong(1);
    private ConcurrentHashMap<Long, Bidder> biddersStorage = new ConcurrentHashMap<>();

    public long save(Bidder bidder) {
        long id = idCounter.getAndIncrement();
        biddersStorage.put(id, bidder);
        return id;
    }

    public Bidder findById(long id) {
        return biddersStorage.get(id);
    }
}
