package ru.toroptsev.dao;

import auction.Bidder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.toroptsev.bidder.AdvancedBidder;

public class BiddersPseudoDAOTest {

    private BiddersDAO dao;

    @Before
    public void init() {
        dao = new BiddersPseudoDAO();
    }

    @Test
    public void testAddNewBidder() {
        Bidder bidder = new AdvancedBidder();
        long id = dao.save(bidder);
        Assert.assertSame("Bidder has been saved, but not found", bidder, dao.findById(id));
    }

    @Test
    public void testFindNotExistingBidder() {
        Assert.assertNull("DAO return nonexistent bidder", dao.findById(1));
    }
}
