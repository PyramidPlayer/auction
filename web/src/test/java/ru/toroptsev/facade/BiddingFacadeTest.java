package ru.toroptsev.facade;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.toroptsev.dao.BiddersPseudoDAO;
import ru.toroptsev.exception.BidderNotFoundException;

import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BiddingFacade.class, BiddersPseudoDAO.class})
@WebAppConfiguration
public class BiddingFacadeTest {

    @Autowired
    private BiddingFacade facade;

    @Test
    public void testInit() {
        long id = facade.init(10, 100);
        Assert.assertThat(id, greaterThan(0L));
    }

    @Test
    public void testPlaceBid() throws BidderNotFoundException {
        long id = facade.init(10, 100);
        int bid = facade.placeBid(id);
        Assert.assertThat(bid, allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(100)));
    }

    @Test(expected = BidderNotFoundException.class)
    public void testNonexistancePlaceBid() throws BidderNotFoundException {
        facade.placeBid(0);
    }

    @Test
    public void testBids() throws BidderNotFoundException {
        long id = facade.init(10, 100);
        int bid = facade.placeBid(id);
        facade.bids(id, bid, 0);
    }

    @Test(expected = BidderNotFoundException.class)
    public void testNonexistanceBids() throws BidderNotFoundException {
        facade.bids(0, 0, 0);
    }
}
