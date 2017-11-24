package ru.toroptsev.bidder;

import auction.Bidder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * App for bidders testing
 */
class CompetitionApp {

    private static Logger logger = LoggerFactory.getLogger(CompetitionApp.class);

    public static void main( String[] args ) throws InstantiationException, IllegalAccessException {
        competeBiddersWithEachOther();
    }

    private static void competeBiddersWithEachOther() throws IllegalAccessException, InstantiationException {
        int[] quantitySet = new int[]{2, 4, 6, 8, 10, 16, 20, 50, 100, 1000};
        int[] cashSet = new int[]{5, 10, 50, 100, 1000, 10000};
        List<Class<? extends Bidder>> biddersClasses = Arrays.asList(MirrorBidder.class, MedianBidder.class,
                AdvancedBidder.class, CunningBidder.class, PredictedBidder.class);

        Map<Class<? extends Bidder>, Integer> winningMap = new HashMap<>();
        for (int quantity : quantitySet) {
            for (int cash : cashSet) {
                for (Class<? extends Bidder> bidder1Class : biddersClasses) {
                    for (Class<? extends Bidder> bidder2Class : biddersClasses) {
                        for (int i = 0; i < 100; i++) {
                            if (bidder1Class == bidder2Class)
                                continue;

                            Bidder bidder1 = bidder1Class.newInstance();
                            Bidder bidder2 = bidder2Class.newInstance();
                            Auctioneer auctioneer = new Auctioneer(bidder1, bidder2, quantity, cash);
                            Bidder winnerBidder = auctioneer.runAuction();
                            if (winnerBidder != null)
                                incrementWinningRate(winningMap, winnerBidder.getClass());
                        }
                    }
                }
            }
        }
        logger.info("Winning map:");
        winningMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> logger.info("{}\t: {}", entry.getKey(), entry.getValue()));
    }

    private static void incrementWinningRate(Map<Class<? extends Bidder>, Integer> winningMap, Class<? extends Bidder> bidderClass) {
        int currentRate;
        if (winningMap.containsKey(bidderClass))
            currentRate = winningMap.get(bidderClass) + 1;
        else
            currentRate = 1;
        winningMap.put(bidderClass, currentRate);
    }
}
