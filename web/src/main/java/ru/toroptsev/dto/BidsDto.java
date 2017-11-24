package ru.toroptsev.dto;

import java.io.Serializable;

/**
 * Data transfer object to inform bidder about bids in the current round
 */
public class BidsDto implements Serializable {

    /**
     * Own bid
     */
    private int ownBid;

    /**
     * Opponent's bid
     */
    private int otherBid;

    public BidsDto() {}

    public BidsDto(int ownBid, int otherBid) {
        this.ownBid = ownBid;
        this.otherBid = otherBid;
    }

    public int getOwnBid() {
        return ownBid;
    }

    public void setOwnBid(int ownBid) {
        this.ownBid = ownBid;
    }

    public int getOtherBid() {
        return otherBid;
    }

    public void setOtherBid(int otherBid) {
        this.otherBid = otherBid;
    }
}
