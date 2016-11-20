package com.onseo.course.viewer;

import com.onseo.course.history.HistoryItem;
import com.onseo.course.engine.Auction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by vlad on 11/20/16.
 */
public class Viewer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Viewer.class);

    private final String name;
    private final Auction auction;


    public Viewer(String name, Auction auction) {
        this.name = name;
        this.auction = auction;
    }

    @Override
    public void run() {
        try {
            auction.waitForStart();
            while (auction.isActive()) {
                auction.getLot();
                Collection<HistoryItem> history = auction.getHistory();
            }
        } catch (InterruptedException e) {
            log.warn("Interrupted", e);

            Thread.currentThread().interrupt();
        }

        log.debug("Viewer {} finished his work", name);
    }
}
