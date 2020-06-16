package com.comandante.creeper.storage;


import com.google.common.util.concurrent.AbstractScheduledService;
import org.mapdb.DB;

import java.util.concurrent.TimeUnit;

public class MapDbAutoCommitService extends AbstractScheduledService {

    private final DB db;

    public MapDbAutoCommitService(DB db) {
        this.db = db;
    }

    @Override
    protected void runOneIteration() throws Exception {
        db.commit();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(30, 30, TimeUnit.SECONDS);
    }
}
