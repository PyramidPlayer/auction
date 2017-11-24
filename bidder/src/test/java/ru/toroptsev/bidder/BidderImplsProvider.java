package ru.toroptsev.bidder;

import auction.Bidder;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by toroptsev on 14.11.2017.
 */
class BidderImplsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return provideBiddersClasses().stream()
                .map(this::instantiate)
                .map(Arguments::of);
    }

    private List<Class<? extends Bidder>> provideBiddersClasses() {
        return Arrays.asList(
                MedianBidder.class,
                MirrorBidder.class,
                AdvancedBidder.class,
                CunningBidder.class,
                PredictedBidder.class);
    }

    private Bidder instantiate(Class<? extends Bidder> clazz) throws RuntimeException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
