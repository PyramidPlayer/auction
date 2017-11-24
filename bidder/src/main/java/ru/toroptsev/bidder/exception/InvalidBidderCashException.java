package ru.toroptsev.bidder.exception;

/**
 * Custom exception that is thrown when negative or zero cash limit is passed for Bidder
 */
public class InvalidBidderCashException extends BidderException {

    public InvalidBidderCashException(String message) {
        super(message);
    }
}
