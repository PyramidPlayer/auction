package ru.toroptsev.bidder;

import auction.Bidder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.toroptsev.bidder.exception.InvalidBidException;
import ru.toroptsev.bidder.exception.InvalidBidderCashException;
import ru.toroptsev.bidder.exception.InvalidBidderQuantityException;
import ru.toroptsev.bidder.exception.InvalidBidderStateException;

import java.util.Random;

/**
 * Base Bidder class that implements general logic of bidder
 * Inherited classes should implement {@link #nextBid() nextBid} method with individual behavior
 */
abstract class BaseBidderImpl implements Bidder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private int initialQuantity;
    private int initialCash;

    private int restQuantity;

    private int ownQuantity;
    private int otherQuantity;

    private int ownCash;
    private int otherCash;

    private int currentBid;

    private int ownLastBid;
    private int otherLastBid;

    private Boolean readyToPlaceBid;

    private Random random;

    private boolean autoLastBids = true;

    /**
     * Initializes the bidder with the production quantity and the allowed cash limit.
     *
     * @param quantity - the quantity
     * @param cash - the cash limit
     * @throws InvalidBidderStateException when this method is called a second time
     * @throws InvalidBidderQuantityException when product quantity less than 1
     * @throws InvalidBidderCashException when cash limit less than 1
     */
    public void init(int quantity, int cash) {
        if (readyToPlaceBid != null)
            throw new InvalidBidderStateException("Bidder has already been initialized");
        if (quantity <= 0)
            throw new InvalidBidderQuantityException("Production quantity should be more than 0");
        if (cash <= 0)
            throw new InvalidBidderCashException("Cash limit should be more than 0");

        restQuantity = initialQuantity = quantity;
        ownCash = otherCash = initialCash = cash;
        ownQuantity = otherQuantity = 0;

        readyToPlaceBid = true;
        random = new Random();
    }

    /**
     * Retrieves the next bid for the product, which may be zero.
     *
     * @return the next bid
     * @throws InvalidBidderStateException
     *      if {@link #init(int, int)} hasn't been called before
     *      or this method has been called twice in a row
     *      or rest product quantity less than 2
     */
    public int placeBid() {
        if (readyToPlaceBid == null)
            throw new InvalidBidderStateException("Bidder hasn't been initialized");
        if (!readyToPlaceBid)
            throw new InvalidBidderStateException("Bidder have already placed bid but round results were not shown");

        if (restQuantity < 2)
            throw new InvalidBidderStateException("Insufficient quantity units for bidding");

        int nextBid = 0;

        if (!autoLastBids)
            nextBid = nextBid();
        else {
            if (ownCash > 0) {
                if (restQuantity < 4)
                    nextBid = lastBid();
                else if (restQuantity < 6)
                    nextBid = preLastBid();
                else
                    nextBid = nextBid();

                nextBid = getCheckedBid(nextBid);
            }
        }

        logger.debug("{} next bid: {}", this, nextBid);
        currentBid = nextBid;
        readyToPlaceBid = false;
        return nextBid;
    }

    /**
     * Shows the bids of the two bidders.
     *
     * @param own - the bid of this bidder
     * @param other - the bid of the other bidder
     * @throws InvalidBidderStateException
     *      when {@link #init(int, int)} hasn't been called before
     *      or this method has been called twice in a row
     *      or rest product quantity less than 2
     * @throws InvalidBidException
     *      when own bid differs of placed value
     *      or other bid more than opponent's cash limit
     */
    public void bids(int own, int other) {
        if (readyToPlaceBid == null)
            throw new InvalidBidderStateException("Bidder hasn't been initialized");
        if (readyToPlaceBid)
            throw new InvalidBidderStateException("Bidder hasn't placed bid");

        if (currentBid != own)
            throw new InvalidBidException("Own bid differs from the actually placed bid");
        if (other > getOtherCash())
            throw new InvalidBidException("Other bid more than his current cash limit");

        ownLastBid = own;
        otherLastBid = other;

        ownCash -= own;
        otherCash -= other;
        restQuantity -= 2;

        int ownWinning = 0;
        int otherWinning = 0;
        if (own > other) {
            ownWinning = 2;
        } else if (own == other) {
            ownWinning = otherWinning = 1;
        } else {
            otherWinning = 2;
        }
        ownQuantity += ownWinning;
        otherQuantity += otherWinning;
        readyToPlaceBid = true;
        logger.debug(toFullString());
    }

    /**
     * @return bid for specific strategy
     */
    protected abstract int nextBid();

    /**
     * Bid for round previous before last round
     * @return bid amount
     */
    private int preLastBid() {

        if (getOwnCash() > getOtherCash()) {
            // increase advantage
            if (getOwnQuantity() > getOtherQuantity())
                return getOtherCash() + 1;

            // I suppose that when the opponent expects that I want to bid more than his money,
            // he will save it (by 0 bid) for draw in the last round
            if (getOwnQuantity() < getOtherQuantity())
                return Math.max(1, getOwnCash() - getOtherCash() - 1);

            return 0; // economy
        }

        if (getOwnCash() == getOtherCash()) {
            if (getOwnQuantity() > getOtherQuantity())
                return getOwnQuantity();
            if (getOwnQuantity() == getOtherQuantity())
                return 0; // economy
            else
                return 0; // loss
        }

        // ownCash < otherCash
        if (getOwnQuantity() > getOtherQuantity())
            return Math.max(2, getOtherCash() - getOwnCash());

        return 0; // loss
    }

    /**
     * Strategy for last bid is only one true
     * @return bid for the last round
     */
    private int lastBid() {

        // If we have an advantage of quantity and cash - the advantage should be increased
        if (getOwnCash() > getOtherCash() && getOwnQuantity() >= getOtherQuantity())
            return getOtherCash() + 1;

        // We should buy out the remaining product but save money for further draw
        if (getOwnCash() > getOtherCash() && getOwnQuantity() < getOtherQuantity()) {
            // we should save more money than opponent if he will bid 0
            int opponentsZeroBid = Math.max(1, getOwnCash() - getOtherCash() - 1);
            // if opponent calculated my decision, I should increase bid
            int opponentCalculatedMeBid = opponentsZeroBid + 2;
            // use random choose for further confuse the opponent
            return getRandom().nextInt(2) > 0 ? opponentsZeroBid : opponentCalculatedMeBid;
        }

        // If our quantity of product is more or equal to opponent's quantity
        // then most likely there will be a win or draw
        // otherwise there will be a loss. So we should maximize the risk
        if (getOwnCash() == getOtherCash())
            return getOwnCash();

        // Last cases, when we have less cash than the opponent

        // We have an advantage of quantity but money should be saved for draw
        if (getOwnQuantity() > getOtherQuantity()) {
            // more save bid is 0
            int moneySaveBid = 0;
            // but if opponent places small bid he can take catch up for me
            int calculateOpponentBid = Math.max(2, getOtherCash() - getOwnCash());
            // use random choose for further confuse the opponent
            return getRandom().nextInt(2) > 0 ? moneySaveBid : calculateOpponentBid;
        }

        // In other cases this is exactly a loss, so we save rest of cash
        return 0;
    }

    /**
     * Return bid that doesn't exceed current cash limit and more or equal than 0
     * @param desiredBid - planned bid
     * @return own cash - if planned bid exceeds current cash limit then it
     *         0 - if planned bid less than 0 then it
     *         desiredBid - else
     */
    private int getCheckedBid(int desiredBid) {
        if (desiredBid > getOtherCash() + 1)
            desiredBid = getOtherCash() + 1;
        if (desiredBid > getOwnCash())
            return getOwnCash();
        if (desiredBid < 0)
            return 0;
        return desiredBid;
    }

    int getInitialQuantity() {
        return initialQuantity;
    }

    int getInitialCash() {
        return initialCash;
    }

    int getRestQuantity() {
        return restQuantity;
    }

    int getOwnQuantity() {
        return ownQuantity;
    }

    int getOtherQuantity() {
        return otherQuantity;
    }

    int getOwnCash() {
        return ownCash;
    }

    int getOtherCash() {
        return otherCash;
    }

    int getOwnLastBid() {
        return ownLastBid;
    }

    int getOtherLastBid() {
        return otherLastBid;
    }

    protected Random getRandom() {
        return random;
    }

    private String toFullString() {
        return toString() + ": Quantity = " + ownQuantity + "; Cash = " + ownCash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    protected void disableAutoLastBids() {
        this.autoLastBids = false;
    }
}
