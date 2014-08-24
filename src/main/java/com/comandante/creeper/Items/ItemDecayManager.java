package com.comandante.creeper.Items;

import com.comandante.creeper.managers.EntityManager;
import com.comandante.creeper.model.CreeperEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemDecayManager extends CreeperEntity {

    private final ConcurrentHashMap<String, DecayProgress> itemDecayTracker = new ConcurrentHashMap<String, DecayProgress>();
    private final EntityManager entityManager;

    public ItemDecayManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void addItem(Item item) {
        itemDecayTracker.put(item.getItemId(), new DecayProgress(ItemType.itemTypeFromCode(item.getItemTypeId()).getItemHalfLifeTicks()));
    }

    public void removeItem(Item item) {
        itemDecayTracker.remove(item.getItemId());
    }

    public ConcurrentHashMap<String, DecayProgress> getItemDecayTracker() {
        return itemDecayTracker;
    }

    @Override
    public void run() {
        List<String> itemsToRemoveFromDecay = new ArrayList<>();
        List<String> itemsToDestroy = new ArrayList<>();
        ConcurrentHashMap<String, DecayProgress> itemDecayTracker1 = getItemDecayTracker();
        for (Map.Entry<String, DecayProgress> next : itemDecayTracker1.entrySet()) {
            DecayProgress decayProgress = next.getValue();
            Item item = entityManager.getItemEntity(next.getKey());
            if (item.isWithPlayer()) {
                itemsToRemoveFromDecay.add(item.getItemId());
                continue;
            }

            decayProgress.incTick();
            if (decayProgress.getCurrentTicks() >= decayProgress.getNumberOfTicks()) {
                itemsToDestroy.add(item.getItemId());
            }
        }
        for (String itemId: itemsToRemoveFromDecay) {
            Item itemEntity = entityManager.getItemEntity(itemId);
            removeItem(itemEntity);
        }
        for (String itemId: itemsToDestroy) {
            Item itemEntity = entityManager.getItemEntity(itemId);
            removeItem(itemEntity);
            entityManager.removeItem(itemEntity);
        }
    }

    class DecayProgress {
        private final int numberOfTicks;
        private int currentTicks = 0;

        DecayProgress(int numberOfTicks) {
            this.numberOfTicks = numberOfTicks;
        }

        public void incTick(){
            this.currentTicks++;
        }

        public int getNumberOfTicks() {
            return numberOfTicks;
        }

        public int getCurrentTicks() {
            return currentTicks;
        }
    }

}
