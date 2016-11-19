package com.onseo.course.bot;

import com.onseo.course.common.Lot.LotDescription;

/**
 * Created by VOgol on 17.11.2016.
 */
public abstract class BotLogic {

    public Integer calcBid(LotDescription lot, BotInfo botInfo) throws OutOfMoneyException {
        validateMoney(lot, botInfo);

        return getNextBid(lot, botInfo);
    }

    protected abstract Integer getNextBid(LotDescription lot, BotInfo botInfo) throws OutOfMoneyException;

    protected void validateMoney(LotDescription lot, BotInfo botInfo) throws OutOfMoneyException {
        if (lot.getCurrentBid().getValue() >= botInfo.getMoney()) {
            throw new OutOfMoneyException();
        }
    }
}
