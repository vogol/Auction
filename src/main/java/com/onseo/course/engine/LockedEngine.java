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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by VOgol on 17.11.2016.
 */
public abstract class LockedEngine extends Auction {
    private static final Logger log = LoggerFactory.getLogger(LockedEngine.class);

    protected final ReadWriteLock lotLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock historyLock = new ReentrantReadWriteLock();

    public LockedEngine(Lot lot) {
        super(lot);
    }

    @Override
    public LotDescription getLot() {
        getLotLock().readLock().lock();
        try {
            return lot.getDecription();
        } finally {
            getLotLock().readLock().unlock();
        }
    }

    @Override
    public void bid(LotDescription lotDescription, Bid bid) {
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

        boolean bidStatus;
        getLotLock().writeLock().lock();
        try {
            bidStatus = lot.updateBid(bid);

            getHistoryLock().writeLock().lock();
            try {
                history.add(new HistoryItem(bid, bidStatus));
            } finally {
                getHistoryLock().writeLock().unlock();
            }
        } finally {
            getLotLock().writeLock().unlock();
        }
        if (bidStatus) {
            log.debug("New highest bid: {}", bid);
        }
    }

    @Override
    public Collection<HistoryItem> getHistory() {
        getHistoryLock().readLock().lock();
        try {
            viewsCounter.incrementAndGet();
            return history.get();
        } finally {
            getHistoryLock().readLock().unlock();
        }
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public void waitForStart() throws InterruptedException {
        latch.await();
    }

    protected abstract ReadWriteLock getLotLock();

    protected abstract ReadWriteLock getHistoryLock();
}
