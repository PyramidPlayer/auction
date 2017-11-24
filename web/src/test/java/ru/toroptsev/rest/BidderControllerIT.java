package ru.toroptsev.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.toroptsev.dto.BidDto;
import ru.toroptsev.dto.BidderIdDto;
import ru.toroptsev.dto.BidderInitDto;
import ru.toroptsev.dto.BidsDto;

import java.net.URL;

import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BidderControllerIT {

    private static final String INIT_URL = "bidder/init";
    private static final String PLACE_BID_URL = "bidder/place-bid?id=";
    private static final String BIDS_URL = "bidder/bids?id=";

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
    }

    @Test
    public void testInit() {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ResponseEntity<BidderIdDto> response = template.postForEntity(base.toString() + INIT_URL, initDto, BidderIdDto.class);
        Assert.assertTrue(response.getBody().getId() > 0);
    }

    @Test
    public void testPlaceBid() {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ResponseEntity<BidderIdDto> initResponse = template.postForEntity(base.toString() + INIT_URL, initDto, BidderIdDto.class);
        long bidderId = initResponse.getBody().getId();
        ResponseEntity<BidDto> response = template.getForEntity(base.toString() + PLACE_BID_URL + bidderId, BidDto.class);
        BidDto result = response.getBody();
        Assert.assertTrue(result.getBid() >= 0 && result.getBid() <= 100);
    }

    @Test
    public void testBids() {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ResponseEntity<BidderIdDto> initResponse = template.postForEntity(base.toString() + INIT_URL, initDto, BidderIdDto.class);
        long bidderId = initResponse.getBody().getId();
        ResponseEntity<BidDto> placeBidResponse = template.getForEntity(base.toString() + PLACE_BID_URL + bidderId, BidDto.class);
        int bid = placeBidResponse.getBody().getBid();
        BidsDto bidsDto = new BidsDto(bid, 0);
        ResponseEntity<Object> response = template.postForEntity(base.toString() + BIDS_URL + bidderId, bidsDto, Object.class);
        Assert.assertSame(null, response.getBody());
    }
}
