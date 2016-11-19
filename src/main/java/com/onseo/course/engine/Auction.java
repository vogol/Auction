package com.onseo.course.engine;

import com.onseo.course.common.Bid;
import com.onseo.course.common.Lot.LotDescription;

/**
 * Created by VOgol on 17.11.2016.
 */
public interface Auction {
    LotDescription getLot();

    void bid(LotDescription lotDescription, Bid bid);

    boolean isActive();

    void waitForStart() throws InterruptedException;
}
