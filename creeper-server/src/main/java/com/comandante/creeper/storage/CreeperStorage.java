package com.comandante.creeper.storage;


import com.comandante.creeper.items.Item;
import com.comandante.creeper.player.PlayerMetadata;

import java.util.Map;
import java.util.Optional;

public interface CreeperStorage {

    void savePlayerMetadata(PlayerMetadata playerMetadata);

    Optional<PlayerMetadata> getPlayerMetadata(String playerId);

    Map<String, PlayerMetadata> getAllPlayerMetadata();

    void removePlayerMetadata(String playerId);

    void saveItemEntity(Item item);

    Optional<Item> getItemEntity(String itemId);

    void removeItem(String itemId);

    Map<String, String> getWeatherHistory();
}
