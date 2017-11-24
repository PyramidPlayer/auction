package ru.toroptsev.bidder;

import auction.Bidder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by toroptsev on 14.11.2017.
 */
class AuctioneerTest {

    @Test
    void testAuctionWithSmallQuantity() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Auctioneer(new PredictedBidder(), new MirrorBidder(),0, 100),
                "Auctioneer accepts small quantity");
        assertEquals("Production quantity should be more than 0", e.getMessage());
    }

    @Test
    void testAuctioneerWithNegativeQuantity() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Auctioneer(new CunningBidder(), new MedianBidder(), -5, 100),
                "Auctioneer accepts negative quantity");
        assertEquals("Production quantity should be more than 0", e.getMessage());
    }

    @Test
    void testAuctioneerWithSmallCash() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Auctioneer(new AdvancedBidder(), new CunningBidder(), 16, 0),
                "Auctioneer accepts zero cash limit");
        assertEquals("Cash limit should be more than 0", e.getMessage());
    }

    @Test
    void testAuctioneerWithNegativeCash() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Auctioneer(new PredictedBidder(), new ConsoleBidder(),16, -100),
                "Auctioneer accepts negative cash limit");
        assertEquals("Cash limit should be more than 0", e.getMessage());
    }

    @Test
    void testAuctioneerWithFirstNullBidder() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Auctioneer(null, new CunningBidder(),16, 100),
                "Auctioneer accepts first null bidder");
        assertEquals("Bidder can not be null", e.getMessage());
    }

    @Test
    void testAuctioneerWithSecondNullBidder() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Auctioneer(new MirrorBidder(), null,33, 100),
                "Auctioneer accepts second null bidder");
        assertEquals("Bidder can not be null", e.getMessage());
    }

    @Test
    void testRunAuction() {
        MedianBidder bidder1 = new MedianBidder();
        MirrorBidder bidder2 = new MirrorBidder();
        Auctioneer auctioneer = new Auctioneer(bidder1, bidder2,100, 1000);
        Bidder winnerBidder = auctioneer.runAuction();
        Bidder expectedWinnerBidder = null;
        if (bidder1.getOwnQuantity() > bidder2.getOwnQuantity())
            expectedWinnerBidder = bidder1;
        else if (bidder1.getOwnQuantity() < bidder2.getOwnQuantity())
            expectedWinnerBidder = bidder2;
        else if (bidder1.getOwnCash() > bidder2.getOwnCash())
            expectedWinnerBidder = bidder1;
        else if (bidder1.getOwnCash() < bidder2.getOwnCash())
            expectedWinnerBidder = bidder2;
        assertSame(expectedWinnerBidder, winnerBidder);
    }
}
