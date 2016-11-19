package com.onseo.course.common;

import com.onseo.course.util.SystemTime;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by VOgol on 15.11.2016.
 */
@Data
public class Lot {
    @Data
    public class LotDescription {
        public final String name;
        public final long timePassed;
        public final long timeLeft;
        public final Bid currentBid;
    }

    private static final Logger log = LoggerFactory.getLogger(Lot.class);

    private final String name;
    private final long duration;
    private long startTime;
    private Bid currentBid;

    public Lot(String name, long duration, int startPrice) {
        this.name = name;
        this.duration = duration;
        currentBid = new Bid(startPrice, "start price");
    }

    public void activate() {
        this.startTime = SystemTime.getTimeMs();
    }

    //TODO: try AtomicReference
    public synchronized boolean updateBid(Bid bid) {
        if (bid.getValue() <= currentBid.getValue()) {
            log.trace("Bid {} has smaller value then current {}", bid, currentBid);
            return false;
        }

        currentBid = bid;

        return true;
    }

    public synchronized LotDescription getDecription() {
        long time = SystemTime.getTimeMs();
        long timePassed = time - startTime;
        long timeLeft = duration - timePassed;
        LotDescription lotDescription = new LotDescription(name, timePassed, timeLeft, currentBid);
        return lotDescription;
    }
}
