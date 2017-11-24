package ru.toroptsev.bidder.exception;

/**
 * Custom exception that is thrown when negative or zero product quantity is passed for Bidder
 */
public class InvalidBidderQuantityException extends BidderException {

    public InvalidBidderQuantityException(String message) {
        super(message);
    }
}
