package com.onseo.course;

import com.onseo.course.bot.Bot;
import com.onseo.course.bot.SimpleBotLogic;
import com.onseo.course.common.Lot;
import com.onseo.course.engine.SingleLotEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class AuctionSimulator
{
    private static final Logger log = LoggerFactory.getLogger(AuctionSimulator.class);


    ExecutorService executorService = Executors.newFixedThreadPool(100);

    private void initBots(int amount, SingleLotEngine auction) {
        SimpleBotLogic simpleBotLogic = new SimpleBotLogic(5000);

        for (int i = 0; i < amount; i++) {
            log.info("Init bot with index {}", i);
            Bot bot = new Bot("Bot-" + i, simpleBotLogic);
            bot.run(auction, executorService);
        }
    }

    private void start() {
        try {
            Lot lot = new Lot("First LOT", 5, 100);

            SingleLotEngine auction = new SingleLotEngine(lot);

            initBots(100, auction);

            auction.start();
        } catch (InterruptedException e) {
            log.warn("Auction interrupted", e);
        }
    }

    public static void main( String[] args )
    {
        log.error("!!!");
        AuctionSimulator simulator = new AuctionSimulator();
        simulator.start();
    }
}
