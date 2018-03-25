package com.comandante.creeper.player;

import java.util.List;

public class Quest {

    private final String questName;
    private final Critera critera;
    private final Reward reward;
    private final String questDescription;
    private final String questCompletionText;

    public Quest(String questName, Critera critera, Reward reward, String questDescription, String questCompletionText) {
        this.questName = questName;
        this.critera = critera;
        this.reward = reward;
        this.questDescription = questDescription;
        this.questCompletionText = questCompletionText;
    }

    public String getQuestName() {
        return questName;
    }

    public Critera getCritera() {
        return critera;
    }

    public Reward getReward() {
        return reward;
    }

    public String getQuestDescription() {
        return questDescription;
    }

    public String getQuestCompletionText() {
        return questCompletionText;
    }

    public static class Critera {

        private final List<ItemsAmount> items;

        public Critera(List<ItemsAmount> items) {
            this.items = items;
        }

        public List<ItemsAmount> getItems() {
            return items;
        }
    }

    public static class Reward {

        private final List<ItemsAmount> items;
        private final long gold;

        public Reward(List<ItemsAmount> items, long gold) {
            this.items = items;
            this.gold = gold;
        }

        public List<ItemsAmount> getItems() {
            return items;
        }

        public long getGold() {
            return gold;
        }
    }

    public static class ItemsAmount {

        private final int amount;
        private final String iternalItemName;

        public ItemsAmount(int amount, String iternalItemName) {
            this.amount = amount;
            this.iternalItemName = iternalItemName;
        }

        public int getAmount() {
            return amount;
        }

        public String getIternalItemName() {
            return iternalItemName;
        }
    }

}
