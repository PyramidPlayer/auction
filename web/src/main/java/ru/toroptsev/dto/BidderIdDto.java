package ru.toroptsev.dto;

import java.io.Serializable;

/**
 * Data transfer object for return identifier of created bidder
 */
public class BidderIdDto implements Serializable {

    /**
     * Bidder identifier
     */
    private long id;

    public BidderIdDto() {}

    public BidderIdDto(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BidderIdDto that = (BidderIdDto) o;
        return id == that.id;
    }
}
