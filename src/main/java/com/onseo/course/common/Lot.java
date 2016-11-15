package com.onseo.course.common;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by VOgol on 15.11.2016.
 */
@Data
public class Lot {
    private static final Logger log = LoggerFactory.getLogger(Lot.class);

    private final String name;
    private final long finishTime;
    private int bid;
    private String highestBidder;

    /**
     * Not thread safe
     */
    public boolean updateBid(int bid, String bidder) {
        if (bid > this.bid) {
            log.info("Lot{}: New highest bidder: {}", name, highestBidder);

            this.bid = bid;
            this.highestBidder = bidder;

            return true;
        }

        return false;
    }
}
