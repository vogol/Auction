package com.onseo.course.engine;

import com.onseo.course.history.History;
import com.onseo.course.history.HistoryItem;
import lombok.Data;

import java.util.Collection;

/**
 * Created by vlad on 11/19/16.
 */
@Data
public class AuctionResult {
    private final String lotName;
    private final String winnerName;
    private final int finalPrice;
    private final History bidsHistory;
    private final int bidsCounter;
    private final int viewsCounter;
}
