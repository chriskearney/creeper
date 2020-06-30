package com.comandante.creeper.items;

import com.comandante.creeper.player.PlayerClass;
import com.comandante.creeper.stats.DefaultStats;
import com.comandante.creeper.storage.FilebasedJsonStorage;
import com.comandante.creeper.storage.ItemStorage;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.junit.Test;

import java.util.Optional;

public class ItemTestingUtility {

    private final static ItemStorage itemStorage = new ItemStorage(new FilebasedJsonStorage(new Gson()));

    public static Optional<ItemMetadata> getMetadataFromFile(String internalItemName) {
        return itemStorage.get(internalItemName);
    }

    @Test
    public void testNewEquip() {
        Equipment equipment = new Equipment(EquipmentSlotType.HEAD, DefaultStats.DEFAULT_PLAYER.createStats(), Sets.newHashSet(PlayerClass.WARRIOR));
        Gson gson = new Gson();
        String s = gson.toJson(equipment, Equipment.class);
    }
}
