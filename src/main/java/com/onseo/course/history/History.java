package com.onseo.course.history;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by vlad on 11/20/16.
 */
public class History {
//    private final Collection<HistoryItem> history = new CopyOnWriteArrayList<>();
    private final Collection<HistoryItem> history = new LinkedList<>();
//    private final Collection<HistoryItem> history = new ConcurrentLinkedQueue<>();


    public void add(HistoryItem historyItem) {
        history.add(historyItem);

    }

    public Collection<HistoryItem> get() {
        return new ArrayList<>(history);
    }

    public String getDescription() {
        return history.getClass().getSimpleName();
    }
}
