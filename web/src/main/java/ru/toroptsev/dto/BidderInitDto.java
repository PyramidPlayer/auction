package ru.toroptsev.dto;

import java.io.Serializable;

/**
 * Data transfer object for new bidder initialization
 */
public class BidderInitDto implements Serializable {

    /**
     * Quantity of product in auction
     */
    private int quantity;

    /**
     * Cash limit in auction
     */
    private int cash;

    public BidderInitDto() {}

    public BidderInitDto(int quantity, int cash) {
        this.quantity = quantity;
        this.cash = cash;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }
}
