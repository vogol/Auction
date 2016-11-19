package com.onseo.course.engine;

import com.onseo.course.common.HistoryItem;
import lombok.Data;

import java.util.List;

/**
 * Created by vlad on 11/19/16.
 */
@Data
public class AuctionResult {
    private final String lotName;
    private final String winnerName;
    private final int finalPrice;
    private final List<HistoryItem> bidsHistory;
    private final int bidsCounter;
    private final int visitsCounter;
}
