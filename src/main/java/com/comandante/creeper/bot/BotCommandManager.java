package com.comandante.creeper.bot;


import com.comandante.creeper.managers.GameManager;

public class BotCommandManager {

    private final GameManager gameManager;
    private final WeatherManager weatherManager;
    private final ChuckNorrisManager chuckNorrisManager;
    private final DictionaryManager dictionaryManager;

    public BotCommandManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.weatherManager = new WeatherManager(gameManager.getCreeperConfiguration());
        this.chuckNorrisManager = new ChuckNorrisManager(gameManager.getCreeperConfiguration());
        this.dictionaryManager = new DictionaryManager(gameManager.getCreeperConfiguration());
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public WeatherManager getWeatherManager() {
        return weatherManager;
    }

    public ChuckNorrisManager getChuckNorrisManager() {
        return chuckNorrisManager;
    }

    public DictionaryManager getDictionaryManager() {
        return dictionaryManager;
    }
}
