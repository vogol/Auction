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
 */
public class AuctionSimulator {
    private static final Logger log = LoggerFactory.getLogger(AuctionSimulator.class);


    ExecutorService executorService = Executors.newFixedThreadPool(100);

    private void initBots(int amount, SingleLotEngine auction) {
        SimpleBotLogic simpleBotLogic = new SimpleBotLogic(5000);

        for (int i = 0; i < amount; i++) {
            Bot bot = new Bot("Bot-" + i, simpleBotLogic);
            bot.run(auction, executorService);
        }
        SimpleBotLogic simpleBotLogic2 = new SimpleBotLogic(6000);
        Bot bot = new Bot("Bot-" + 100, simpleBotLogic2);
        bot.run(auction, executorService);
    }

    private void start() {
        Lot lot = new Lot("First LOT", 5, 100);

        SingleLotEngine auction = new SingleLotEngine(lot);

        initBots(10, auction);

        auction.start();
    }

    public static void main(String[] args) {
        AuctionSimulator simulator = new AuctionSimulator();
        simulator.start();
    }
}
