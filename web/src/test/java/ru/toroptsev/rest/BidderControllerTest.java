package ru.toroptsev.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.toroptsev.dto.BidDto;
import ru.toroptsev.dto.BidderIdDto;
import ru.toroptsev.dto.BidderInitDto;
import ru.toroptsev.dto.BidsDto;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BidderControllerTest {

    private static final String BIDDER_INIT_URL = "/bidder/init";
    private static final String PLACE_BID_URL = "/bidder/place-bid?id=";
    private static final String BIDS_URL = "/bidder/bids?id=";

    @Autowired
    private MockMvc mvc;

    @Test
    public void postInit() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BidderInitDto initDto = new BidderInitDto(10, 100);
        String initJsonContent = objectMapper.writeValueAsString(initDto);

        mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(initJsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", greaterThan(0)));
    }

    @Test
    public void postInitWithBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BidderInitDto initDto = new BidderInitDto(0, 100);
        String jsonContent = objectMapper.writeValueAsString(initDto);

        mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Production quantity should be more than 0"));
    }

    @Test
    public void postInitEmptyBody() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getPlaceBid() throws Exception {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String initJsonContent = objectMapper.writeValueAsString(initDto);

        MvcResult initResult = mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(initJsonContent))
                .andExpect(status().isOk())
                .andReturn();

        String initResultJson = initResult.getResponse().getContentAsString();
        long bidderId = objectMapper.readValue(initResultJson, BidderIdDto.class).getId();

        mvc.perform(MockMvcRequestBuilders.get(PLACE_BID_URL + bidderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("bid", allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(100))));
    }

    @Test
    public void getPlaceBidWithNonexistenceBidder() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(PLACE_BID_URL + 0)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Bidder with id 0 not found"));
    }

    @Test
    public void getPlaceBidTwice() throws Exception {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String initJsonContent = objectMapper.writeValueAsString(initDto);

        MvcResult initResult = mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(initJsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String initResultJson = initResult.getResponse().getContentAsString();
        long bidderId = objectMapper.readValue(initResultJson, BidderIdDto.class).getId();

        mvc.perform(MockMvcRequestBuilders.get(PLACE_BID_URL + bidderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.get(PLACE_BID_URL + bidderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Bidder have already placed bid but round results were not shown"));
    }

    @Test
    public void postBids() throws Exception {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String initJsonContent = objectMapper.writeValueAsString(initDto);

        MvcResult initResult = mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(initJsonContent))
                .andExpect(status().isOk())
                .andReturn();

        String initResultJson = initResult.getResponse().getContentAsString();
        long bidderId = objectMapper.readValue(initResultJson, BidderIdDto.class).getId();

        MvcResult placeBidResult = mvc.perform(MockMvcRequestBuilders.get(PLACE_BID_URL + bidderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = placeBidResult.getResponse().getContentAsString();
        int bid = objectMapper.readValue(responseJson, BidDto.class).getBid();
        BidsDto bidsDto = new BidsDto(bid, 0);
        String bidsJsonContent = objectMapper.writeValueAsString(bidsDto);

        mvc.perform(MockMvcRequestBuilders.post(BIDS_URL + bidderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bidsJsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void postBidsTwice() throws Exception {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String initJsonContent = objectMapper.writeValueAsString(initDto);

        MvcResult initResult = mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(initJsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String initResultJson = initResult.getResponse().getContentAsString();
        long bidderId = objectMapper.readValue(initResultJson, BidderIdDto.class).getId();

        MvcResult placeBidResult = mvc.perform(MockMvcRequestBuilders.get(PLACE_BID_URL + bidderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String placeBidJson = placeBidResult.getResponse().getContentAsString();
        int bid = objectMapper.readValue(placeBidJson, BidDto.class).getBid();
        BidsDto bidsDto = new BidsDto(bid, 0);
        String bidsJsonContent = objectMapper.writeValueAsString(bidsDto);

        mvc.perform(MockMvcRequestBuilders.post(BIDS_URL + bidderId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(bidsJsonContent))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post(BIDS_URL + bidderId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(bidsJsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Bidder hasn't placed bid"));
    }

    @Test
    public void postBidsWithDifferentOwnBid() throws Exception {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String initJsonContent = objectMapper.writeValueAsString(initDto);

        MvcResult initResult = mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(initJsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String initResultJson = initResult.getResponse().getContentAsString();
        long bidderId = objectMapper.readValue(initResultJson, BidderIdDto.class).getId();

        MvcResult placeBidResult = mvc.perform(MockMvcRequestBuilders.get(PLACE_BID_URL + bidderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String placeBidJson = placeBidResult.getResponse().getContentAsString();
        int bid = objectMapper.readValue(placeBidJson, BidDto.class).getBid();
        BidsDto bidsDto = new BidsDto(bid + 1, 0);
        String bidsJsonContent = objectMapper.writeValueAsString(bidsDto);

        mvc.perform(MockMvcRequestBuilders.post(BIDS_URL + bidderId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(bidsJsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Own bid differs from the actually placed bid"));
    }

    @Test
    public void postBidsWithVeryHighOtherBid() throws Exception {
        BidderInitDto initDto = new BidderInitDto(10, 100);
        ObjectMapper objectMapper = new ObjectMapper();
        String initJsonContent = objectMapper.writeValueAsString(initDto);

        MvcResult initResult = mvc.perform(MockMvcRequestBuilders.post(BIDDER_INIT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(initJsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String initResultJson = initResult.getResponse().getContentAsString();
        long bidderId = objectMapper.readValue(initResultJson, BidderIdDto.class).getId();

        MvcResult placeBidResult = mvc.perform(MockMvcRequestBuilders.get(PLACE_BID_URL + bidderId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String placeBidJson = placeBidResult.getResponse().getContentAsString();
        int bid = objectMapper.readValue(placeBidJson, BidDto.class).getBid();
        BidsDto bidsDto = new BidsDto(bid, 101);
        String bidsJsonContent = objectMapper.writeValueAsString(bidsDto);

        mvc.perform(MockMvcRequestBuilders.post(BIDS_URL + bidderId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(bidsJsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Other bid more than his current cash limit"));

    }
}
