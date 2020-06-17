package com.comandante.creeper.core_game.service;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.player_communication.Color;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeTracker extends CreeperEntity {

    private static final int NUMBER_OF_MILLISECONDS_IN_A_DAY = 86400000;
    private int currentTick = 0;
    private TimeOfDay currentTimeOfDay = TimeOfDay.MORNING;
    private final GameManager gameManager;

    public TimeTracker(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
             TimeOfDay timeOfDay = determineTimeOfDay();
             if (timeOfDay != currentTimeOfDay) {
                 announceChange(timeOfDay);
             }
             currentTimeOfDay = timeOfDay;
    }

    private void announceChange(TimeOfDay timeOfDay) {
        Iterator<Map.Entry<String, Player>> players = gameManager.getPlayerManager().getPlayers();
        while (players.hasNext()) {
            Map.Entry<String, Player> next = players.next();
            gameManager.getChannelUtils().write(next.getValue().getPlayerId(), "It is now " + timeOfDay.color + timeOfDay + Color.RESET + ".\r\n", true);
        }
    }

    public TimeOfDay getTimeOfDay() {
        return currentTimeOfDay;
    }

    private TimeOfDay determineTimeOfDay() {
        long milliSecondsSinceMidnight = milliSecondsSinceMidnight();
        if (milliSecondsSinceMidnight <= TimeUnit.HOURS.toMillis(5)) {
            return TimeOfDay.NIGHT;
        } else if (milliSecondsSinceMidnight <= TimeUnit.HOURS.toMillis(12)) {
            return TimeOfDay.MORNING;
        } else if (milliSecondsSinceMidnight <= TimeUnit.HOURS.toMillis(16)) {
            return TimeOfDay.AFTERNOON;
        } else if (milliSecondsSinceMidnight <= TimeUnit.HOURS.toMillis(20)) {
            return TimeOfDay.EVENING;
        } else {
            return TimeOfDay.NIGHT;
        }
    }

    private long milliSecondsSinceMidnight() {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long passed = now - c.getTimeInMillis();
        return passed;
    }
}
