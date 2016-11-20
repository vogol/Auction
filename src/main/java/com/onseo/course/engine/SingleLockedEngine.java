package com.onseo.course.engine;

import com.onseo.course.common.Bid;
import com.onseo.course.history.History;
import com.onseo.course.history.HistoryItem;
import com.onseo.course.common.Lot;
import com.onseo.course.common.Lot.LotDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by VOgol on 17.11.2016.
 */
public class SingleLockedEngine extends LockedEngine {
    public SingleLockedEngine(Lot lot) {
        super(lot);
    }

    protected ReadWriteLock getLotLock() {
        return lotLock;
    }

    protected ReadWriteLock getHistoryLock() {
        return lotLock;
    }
}
