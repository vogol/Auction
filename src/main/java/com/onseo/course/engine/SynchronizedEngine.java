package com.onseo.course.engine;

import com.onseo.course.common.Bid;
import com.onseo.course.common.Lot;
import com.onseo.course.common.Lot.LotDescription;
import com.onseo.course.history.History;
import com.onseo.course.history.HistoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by VOgol on 17.11.2016.
 */
public class SynchronizedEngine extends Auction {
    private static final Logger log = LoggerFactory.getLogger(SynchronizedEngine.class);

    public SynchronizedEngine(Lot lot) {
        super(lot);
    }

    @Override
    public synchronized LotDescription getLot() {
        return lot.getDecription();
    }

    @Override
    public synchronized void bid(LotDescription lotDescription, Bid bid) {
        log.trace("Got bid {} on {}", bid, lotDescription.getName());
        if (!active.get()) {
            log.warn("Bid {} discarded - Auction is finished", bid);
            return;
        }

        Lot lot = findLot(lotDescription.getName());

        if (lot == null) {
            log.warn("Invalid lot ({}) in bid {}", lotDescription.getName(), bid);
            return;
        }

        bidsCounter.incrementAndGet();

        boolean bidStatus = lot.updateBid(bid);

        history.add(new HistoryItem(bid, bidStatus));

        if (bidStatus) {
            log.debug("New highest bid: {}", bid);
        }
    }

    @Override
    public synchronized Collection<HistoryItem> getHistory() {
        viewsCounter.incrementAndGet();
        return history.get();
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public void waitForStart() throws InterruptedException {
        latch.await();
    }
}
