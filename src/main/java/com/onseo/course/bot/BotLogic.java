package com.onseo.course.bot;

import com.onseo.course.common.Lot.LotDescription;

/**
 * Created by VOgol on 17.11.2016.
 */
public interface BotLogic {
    Integer calcBid(LotDescription lot) throws OutOfMoneyException;
}
