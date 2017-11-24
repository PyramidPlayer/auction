package ru.toroptsev.bidder.exception;

/**
 * Base custom exception for bidder fails
 */
public class BidderException extends RuntimeException {

    public BidderException(String message) {
        super(message);
    }
}
