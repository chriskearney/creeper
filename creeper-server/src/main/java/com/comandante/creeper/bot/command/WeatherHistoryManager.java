package com.comandante.creeper.bot.command;

import com.comandante.creeper.storage.CreeperStorage;
import org.apache.log4j.Logger;
import org.pircbotx.UserHostmask;

import java.util.Optional;

public class WeatherHistoryManager {

    private final CreeperStorage creeperStorage;
    private static final Logger log = Logger.getLogger(WeatherHistoryManager.class);

    public WeatherHistoryManager(CreeperStorage creeperStorage) {
        this.creeperStorage = creeperStorage;
    }

    public Optional<String> getArgumentString(String nick) {
        try {
            return Optional.ofNullable(creeperStorage.getWeatherHistory().get(nick));
        } catch (Exception e) {
            log.error("Unable to retrieve weather history.", e);
        }

        return Optional.empty();
    }

    public void save(String nick, String argumentString) {
        creeperStorage.getWeatherHistory().put(nick, argumentString);
    }
}
