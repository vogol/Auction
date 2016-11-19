package com.onseo.course.bot;

import com.onseo.course.common.Bid;
import com.onseo.course.common.Lot.LotDescription;
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
                auction.waitForStart();

                while (auction.isActive()) {
                    LotDescription lot = auction.getLot();
                    if (lot != null) {
                        log.trace("{} got lot: {}", name, lot);

                        if (!lot.getCurrentBid().getBidder().equals(name)) {
                            Integer bidValue = logic.calcBid(lot);

                            if (bidValue != null) {
                                Bid bid = new Bid(bidValue, name);
                                auction.bid(lot, bid);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (OutOfMoneyException e) {
                log.debug("Bot {}: Out of money", name);
            }

            log.debug("Bot {} finished his work", name);
        });
    }
}
