package com.onseo.course.bot;

import com.onseo.course.common.Lot.LotDescription;
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
    public Integer calcBid(LotDescription lot) throws OutOfMoneyException {
        if (lot.getCurrentBid().getValue() >= maxValue) {
            throw new OutOfMoneyException();
        }

        return RNG.randomFromInterval(lot.getCurrentBid().getValue(), maxValue);
    }
}
