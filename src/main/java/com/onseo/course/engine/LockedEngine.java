package com.onseo.course.engine;

import com.onseo.course.common.Bid;
import com.onseo.course.common.HistoryItem;
import com.onseo.course.common.Lot;
import com.onseo.course.common.Lot.LotDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by VOgol on 17.11.2016.
 */
public class LockedEngine implements Auction {
    private static final Logger log = LoggerFactory.getLogger(LockedEngine.class);

    private final Lot lot;
    //    private final List<HistoryItem> history = new CopyOnWriteArrayList<>();
//    private final List<HistoryItem> history = new LinkedList<>();
    private final Collection<HistoryItem> history = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final CountDownLatch latch = new CountDownLatch(1);
    private final CompletableFuture<AuctionResult> auctionFinishFuture = new CompletableFuture<>();
    private final AtomicInteger bidsCounter = new AtomicInteger(0);
    private final AtomicInteger viewsCounter = new AtomicInteger(0);

    private final ReadWriteLock lotLock = new ReentrantReadWriteLock();
    private final ReadWriteLock historyLock = new ReentrantReadWriteLock();

    public LockedEngine(Lot lot) {
        this.lot = lot;
    }

    public CompletableFuture<AuctionResult> start() {
        log.info("Auction on lot {} is started", lot);

        active.set(true);
        lot.activate();
        latch.countDown();

        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.schedule(this::finish, lot.getDuration(), TimeUnit.SECONDS);

        return auctionFinishFuture;
    }

    private void finish() {
        active.set(false);
        AuctionResult result = new AuctionResult(
                lot.getName(),
                lot.getCurrentBid().getBidder(), lot.getCurrentBid().getValue(),
                history, bidsCounter.get(), viewsCounter.get());

        auctionFinishFuture.complete(result);
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
    public synchronized Collection<HistoryItem> getHistory() {
        getHistoryLock().readLock().lock();
        try {
            viewsCounter.incrementAndGet();
            return new ArrayList<>(history);
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

    private Lot findLot(String name) {
        if (lot.getName().equals(name)) {
            return lot;
        }

        return null;
    }

    private ReadWriteLock getLotLock() {
        return lotLock;
    }

    private ReadWriteLock getHistoryLock() {
        return historyLock;
    }
}