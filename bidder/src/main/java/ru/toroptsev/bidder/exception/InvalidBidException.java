package ru.toroptsev.bidder.exception;

/**
 * Custom bidder exception that is thrown when {@link auction.Bidder#bids(int, int)} is called with invalid bids
 */
public class InvalidBidException extends BidderException {

    public InvalidBidException(String message) {
        super(message);
    }
}
