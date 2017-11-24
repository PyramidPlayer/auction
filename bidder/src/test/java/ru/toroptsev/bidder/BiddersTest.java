package ru.toroptsev.bidder;

import auction.Bidder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import ru.toroptsev.bidder.exception.InvalidBidException;
import ru.toroptsev.bidder.exception.InvalidBidderCashException;
import ru.toroptsev.bidder.exception.InvalidBidderQuantityException;
import ru.toroptsev.bidder.exception.InvalidBidderStateException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests of all bidders implementations
 */
class BiddersTest {

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testDoubleInit(Bidder bidder) {
        bidder.init(10, 100);
        assertThrows(InvalidBidderStateException.class, () -> bidder.init(10, 100),
                "Bidder " + bidder + " allows double initialization");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testInitWithSmallQuantity(Bidder bidder) {
        assertThrows(InvalidBidderQuantityException.class, () -> bidder.init(0, 100),
                "Bidder " + bidder + " accepts small quantity");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testInitWithNegativeQuantity(Bidder bidder) {
        assertThrows(InvalidBidderQuantityException.class, () -> bidder.init(-5, 100),
                "Bidder " + bidder + " accepts negative quantity");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testInitWithSmallCash(Bidder bidder) {
        assertThrows(InvalidBidderCashException.class, () -> bidder.init(16, 0),
                "Bidder " + bidder + " accepts zero cash limit");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testInitWithNegativeCash(Bidder bidder) {
        assertThrows(InvalidBidderCashException.class, () -> bidder.init(16, -100),
                "Bidder " + bidder + " accepts negative cash limit");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testPlaceBidWithoutInit(Bidder bidder) {
        assertThrows(InvalidBidderStateException.class, bidder::placeBid,
                "Bidder " + bidder + " didn't check that it was initialized");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testPlaceBidTwice(Bidder bidder) {
        bidder.init(10, 100);
        bidder.placeBid();
        assertThrows(InvalidBidderStateException.class, bidder::placeBid,
                "Bidder " + bidder + " didn't check that placeBid was called two consecutive times");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testPlaceBidLessThanCash(Bidder bidder) {
        int quantity = 1000;
        int cash = 100000;
        bidder.init(quantity, cash);
        while (quantity >= 2) {
            int bid = bidder.placeBid();
            assertTrue(bid <= cash, "Bidder " + bidder + " placed bid more than his cash limit");
            bidder.bids(bid, 50);
            cash -= bid;
            quantity -= 2;
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testPlaceBidWithInsufficientQuantity(Bidder bidder) {
        bidder.init(3, 100);
        int bid = bidder.placeBid();
        bidder.bids(bid, 100);
        assertThrows(InvalidBidderStateException.class, bidder::placeBid,
                "Bidder " + bidder + " continue place bids after end of auction");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testPlaceBidTwiceWithoutBids(Bidder bidder) {
        bidder.init(10, 100);
        bidder.placeBid();
        assertThrows(InvalidBidderStateException.class, bidder::placeBid,
                "Bidder " + bidder + " continue place bids after end of auction");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testBidsWithDifferentOwnBid(Bidder bidder) {
        bidder.init(10, 100);
        int ownBid = bidder.placeBid();
        assertThrows(InvalidBidException.class, () -> bidder.bids(ownBid + 1, 0),
                "Bidder " + bidder + " doesn't check own bid parameter input");
    }

    @ParameterizedTest
    @ArgumentsSource(BidderImplsProvider.class)
    void testBidsWithInvalidOtherBid(Bidder bidder) {
        bidder.init(10, 100);
        int ownBid = bidder.placeBid();
        assertThrows(InvalidBidException.class, () -> bidder.bids(ownBid, 101),
                "Bidder " + bidder + " doesn't check other bid parameter input");
    }
}
