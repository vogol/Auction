package com.onseo.course.util;

import java.util.Random;

/**
 * Created by VOgol on 17.11.2016.
 */
public class RNG {
    private final static Random random = new Random();

    /**
     * Returns random integer from specified interval
     *
     * @param from left bound inclusive
     * @param to   right bound inclusive
     */
    public static int randomFromInterval(int from, int to) {
        return from + random.nextInt(to - from + 1);
    }
}
