package com.onseo.course.common;

import com.onseo.course.util.SystemTime;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by VOgol on 15.11.2016.
 */
@Data
public class Lot implements LotDescription {
    private static final Logger log = LoggerFactory.getLogger(Lot.class);

    private final String name;
    private final long duration;
    private long startTime;
    private Deque<Bid> bids = new LinkedList<>();

    public Lot(String name, long duration, int startPrice){
        this.name = name;
        this.duration = duration;
        bids.push(new Bid(startPrice, "start price"));
    }

    public void activate() {
        this.startTime = SystemTime.getTimeMs();
    }

    @Override
    public long getTimePassed() {
        return SystemTime.getTimeMs() - this.startTime;
    }

    @Override
    public long getTimeLeft() {
        return duration - getTimePassed();
    }

    @Override
    public Bid getCurrentBid() {
        return bids.peek();
    }

    public boolean updateBid(int bid, String bidder) {
        if (bid > getCurrentBid().getValue()) {
            log.info("Lot {}: New highest bidder: {}", name, bidder);

            Bid newBid = new Bid(bid, bidder);
            bids.push(newBid);

            return true;
        }

        return false;
    }
}
