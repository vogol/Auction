package com.onseo.course.engine;

import com.onseo.course.common.Lot;
import com.onseo.course.common.LotDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by VOgol on 17.11.2016.
 */
public class SingleLotEngine implements Auction {
    private static final Logger log = LoggerFactory.getLogger(SingleLotEngine.class);

    private final Lot lot;
    private final BlockingQueue<Lot> lotQueue = new ArrayBlockingQueue<Lot>(1);
    private final AtomicBoolean active = new AtomicBoolean(false);

    public SingleLotEngine(Lot lot) {
        this.lot = lot;
    }

    public void start() throws InterruptedException {
        active.set(true);
        lot.activate();
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.schedule(() -> this.finish(), lot.getDuration(), TimeUnit.SECONDS);

        lotQueue.put(lot);
    }

    private void finish() {
        active.set(false);
        log.info("Auction on Lot {} is finished. Winner: {} with bid: {}",
                lot.getName(), lot.getCurrentBid().getBidder(), lot.getCurrentBid().getValue());
    }

    @Override
    public LotDescription getLot(long waitMillis) throws InterruptedException {
        return lotQueue.poll(waitMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void bid(LotDescription lotDescription, String name, int value) throws InterruptedException {
        log.info("{} bid {} on {}", name, value, lotDescription.getName());
        Lot lot = findLot(lotDescription.getName());

        if (lot == null) {
            log.warn("Invalid lot ({}) in bid from {}", lotDescription.getName(), name);
            return;
        }

        lot.updateBid(value, name);
        lotQueue.put(lot);
    }

    @Override
    public void pass(LotDescription lotDescription, String name) throws InterruptedException {
//        log.info("{} pass", name);
        Lot lot = findLot(lotDescription.getName());

        if (lot == null) {
            log.warn("Invalid lot ({}) in bid from {}", lotDescription.getName(), name);
            return;
        }

        lotQueue.put(lot);
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    private Lot findLot(String name) {
        if (lot.getName().equals(name)) {
            return lot;
        }

        return null;
    }
}
