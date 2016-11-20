package com.onseo.course.history;

import com.onseo.course.common.Bid;
import com.onseo.course.util.SystemTime;
import lombok.Data;

/**
 * Created by vlad on 11/19/16.
 */
@Data
public class HistoryItem {
    private final long time = SystemTime.getTimeMs();
    private final Bid bid;
    private final boolean success;
}
