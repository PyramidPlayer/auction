package ru.toroptsev.bidder;

/**
 * Inner test implementation of bidder for testing
 * This bidder looks at rest opponent's cash and place average bid for rest rounds plus 1 if it has advantage by cash
 * In other cases it place 0 bid
 */
class CunningBidder extends BaseBidderImpl {

    @Override
    protected int nextBid() {
        if (getOwnCash() < getOtherCash())
            return 0;

        int restRoundsToOpponentsWin = getInitialQuantity() / 2 + 1 - getOtherQuantity();
        if (restRoundsToOpponentsWin == 0)
            return getOtherCash() + 1;

        return getOtherCash() / restRoundsToOpponentsWin + 1;
    }
}
