package com.comandante.creeper.configuration;


import com.comandante.creeper.Creeper;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.items.Forage;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcBuilder;
import com.comandante.creeper.spawner.ItemSpawner;
import com.comandante.creeper.spawner.NpcSpawner;
import com.comandante.creeper.spawner.SpawnRule;
import com.comandante.creeper.storage.NpcMetadata;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ConfigureNpc {

    public static void configureAllNpcs(GameManager gameManager) throws IOException {
        EntityManager entityManager = gameManager.getEntityManager();
        List<NpcMetadata> npcsFromFile = gameManager.getNpcStorage().readAllNpcs();

        for (NpcMetadata npcMetadata : npcsFromFile) {
            Creeper.startUpMessage("Adding npc spawn: " + npcMetadata.getName());
//            Npc npc = new NpcBuilder(npcMetadata).setGameManager(gameManager).createNpc();
//            entityManager.addEntity(npc);
            Set<SpawnRule> spawnRules = npcMetadata.getSpawnRules();
            for (SpawnRule spawnRule : spawnRules) {
                entityManager.addEntity(new NpcSpawner(npcMetadata, gameManager, spawnRule));
            }
        }
    }

    public static void configure(EntityManager entityManager, GameManager gameManager) throws IOException {

        configureAllNpcs(gameManager);

        List<ItemMetadata> allItemMetadata = gameManager.getItemStorage().getItemMetadatas();

        for (ItemMetadata itemMetadata : allItemMetadata) {
            for (SpawnRule spawnRule : itemMetadata.getSpawnRules()) {
                Creeper.startUpMessage("Adding item spawn: " + itemMetadata.getInternalItemName());
                ItemSpawner itemSpawner = new ItemSpawner(itemMetadata, spawnRule, gameManager);
                entityManager.addEntity(itemSpawner);
            }
        }

        for (ItemMetadata itemMetadata : allItemMetadata) {
            for (Forage forage : itemMetadata.getForages()) {
                Creeper.startUpMessage("Adding forage: " + itemMetadata.getInternalItemName());
                gameManager.getForageManager().addForage(itemMetadata.getInternalItemName(), forage);
            }
        }

        List<Merchant> allMerchantMetadatas = gameManager.getMerchantStorage().getAllMerchants();
        for (Merchant merchant : allMerchantMetadatas) {
            Creeper.startUpMessage("Adding merchant: " + merchant.getInternalName());
            gameManager.getRoomManager().addMerchant(merchant);
        }

    }
}
