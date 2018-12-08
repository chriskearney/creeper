package com.comandante.creeper.storage;

import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.player.PlayerMetadataSerializer;
import com.google.common.util.concurrent.AbstractIdleService;
import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.util.Map;
import java.util.Optional;

public class MapDBCreeperStorage extends AbstractIdleService implements CreeperStorage  {

    private final DB db;
    private final MapDbAutoCommitService mapDbAutoCommitService;

    private final HTreeMap<String, Item> items;
    private final HTreeMap<String, PlayerMetadata> playerMetadataStore;
    private final HTreeMap<String, String> ircWeatherPreferences;

    private final static String ITEM_MAP = "itemMap";
    private final static String WEATHER_PREFERENCES_MAP = "weatherPreferencesMap";
    private final static String PLAYER_METADATA_MAP = "playerMetadata";

    public MapDBCreeperStorage(DB db) {
        this.db = db;
        this.items = db.hashMap(ITEM_MAP)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new ItemSerializer())
                .createOrOpen();

        this.ircWeatherPreferences = db.hashMap(WEATHER_PREFERENCES_MAP)
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.STRING)
                .createOrOpen();

        this.playerMetadataStore = db.hashMap(PLAYER_METADATA_MAP)
                .keySerializer(Serializer.STRING)
                .valueSerializer(new PlayerMetadataSerializer())
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
    public void saveWeatherPreference(String nick, String weatherQuery) {
            this.ircWeatherPreferences.put(nick, weatherQuery);
    }

    @Override
    public Optional<String> getWeatherQuery(String nick) {
        return Optional.ofNullable(this.ircWeatherPreferences.get(nick));
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
