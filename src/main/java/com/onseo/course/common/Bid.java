package com.onseo.course.common;

import lombok.Data;

import java.util.UUID;

/**
 * Created by VOgol on 15.11.2016.
 */
@Data
public class Bid {
    private final int value;
    private final String bidder;
}
