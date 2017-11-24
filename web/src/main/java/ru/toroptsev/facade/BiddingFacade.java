package ru.toroptsev.facade;

import auction.Bidder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.toroptsev.bidder.AdvancedBidder;
import ru.toroptsev.dao.BiddersDAO;
import ru.toroptsev.exception.BidderNotFoundException;

/**
 * Service for bidders control
 */
@Service
public class BiddingFacade {

    @Autowired
    private BiddersDAO dao;

    /**
     * Initializes new bidder
     * @param quantity - product quantity
     * @param cash - cash limit
     * @return identifier of created bidder
     */
    public long init(int quantity, int cash) {
        Bidder bidder = new AdvancedBidder();
        bidder.init(quantity, cash);
        return dao.save(bidder);
    }

    /**
     * Places next bid
     * @param id - identifier of bidder
     * @return bid for current round
     * @throws BidderNotFoundException if bidder with the passed id doesn't exist
     */
    public int placeBid(long id) throws BidderNotFoundException {
        Bidder bidder = dao.findById(id);
        if (bidder == null)
            throw new BidderNotFoundException();

        return bidder.placeBid();
    }

    /**
     * Informs the bidder about current round bids
     * @param id - identifier of bidder
     * @param ownBid - bid of this bidder
     * @param otherBid - opponent's bid
     * @throws BidderNotFoundException if bidder with the passed id doesn't exist
     */
    public void bids(long id, int ownBid, int otherBid) throws BidderNotFoundException {
        Bidder bidder = dao.findById(id);
        if (bidder == null)
            throw new BidderNotFoundException();

        bidder.bids(ownBid, otherBid);
    }
}
