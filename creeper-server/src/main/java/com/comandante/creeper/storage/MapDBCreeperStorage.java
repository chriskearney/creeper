package com.comandante.creeper.storage;

import com.comandante.creeper.bot.command.QuoteManager;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.player.PlayerMetadataSerializer;
import com.google.common.util.concurrent.AbstractIdleService;
import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.mapdb.serializer.SerializerString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MapDBCreeperStorage extends AbstractIdleService implements CreeperStorage  {

    private final DB db;
    private final MapDbAutoCommitService mapDbAutoCommitService;

    private final HTreeMap<String, Item> items;
    private final HTreeMap<String, Effect> effects;
    private final HTreeMap<String, PlayerMetadata> playerMetadataStore;
    private final HTreeMap<String, String> weatherHistory;
    private final HTreeMap<String, List<QuoteManager.IrcQuote>> ircQuotes;

    private final static String ITEM_MAP = "itemMap";
    private final static String EFFECTS_MAP = "effectsMap";
    private final static String PLAYER_METADATA_MAP = "playerMetadata";
    private final static String WEATHER_HISTORY = "weatherHistory";
    private final static String IRC_QUOTES = "ircQuotesDatabase";

    public MapDBCreeperStorage(DB db) {
        this.db = db;
        this.items = db.hashMap(ITEM_MAP)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new ItemSerializer())
                .createOrOpen();

        this.effects = db.hashMap(EFFECTS_MAP)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new EffectSerializer())
                .createOrOpen();

        this.playerMetadataStore = db.hashMap(PLAYER_METADATA_MAP)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new PlayerMetadataSerializer())
                .createOrOpen();

        this.weatherHistory = db.hashMap(WEATHER_HISTORY)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.STRING)
                .createOrOpen();

        this.ircQuotes = db.hashMap(IRC_QUOTES)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new IrcQuotesSerializer())
                .createOrOpen();

        this.mapDbAutoCommitService = new MapDbAutoCommitService(db);

    }

    @Override
    public void savePlayerMetadata(PlayerMetadata playerMetadata) {
        this.playerMetadataStore.put(playerMetadata.getPlayerId(), playerMetadata);
    }

    @Override
    public Optional<PlayerMetadata> getPlayerMetadata(String playerId) {
        return Optional.ofNullable(this.playerMetadataStore.get(playerId));
    }

    @Override
    public Map<String, PlayerMetadata> getAllPlayerMetadata() {
        return playerMetadataStore;
    }

    @Override
    public void removePlayerMetadata(String playerId) {
        this.playerMetadataStore.remove(playerId);
    }

    @Override
    public void saveItemEntity(Item item) {
        this.items.put(item.getItemId(), item);
    }

    @Override
    public Optional<Item> getItemEntity(String itemId) {
        return Optional.ofNullable(this.items.get(itemId));
    }

    @Override
    public void removeItem(String itemId) {
        this.items.remove(itemId);
    }

    @Override
    public Map<String, String> getWeatherHistory() {
        return weatherHistory;
    }

    @Override
    public Map<String, List<QuoteManager.IrcQuote>> getIrcQuotes() {
        return ircQuotes;
    }

    @Override
    protected void startUp() throws Exception {
        mapDbAutoCommitService.startAsync();
    }

    @Override
    protected void shutDown() throws Exception {
        mapDbAutoCommitService.stopAsync();
        mapDbAutoCommitService.awaitTerminated();
        db.commit();
    }
}
