package ru.toroptsev.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.toroptsev.bidder.exception.BidderException;
import ru.toroptsev.dto.*;
import ru.toroptsev.exception.BidderNotFoundException;
import ru.toroptsev.facade.BiddingFacade;


@RestController
@RequestMapping("/bidder")
public class BidderController {

    private Logger logger = LoggerFactory.getLogger(BidderController.class);

    @Autowired
    private BiddingFacade biddingFacade;

    @RequestMapping(value = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> init() {
        return ResponseEntity.ok("Use /bidder/init to initialize");
    }
    
    @RequestMapping(value = "/init",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> init(@RequestBody BidderInitDto dto) {
        try {
            long id = biddingFacade.init(dto.getQuantity(), dto.getCash());
            return ResponseEntity.ok(new BidderIdDto(id));
        } catch (BidderException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorDto(e));
        } catch (Exception e) {
            logger.error("Failed to init bidder", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/place-bid",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> placeBid(@RequestParam("id") long id) {
        try {
            int bid = biddingFacade.placeBid(id);
            return ResponseEntity.ok(new BidDto(bid));
        } catch (BidderNotFoundException e) {
            String errorMsg = String.format("Bidder with id %d not found", id);
            logger.error(errorMsg);
            return ResponseEntity.badRequest().body(new ErrorDto(errorMsg));
        } catch (BidderException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorDto(e));
        } catch (Exception e) {
            logger.error("Failed to place bid", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(value = "/bids",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> bids(@RequestParam("id") long id, @RequestBody BidsDto dto) {
        try {
            biddingFacade.bids(id, dto.getOwnBid(), dto.getOtherBid());
            return ResponseEntity.ok().build();
        } catch (BidderNotFoundException e) {
            String errorMsg = String.format("Bidder with id %d not found", id);
            logger.error(errorMsg);
            return ResponseEntity.badRequest().body(new ErrorDto(errorMsg));
        } catch (BidderException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorDto(e));
        } catch (Exception e) {
            logger.error("Failed to call bids", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
