package com.onseo.course.engine;

import com.onseo.course.common.Bid;
import com.onseo.course.common.HistoryItem;
import com.onseo.course.common.Lot;
import com.onseo.course.common.Lot.LotDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by VOgol on 17.11.2016.
 */
public class SingleLotEngine implements Auction {
    private static final Logger log = LoggerFactory.getLogger(SingleLotEngine.class);

    private final Lot lot;
    private final List<HistoryItem> history = new CopyOnWriteArrayList<>();
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final CountDownLatch latch = new CountDownLatch(1);

    public SingleLotEngine(Lot lot) {
        this.lot = lot;
    }

    public void start() {
        log.info("Auction on lot {} is started", lot);

        active.set(true);
        lot.activate();
        latch.countDown();

        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.schedule(this::finish, lot.getDuration(), TimeUnit.SECONDS);
    }

    private void finish() {
        active.set(false);
        log.info("Auction on Lot {} is finished. Winner: {} with bid: {}",
                lot.getName(), lot.getCurrentBid().getBidder(), lot.getCurrentBid().getValue());

        printHistory("Full history", history);
        printHistory("Success bids", history.stream().filter(HistoryItem::isSuccess).collect(Collectors.toList()));
    }

    private void printHistory(String title, List<HistoryItem> history) {
        log.info("=== {} ===", title);
        history.forEach(h -> log.info(h.toString()));
    }

    @Override
    public LotDescription getLot()  {
        return lot.getDecription();
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

        boolean bidStatus = lot.updateBid(bid);

        history.add(new HistoryItem(bid, bidStatus));

        if (bidStatus) {
            log.info("New highest bid: {}", bid);
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
}
