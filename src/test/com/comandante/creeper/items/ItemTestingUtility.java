package com.comandante.creeper.items;

import com.comandante.creeper.storage.FilebasedJsonStorage;
import com.comandante.creeper.storage.ItemStorage;
import com.google.gson.Gson;

import java.util.Optional;

public class ItemTestingUtility {

    private final static ItemStorage itemStorage = new ItemStorage(new FilebasedJsonStorage(new Gson()));

    public static Optional<ItemMetadata> getMetadataFromFile(String internalItemName) {
        return itemStorage.get(internalItemName);
    }

}
