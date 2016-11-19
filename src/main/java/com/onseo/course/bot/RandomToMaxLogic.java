package com.onseo.course.bot;

import com.onseo.course.common.Lot.LotDescription;
import com.onseo.course.util.RNG;

/**
 * Created by VOgol on 17.11.2016.
 */
public class RandomToMaxLogic extends BotLogic {
    @Override
    protected Integer getNextBid(LotDescription lot, BotInfo botInfo) throws OutOfMoneyException {
        return RNG.randomFromInterval(lot.getCurrentBid().getValue(), botInfo.getMoney());
    }
}
