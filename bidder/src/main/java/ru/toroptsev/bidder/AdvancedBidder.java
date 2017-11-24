package ru.toroptsev.bidder;

/**
 * Final version of bidder
 * This bidder use different strategy depending on initial conditions
 */
public class AdvancedBidder extends BaseBidderImpl {

    private BidderStrategy bidderStrategy;

    @Override
    public void init(int quantity, int cash) {
        super.init(quantity, cash);
        int numberOfRounds = quantity / 2;
        if (cash <= numberOfRounds)
            bidderStrategy = new CashLessThanRoundsBidderStrategy();
        else
            bidderStrategy = new AdvancedRandomBidderStrategy();
    }

    @Override
    protected int nextBid() {
        return bidderStrategy.nextBid();
    }

    private interface BidderStrategy {
        int nextBid();
    }

    /**
     * This bidder strategy should be used when the number of rounds exceeds amount of money.
     * It wait for the opponent to spend most of his cash. Then he buy up rest of product with 1 bids
     */
    private class CashLessThanRoundsBidderStrategy implements BidderStrategy {

        @Override
        public int nextBid() {
            int restRounds = getRestQuantity() / 2;
            return getOwnCash() < restRounds ? 0 : 1;
        }
    }

    private class AdvancedRandomBidderStrategy implements BidderStrategy {

        @Override
        public int nextBid() {
            int spread = getInitialCash() / getInitialQuantity() * 2;
            int restWinRounds = getInitialQuantity() / 2 + 1 - getOwnQuantity();
            if (restWinRounds == 0)
                return 0;

            int restRounds = getRestQuantity() / 2;
            int winRounds = restRounds / 2 + 1;
            int winBid = getOwnCash() / winRounds;

            return winBid - spread + getRandom().nextInt(spread * 2 + 1);
        }
    }
}
