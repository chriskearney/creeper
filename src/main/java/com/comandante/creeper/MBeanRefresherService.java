package com.comandante.creeper;


import com.comandante.creeper.managers.GameManager;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.mapdb.DB;

import java.util.concurrent.TimeUnit;

public class MBeanRefresherService extends AbstractScheduledService {

    private final GameManager gameManager;

    public MBeanRefresherService(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    protected void runOneIteration() throws Exception {
        gameManager.getPlayerManagementManager().createAndRegisterAllPlayerManagementMBeans();
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(2, 2, TimeUnit.MINUTES);
    }
}
