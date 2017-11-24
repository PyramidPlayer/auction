package ru.toroptsev.bidder.exception;

/**
 * Custom exception that is thrown when bidder call sequence is broken
 */
public class InvalidBidderStateException extends BidderException {

    public InvalidBidderStateException(String message) {
        super(message);
    }
}
