package com.onseo.course.bot;

import com.onseo.course.common.LotDescription;
import com.onseo.course.util.RNG;

/**
 * Created by VOgol on 17.11.2016.
 */
public class SimpleBotLogic implements BotLogic {
    private final int maxValue;

    public SimpleBotLogic(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public Integer calcBid(LotDescription lot) {
        if (lot.getCurrentBid().getValue() >= maxValue) {
            return null;
        }

        return RNG.randomFromInterval(lot.getCurrentBid().getValue(), maxValue);
    }
}
