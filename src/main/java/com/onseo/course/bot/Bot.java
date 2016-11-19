package com.onseo.course.bot;

import com.onseo.course.common.LotDescription;
import com.onseo.course.engine.Auction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by VOgol on 17.11.2016.
 */
public class Bot {
    private static final Logger log = LoggerFactory.getLogger(Bot.class);

    private final String name;
    private final BotLogic logic;

    public Bot(String name, BotLogic logic) {
        this.name = name;
        this.logic = logic;
    }

    public void run(Auction auction, ExecutorService executorService) {
        executorService.submit(() -> {
            try {
                do {
                    LotDescription lot = auction.getLot(1000);
                    if (lot != null) {
//                        log.info("{} got lot: {}, value: {}", name, lot.getName(), lot.getCurrentBid());

                        Integer bid = logic.calcBid(lot);

                        if (bid == null) {
                            auction.pass(lot, name);
                        } else {
                            auction.bid(lot, name, bid);
                        }
                    }
                }
                while (auction.isActive());
            } catch (InterruptedException e) {
                log.warn("Bot {} is interrupted", name);
                Thread.currentThread().interrupt();
            }
        });
    }
}
