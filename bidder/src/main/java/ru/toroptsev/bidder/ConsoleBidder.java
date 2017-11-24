package ru.toroptsev.bidder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Bidder that ask next bid from a human in console
 */
class ConsoleBidder extends BaseBidderImpl {

    private Logger logger = LoggerFactory.getLogger(ConsoleBidder.class);

    private Scanner reader;

    @Override
    public void init(int quantity, int cash) {
        super.init(quantity, cash);
        logger.info("Initial conditions: quantity={}; cash={}", quantity, cash);
        reader = new Scanner(System.in);
        disableAutoLastBids();
    }

    @Override
    protected int nextBid() {
        while (true) {
            logger.info("Enter a next bid: ");
            try {
                while (!reader.hasNextInt()) {
                    String nonIntegerValue = reader.next();
                    if ("quit".equals(nonIntegerValue) || "exit".equals(nonIntegerValue)) {
                        logger.info("Game over");
                        System.exit(0);
                    }
                    logger.warn("This is not number");
                }

                int bid = reader.nextInt();
                if (bid > getOwnCash()) {
                    logger.warn("Your bid more than current cash limit: {} > {}", bid, getOwnCash());
                    continue;
                }
                return bid;
            } catch (NoSuchElementException e) {
                logger.error("Wrong input: " + e.getMessage());
                reader.next();
            }
        }
    }

    @Override
    public void bids(int own, int other) {
        super.bids(own, other);
        logger.info("Opponent bids {}", other);
        logger.info("Rest product quantity: {}; Own quantity: {}; Other quantity: {}; Own cash: {}; Other cash: {}",
                getRestQuantity(), getOwnQuantity(), getOtherQuantity(), getOwnCash(), getOtherCash());
    }
}
