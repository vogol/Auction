package com.onseo.course;

import com.onseo.course.bot.Bot;
import com.onseo.course.bot.BotLogic;
import com.onseo.course.bot.SimpleIncLogic;
import com.onseo.course.common.HistoryItem;
import com.onseo.course.common.Lot;
import com.onseo.course.engine.AuctionResult;
import com.onseo.course.engine.SingleLotEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class AuctionSimulator {
    private static final Logger log = LoggerFactory.getLogger(AuctionSimulator.class);

    private static final int BOTS_COUNT = 100;
    private static final int BOTS_THREADS_COUNT = 100;
    private static final int BOTS_MONEY = 1_000_000;

    ExecutorService executorService = Executors.newFixedThreadPool(BOTS_THREADS_COUNT);

    private void initBots(int amount, SingleLotEngine auction) {
        BotLogic randomToMaxLogic = new SimpleIncLogic(1);

        for (int i = 0; i < amount; i++) {
            Bot bot = new Bot("Bot-" + i, BOTS_MONEY, randomToMaxLogic, auction);
            executorService.submit(bot);
        }

//        Bot bot = new Bot("Bot-" + 100, 6000, randomToMaxLogic, auction);
//        executorService.submit(bot);
//        bot = new Bot("Bot-" + 101, 6001, randomToMaxLogic, auction);
//        executorService.submit(bot);
//        bot = new Bot("Bot-" + 102, 6002, randomToMaxLogic, auction);
//        executorService.submit(bot);
    }

    private void start() {
        Lot lot = new Lot("First LOT", 5, 100);

        SingleLotEngine auction = new SingleLotEngine(lot);

        initBots(BOTS_COUNT, auction);

        auction.start().thenAccept(this::auctionFinished);
    }

    private void auctionFinished(AuctionResult auctionResult) {
        printResults(auctionResult);
        shutdown();
    }

    private void printResults(AuctionResult auctionResult) {
        printHistory("Full history", auctionResult.getBidsHistory());
        printHistory("Success bids", auctionResult.getBidsHistory().stream()
                .filter(HistoryItem::isSuccess)
                .collect(Collectors.toList()));

        log.info("======SUMMARY======");
        log.info("BOTS_COUNT:         {}", BOTS_COUNT);
        log.info("BOTS_THREADS_COUNT: {}", BOTS_THREADS_COUNT);
        log.info("BOTS_MONEY:         {}", BOTS_MONEY);

        log.info("Auction on Lot {} is finished. Winner: {} with bid: {}",
                auctionResult.getLotName(), auctionResult.getWinnerName(), auctionResult.getFinalPrice());

        log.info("Total bids:    {}", auctionResult.getBidsCounter());
        log.info("Total visits:  {}", auctionResult.getVisitsCounter());
    }

    private void printHistory(String title, List<HistoryItem> history) {
        log.info("=== {} ===", title);
        history.forEach(h -> log.info(h.toString()));
    }

    private void shutdown() {
        log.info("Executor shutdown");
        executorService.shutdown();
    }

    public static void main(String[] args) {
        AuctionSimulator simulator = new AuctionSimulator();
        simulator.start();
        log.debug("Simulation finished");
    }
}
