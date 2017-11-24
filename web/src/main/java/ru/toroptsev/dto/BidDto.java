package ru.toroptsev.dto;

import java.io.Serializable;

/**
 * Data transfer object to place bid
 */
public class BidDto implements Serializable {

    private int bid;

    public BidDto() {}

    public BidDto(int bid) {
        this.bid = bid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }
}
