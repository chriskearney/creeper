package com.comandante.creeper.player;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class Quest {

    private String questName;
    private Critera criteria;
    private Reward reward;
    private String questDescription;
    private String questCompletionText;
    private long minimumLevel;
    private Set<PlayerClass> limitedClasses;
    private long expireTimeInSeconds;
    private String dependentOnCompletionOfQuestName;

    public Quest(String questName, Critera criteria, Reward reward, String questDescription, String questCompletionText, long minimumLevel, Set<PlayerClass> limitedClasses, long expireTimeInSeconds, String dependentOnCompletionOfQuestName) {
        this.questName = questName;
        this.criteria = criteria;
        this.reward = reward;
        this.questDescription = questDescription;
        this.questCompletionText = questCompletionText;
        this.minimumLevel = minimumLevel;
        this.limitedClasses = limitedClasses;
        this.expireTimeInSeconds = expireTimeInSeconds;
        this.dependentOnCompletionOfQuestName = dependentOnCompletionOfQuestName;
    }

    public Quest() {
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    public void setCriteria(Critera criteria) {
        this.criteria = criteria;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public void setQuestDescription(String questDescription) {
        this.questDescription = questDescription;
    }

    public void setQuestCompletionText(String questCompletionText) {
        this.questCompletionText = questCompletionText;
    }

    public void setMinimumLevel(long minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public void setLimitedClasses(Set<PlayerClass> limitedClasses) {
        this.limitedClasses = limitedClasses;
    }

    public long getExpireTimeInSeconds() {
        return expireTimeInSeconds;
    }

    public void setExpireTimeInSeconds(long expireTimeInSeconds) {
        this.expireTimeInSeconds = expireTimeInSeconds;
    }

    public String getDependentOnCompletionOfQuestName() {
        return dependentOnCompletionOfQuestName;
    }

    public void setDependentOnCompletionOfQuestName(String dependentOnCompletionOfQuestName) {
        this.dependentOnCompletionOfQuestName = dependentOnCompletionOfQuestName;
    }

    public String getQuestName() {
        return questName;
    }

    public Critera getCriteria() {
        return criteria;
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

    public long getMinimumLevel() {
        return minimumLevel;
    }

    public Set<PlayerClass> getLimitedClasses() {
        if (this.limitedClasses == null) {
            this.limitedClasses = Sets.newHashSet();
        }
        return limitedClasses;
    }

    public static class Critera {

        private List<ItemsAmount> items;

        public Critera() {
        }

        public Critera(List<ItemsAmount> items) {
            this.items = items;
        }

        public List<ItemsAmount> getItems() {
            return items;
        }

        public void setItems(List<ItemsAmount> items) {
            this.items = items;
        }
    }

    public static class Reward {

        private List<ItemsAmount> items;
        private long gold;
        private long xp;

        public Reward(List<ItemsAmount> items, long gold, long xp) {
            this.items = items;
            this.gold = gold;
            this.xp = xp;
        }

        public Reward() {
        }

        public List<ItemsAmount> getItems() {
            return items;
        }

        public long getGold() {
            return gold;
        }

        public long getXp() {
            return xp;
        }

        public void setItems(List<ItemsAmount> items) {
            this.items = items;
        }

        public void setGold(long gold) {
            this.gold = gold;
        }

        public void setXp(long xp) {
            this.xp = xp;
        }
    }

    public static class ItemsAmount {

        private int amount;
        private String internalItemName;

        public ItemsAmount(int amount, String internalItemName) {
            this.amount = amount;
            this.internalItemName = internalItemName;
        }

        public ItemsAmount() {
        }

        public int getAmount() {
            return amount;
        }

        public String getInternalItemName() {
            return internalItemName;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void setInternalItemName(String internalItemName) {
            this.internalItemName = internalItemName;
        }
    }
}
