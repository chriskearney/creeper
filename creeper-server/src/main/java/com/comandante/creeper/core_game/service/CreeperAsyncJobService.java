package com.comandante.creeper.core_game.service;

import com.google.api.client.util.Lists;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CreeperAsyncJobService extends AbstractScheduledService {

    private final ArrayBlockingQueue<CreeperAsyncJob> creeperAsyncJobQueue;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private static final Logger log = Logger.getLogger(CreeperAsyncJobService.class);

    public CreeperAsyncJobService(ArrayBlockingQueue<CreeperAsyncJob> creeperAsyncJobQueue) {
        this.creeperAsyncJobQueue = creeperAsyncJobQueue;
    }

    @Override
    protected void runOneIteration() throws Exception {
        ArrayList<CreeperAsyncJob> events = Lists.newArrayList();
        creeperAsyncJobQueue.drainTo(events);
        for (CreeperAsyncJob event: events) {
            executorService.submit(() -> {
                safeRun(event);
            });
        }
    }

    public void assAsyncJob(CreeperAsyncJob event) {
        try {
            creeperAsyncJobQueue.put(event);
        } catch (InterruptedException ex) {
            log.error("Problem adding event.", ex);
        }
    }

    private void safeRun(final CreeperAsyncJob e) {
        try {
            e.run();
        } catch (Exception ex) {
            log.error("Problem executing event.", ex);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 10, TimeUnit.MILLISECONDS);
    }
}
