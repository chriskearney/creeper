package com.comandante.creeper.bot;


import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.player.PlayerMetadataSerializer;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

public class BotCommandManager {

    public static final String QUOTE_STORE = "quoteStore";
    private final GameManager gameManager;
    private final WeatherManager weatherManager;
    private final ChuckNorrisManager chuckNorrisManager;
    private final DictionaryManager dictionaryManager;
    private final OmdbManager omdbManager;
    private HTreeMap<String, Quote> quoteStore;
    private final DB db;

    public BotCommandManager(DB db, GameManager gameManager) {
        this.gameManager = gameManager;
        this.weatherManager = new WeatherManager(gameManager.getCreeperConfiguration());
        this.chuckNorrisManager = new ChuckNorrisManager(gameManager.getCreeperConfiguration());
        this.dictionaryManager = new DictionaryManager(gameManager.getCreeperConfiguration());
        this.omdbManager = new OmdbManager();
        this.db = db;
        if (db.exists(QUOTE_STORE)) {
            this.quoteStore = db.get(QUOTE_STORE);
        } else {
            this.quoteStore = db.createHashMap(QUOTE_STORE).valueSerializer(new QuoteSerializer()).make();
        }
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

    public OmdbManager getOmdbManager() {
        return omdbManager;
    }
}
