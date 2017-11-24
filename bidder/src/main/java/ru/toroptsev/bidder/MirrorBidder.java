package ru.toroptsev.bidder;

/**
 * This bidder use previous opponent's bid with additional random part
 */
public class MirrorBidder extends BaseBidderImpl {

    private int randomSpread;

    @Override
    public void init(int quantity, int cash) {
        super.init(quantity, cash);
        randomSpread = cash / quantity / 2;
    }

    @Override
    protected int nextBid() {
        if (getOwnQuantity() == 0 && getOtherQuantity() == 0) {
            int bidBound = getOwnCash() / getRestQuantity() * 2;
            return getRandom().nextInt(bidBound + 1);
        }

        return getOtherLastBid() + getRandom().nextInt(randomSpread + 1);
    }
}
