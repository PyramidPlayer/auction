package ru.toroptsev.dao;

import auction.Bidder;

/**
 * Interface for storage and retrieving stored bidders
 */
public interface BiddersDAO {

    long save(Bidder bidder);

    Bidder findById(long id);
}
