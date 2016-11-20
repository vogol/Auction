package com.onseo.course;

import com.onseo.course.bot.Bot;
import com.onseo.course.bot.BotLogic;
import com.onseo.course.bot.SimpleIncLogic;
import com.onseo.course.common.Lot;
import com.onseo.course.engine.*;
import com.onseo.course.history.HistoryItem;
import com.onseo.course.viewer.Viewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AuctionSimulator {
    private static final Logger log = LoggerFactory.getLogger(AuctionSimulator.class);

    private static final int AUCTIOIN_DURATION = 10;
    private static final int BOTS_COUNT = 100;
    private static final int BOTS_THREADS_COUNT = 100;
    private static final int BOTS_MONEY = 1_000_000;
    private static final int VIEWERS_COUNT = 1000;
    private static final int VIEWERS_THREADS_COUNT = 1000;


    private Lot lot = new Lot("First LOT", AUCTIOIN_DURATION, 1);
//    private Auction auction = new SynchronizedEngine(lot);
//    private Auction auction = new SingleLockedEngine(lot);
//    private Auction auction = new DoubleLockedEngine(lot);
    private Auction auction = new NonBlockingHistoryEngine(lot);

    private ExecutorService botsExecutor = Executors.newFixedThreadPool(BOTS_THREADS_COUNT);
    private ExecutorService viewersExecutor = Executors.newFixedThreadPool(VIEWERS_THREADS_COUNT);

    private void initBots(int amount, Auction auction) {
        BotLogic logic = new SimpleIncLogic(1);

        for (int i = 0; i < amount; i++) {
            Bot bot = new Bot("Bot-" + i, BOTS_MONEY, logic, auction);
            botsExecutor.submit(bot);
        }
    }

    private void initViewers(int amount, Auction auction) {
        for (int i = 0; i < amount; i++) {
            Viewer viewer = new Viewer("Viewer-" + i, auction);
            viewersExecutor.submit(viewer);
        }
    }

    private void start() {

        initBots(BOTS_COUNT, auction);
        initViewers(VIEWERS_COUNT, auction);

        auction.start().thenAccept(this::auctionFinished);
    }

    private void auctionFinished(AuctionResult auctionResult) {
        printResults(auctionResult);
        shutdown();
    }

    private void printResults(AuctionResult auctionResult) {
        /*
        printHistory("Full history", auctionResult.getBidsHistory());
        printHistory("Success bids", auctionResult.getBidsHistory().stream()
                .filter(HistoryItem::isSuccess)
                .collect(Collectors.toList()));
        */

        log.info("\n\n======SUMMARY======"
                + "\nAUCTIOIN_DURATION:     " + AUCTIOIN_DURATION
                + "\nBOTS_COUNT:            " + BOTS_COUNT
                + "\nBOTS_THREADS_COUNT:    " + BOTS_THREADS_COUNT
                + "\nBOTS_MONEY:            " + BOTS_MONEY
                + "\nVIEWERS_COUNT:         " + VIEWERS_COUNT
                + "\nVIEWERS_THREADS_COUNT: " + VIEWERS_THREADS_COUNT
                + "\n"
                + "\nAuction engine:        " + auction.getDescription()
                + "\nHistory storage:       " + auctionResult.getBidsHistory().getDescription()
                + "\n"
                + "\nTotal bids:   " + auctionResult.getBidsCounter()
                + "\nTotal views:  " + auctionResult.getViewsCounter()
                + "\n"
                + "\nWinner:       " + auctionResult.getWinnerName()
                + "\nFinal price:  " + auctionResult.getFinalPrice()
                + "\n"
        );
        log.info("\n\n======TSV======"
                        + "\nDuration\tBots\tBotsThreads\tBotsMoney\tViewers\tViewersThreads\tAuction engine\tHistory storage\tTotal bids\tTotal views\tWinner\tFinal price"
                        + "\n{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}"
                , AUCTIOIN_DURATION
                , BOTS_COUNT
                , BOTS_THREADS_COUNT
                , BOTS_MONEY
                , VIEWERS_COUNT
                , VIEWERS_THREADS_COUNT
                , auction.getDescription()
                , auctionResult.getBidsHistory().getDescription()
                , auctionResult.getBidsCounter()
                , auctionResult.getViewsCounter()
                , auctionResult.getWinnerName()
                , auctionResult.getFinalPrice()
        );
    }

    private void printHistory(String title, Collection<HistoryItem> history) {
        log.info("=== {} ===", title);
        history.forEach(h -> log.info(h.toString()));
    }

    private void shutdown() {
        log.info("Executors shutdown");
        botsExecutor.shutdown();
        viewersExecutor.shutdown();
    }

    public static void main(String[] args) {
        AuctionSimulator simulator = new AuctionSimulator();
        simulator.start();
    }
}
