package com.onseo.course.engine;

import com.onseo.course.common.Bid;
import com.onseo.course.common.Lot;
import com.onseo.course.history.History;
import com.onseo.course.history.HistoryItem;
import com.onseo.course.common.Lot.LotDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by VOgol on 17.11.2016.
 */
public abstract class Auction {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected final Lot lot;
    protected final History history = new History();
    protected final AtomicBoolean active = new AtomicBoolean(false);
    protected final CountDownLatch latch = new CountDownLatch(1);
    protected final CompletableFuture<AuctionResult> auctionFinishFuture = new CompletableFuture<>();
    protected final AtomicInteger bidsCounter = new AtomicInteger(0);
    protected final AtomicInteger viewsCounter = new AtomicInteger(0);

    private ScheduledExecutorService finishExecutor = new ScheduledThreadPoolExecutor(1);

    protected Auction(Lot lot) {
        this.lot = lot;
    }

    public CompletableFuture<AuctionResult> start() {
        log.info("Auction on lot {} is started", lot);

        active.set(true);
        lot.activate();
        latch.countDown();

        finishExecutor.schedule(this::finish, lot.getDuration(), TimeUnit.SECONDS);

        return auctionFinishFuture;
    }

    public abstract LotDescription getLot();

    public abstract void bid(LotDescription lotDescription, Bid bid);

    public abstract boolean isActive();

    public abstract void waitForStart() throws InterruptedException;

    public abstract Collection<HistoryItem> getHistory();

    public String getDescription() {
        return this.getClass().getSimpleName();
    }

    protected Lot findLot(String name) {
        if (lot.getName().equals(name)) {
            return lot;
        }

        return null;
    }

    private void finish() {
        active.set(false);
        AuctionResult result = new AuctionResult(
                lot.getName(),
                lot.getCurrentBid().getBidder(), lot.getCurrentBid().getValue(),
                history, bidsCounter.get(), viewsCounter.get());

        auctionFinishFuture.complete(result);
        finishExecutor.shutdown();
    }
}
