package ru.toroptsev.bidder;


import auction.Bidder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for inner auction testing
 */
class Auctioneer {

    private Logger logger = LoggerFactory.getLogger(Auctioneer.class);

    private Bidder bidder1;
    private Bidder bidder2;
    private int quantity;
    private int cash;

    /**
     * Constructs new auctioneer with 2 bidders
     * @param bidder1 - first bidder
     * @param bidder2 - second bidder
     * @param quantity - product quantity
     * @param cash -
     */
    Auctioneer(Bidder bidder1, Bidder bidder2, int quantity, int cash) {
        if (bidder1 == null || bidder2 == null)
            throw new IllegalArgumentException("Bidder can not be null");

        if (quantity <= 0)
            throw new IllegalArgumentException("Production quantity should be more than 0");

        if (cash <= 0)
            throw new IllegalArgumentException("Cash limit should be more than 0");

        this.bidder1 = bidder1;
        this.bidder2 = bidder2;
        this.bidder1.init(quantity, cash);
        this.bidder2.init(quantity, cash);
        this.quantity = quantity;
        this.cash = cash;
    }

    /**
     * Run auction
     * @return winner bidder
     */
    Bidder runAuction() {
        int bidder1quantity = 0;
        int bidder2quantity = 0;
        int bidder1cash = cash;
        int bidder2cash = cash;
        while (quantity >= 2) {
            int bid1 = bidder1.placeBid();
            bidder1cash -= bid1;
            logger.debug("{} Bid = {}", bidder1, bid1);
            int bid2 = bidder2.placeBid();
            bidder2cash -= bid2;
            logger.debug("{} bid = {}", bidder2, bid2);

            if (bid1 > bid2) {
                bidder1quantity += 2;
                logger.debug("Bidder {} won", bidder1);
            } else if (bid1 == bid2) {
                bidder1quantity++;
                bidder2quantity++;
                logger.debug("Draw");
            } else {
                bidder2quantity += 2;
                logger.debug("Bidder {} won", bidder2);
            }

            bidder1.bids(bid1, bid2);
            bidder2.bids(bid2, bid1);
            quantity -= 2;
            logger.debug("Rest quantity = {}", quantity);
        }

        Bidder winner = null;
        if (bidder1quantity > bidder2quantity) {
            winner = bidder1;
            logger.info("Bidder {} won {} by quantity: {} > {}", bidder1, bidder2, bidder1quantity, bidder2quantity);
        } else if (bidder1quantity < bidder2quantity) {
            winner = bidder2;
            logger.info("Bidder {} won {} by quantity: {} < {}", bidder2, bidder1, bidder1quantity, bidder2quantity);
        } else {
            logger.info("Equal quantity: {}", bidder1quantity);
            if (bidder1cash > bidder2cash) {
                winner = bidder1;
                logger.info("Bidder {} won {} by cash: {} > {}", bidder1, bidder2, bidder1cash, bidder2cash);
            } else if (bidder1cash == bidder2cash) {
                logger.info("Absolutely draw between {} and {}", bidder1, bidder2);
            } else {
                winner = bidder2;
                logger.info("Bidder {} won {} by cash: {} < {}", bidder2, bidder1, bidder1cash, bidder2cash);
            }
        }

        return winner;
    }
}
