package com.onseo.course.bot;

import com.onseo.course.common.Lot.LotDescription;
import com.onseo.course.util.RNG;

/**
 * Created by VOgol on 17.11.2016.
 */
public class SimpleIncLogic extends BotLogic {
    private final int delta;

    public SimpleIncLogic(int delta) {
        this.delta = delta;
    }

    @Override
    protected Integer getNextBid(LotDescription lot, BotInfo botInfo) throws OutOfMoneyException {
        int bid = lot.getCurrentBid().getValue() + delta;
        if (bid > botInfo.getMoney()) {
            return botInfo.getMoney();
        } else {
            return bid;
        }
    }
}
