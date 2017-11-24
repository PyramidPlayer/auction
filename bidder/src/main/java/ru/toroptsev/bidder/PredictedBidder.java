package ru.toroptsev.bidder;

/**
 * This bidder strategy is used for default case when cash limit more than quantity of product
 */
public class PredictedBidder extends BaseBidderImpl {

    @Override
    public int nextBid() {
        // first round
        if (getOwnQuantity() == 0 && getOtherQuantity() == 0)
            return saveMoneyBid();

        // take out rest products
        if (getOtherCash() == 0)
            return 1;

        int restRoundsToWin = getInitialQuantity() / 2 + 1 - getOwnQuantity();

        if (restRoundsToWin == 1) {
            if (getOwnCash() > getOtherCash())
                return getOtherCash() + 1;
            else if (getOwnCash() == getOtherCash())
                return getOwnCash();
            else
                return saveMoneyBid();
        }

        // save maximum money
        if (restRoundsToWin == 0)
            return 0;

        int winningBid = getOwnCash() / restRoundsToWin;

        // add random behavior
        if (getRandom().nextInt(100) > 80) // 50%
            return getBidWithVariance(winningBid, 3);

        // If we have an advantage of quantity then we should save money
        if (getOwnQuantity() > getOtherQuantity()) {
            if (getOwnCash() > getOtherCash())
                return saveMoneyBid();
            else
                return 0;
        }

        // If our and opponent's quantity are equal
        // then we will bid slightly more than averaged by opponent's cash divided by rest quantity
        if (getOwnQuantity() == getOtherQuantity()) {
            return getBidWithVariance(winningBid, 2);
        }

        int opponentRestRoundsToWin = getInitialQuantity() / 2 + 1 - getOtherQuantity();

        // save money
        if (opponentRestRoundsToWin == 1)
            return saveMoneyBid();

        // save maximum money
        if (getOwnCash() <= getOtherCash())
            return 0;

        // In other cases, when our quantity less than opponent's quantity
        // then we will bid averaged by opponent's cash divided by rest quantity
        int wonRoundsDifference = getOtherQuantity() - getOwnQuantity();
        int cashDifference = getOwnCash() - getOtherCash();
        return getBidWithVariance(cashDifference / wonRoundsDifference - 1, 1);
    }

    /**
     * Random bid to make it difficult for the opponent to my strategy
     * Small enough for save money
     * @return small random bid between 0 and quarter of average bid
     */
    private int saveMoneyBid() {
        int bound = getInitialCash() / getInitialQuantity();
        return getRandom().nextInt(bound + 1) + 1;
    }

//        private int

    /**
     * Random bid with a given mean value and deviation
     * @param mean
     *                  approximate bid that we are going to place
     * @param deviation
     *                  deviation from the mean value
     * @return random value in range of mean +/- deviation
     */
    private int getBidWithVariance(int mean, int deviation) {
        return mean - deviation + getRandom().nextInt(deviation * 2 + 1);
    }

}
