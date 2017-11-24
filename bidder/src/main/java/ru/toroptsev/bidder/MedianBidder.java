package ru.toroptsev.bidder;

/**
 * Inner test implementation of bidder for testing
 * This bidder place average bid between his and opponent's last bids plus 1
 */
class MedianBidder extends BaseBidderImpl {

    @Override
    protected int nextBid() {
        return (getOwnLastBid() + getOtherLastBid()) / 2 + 1;
    }
}
