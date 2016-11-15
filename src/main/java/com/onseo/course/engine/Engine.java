package com.onseo.course.engine;

import com.onseo.course.common.Lot;
import com.onseo.course.util.SystemTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by VOgol on 15.11.2016.
 */
public class Engine {
    private static final Logger log = LoggerFactory.getLogger(Engine.class);

    private final Map<String, Lot> activeLots = new HashMap<>();
    private final Map<String, Lot> finishedLots = new HashMap<>();

    public void registerLot(Lot lot) {
        int size;
        Lot previous;

        synchronized (this) {
            previous = activeLots.putIfAbsent(lot.getName(), lot);
            size = activeLots.size();
        }

        if (previous == null) {
            log.info("Engine: new lot registered: {}. Total lots: {}", lot, size);
        } else {
            log.warn("Lot \'{}\' is already registered", lot.getName());
        }
    }

    public boolean bid(String lotKey, int bid, String bidder) {
        synchronized (this) {
            Lot lot = activeLots.get(lotKey);

            if (lot != null) {
                return lot.updateBid(bid, bidder);
            } else {
                log.warn("Lot with key {} is not found", lotKey);
                return false;
            }
        }
    }

    public void tick() {
        long curTime = SystemTime.getTimeMs();
        log.debug("Auction tick, time: {}", curTime);
        Map<String, Lot> finished;

        synchronized (this) {
            finished = activeLots.values().stream()
                    .filter(lot -> lot.getFinishTime() < curTime)
                    .collect(Collectors.toMap(Lot::getName, Function.identity()));

            finished.keySet().forEach(lot -> activeLots.remove(lot));
            finishedLots.putAll(finished);
        }

        finished.keySet().forEach(lot -> {
            log.info("Found finished lot: {}", lot);
        });

    }

    public Set<String> getActiveLots() {
        return activeLots.keySet();
    }

    public String getLotInfo(String lotKey) {
        Lot lot = activeLots.get(lotKey);

        if (lot != null) {
            return lot.toString();
        } else {
            return "No such lot " + lotKey;
        }
    }
}
