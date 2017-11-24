package ru.toroptsev.bidder;

/**
 * App for game with human
 */
class ConsoleApp {

    public static void main( String[] args ) {
        Auctioneer auctioneer = new Auctioneer(new ConsoleBidder(), new AdvancedBidder(), 20, 100);
        auctioneer.runAuction();
    }
}
