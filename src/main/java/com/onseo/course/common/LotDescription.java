package com.onseo.course.common;

/**
 * Created by VOgol on 17.11.2016.
 */
public interface LotDescription {
    String getName();
    long getTimePassed();
    long getTimeLeft();
    Bid getCurrentBid();
}
