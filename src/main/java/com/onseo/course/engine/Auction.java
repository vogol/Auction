package com.onseo.course.engine;

import com.onseo.course.common.Bid;
import com.onseo.course.common.HistoryItem;
import com.onseo.course.common.Lot.LotDescription;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Created by VOgol on 17.11.2016.
 */
public interface Auction {
    CompletableFuture<AuctionResult> start();

    LotDescription getLot();

    void bid(LotDescription lotDescription, Bid bid);

    boolean isActive();

    void waitForStart() throws InterruptedException;

    Collection<HistoryItem> getHistory();
}
