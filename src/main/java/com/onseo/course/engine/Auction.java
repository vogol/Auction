package com.onseo.course.engine;

import com.onseo.course.common.Lot;
import com.onseo.course.common.LotDescription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by VOgol on 17.11.2016.
 */
public interface Auction {
    LotDescription getLot(long waitMillis) throws InterruptedException;

    void bid(LotDescription lot, String name, int value) throws InterruptedException;

    void pass(LotDescription lot, String name) throws InterruptedException;

    boolean isActive();
}
