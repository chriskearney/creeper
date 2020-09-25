package com.comandante.creeper.npc;


import com.comandante.creeper.common.ColorizedTextTemplate;
import com.comandante.creeper.common.CreeperMessage;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SentryManager;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.Loot;
import com.comandante.creeper.player.CoolDown;
import com.comandante.creeper.player.CoolDownType;
import com.comandante.creeper.player.DamageProcessor;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.spawner.SpawnRule;
import com.comandante.creeper.stats.Levels;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.stats.StatsBuilder;
import com.comandante.creeper.stats.StatsHelper;
import com.comandante.creeper.stats.experience.Experience;
import com.comandante.creeper.world.model.Area;
import com.comandante.creeper.world.model.Room;
import com.google.api.client.util.Sets;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.comandante.creeper.server.player_communication.Color.RED;
import static com.comandante.creeper.server.player_communication.Color.RESET;


public class Npc extends CreeperEntity {

    private final GameManager gameManager;
    private final String name;
    private final String colorName;
    private final Stats stats;
    private final String dieMessage;
    private final Temperament temperament;
    private final Set<Area> roamAreas;
    private final Set<String> validTriggers;
    private final Set<SpawnRule> spawnRules;
    private final ArrayBlockingQueue<NpcStatsChange> npcStatsChanges = new ArrayBlockingQueue<>(3000);
    private final ArrayBlockingQueue<Effect> effectsToApply = new ArrayBlockingQueue<>(3000);
    private final AtomicBoolean isAlive = new AtomicBoolean(true);
    private final Random random = new Random();
    private long lastPhraseTimestamp;
    private Loot loot;
    private List<Effect> effects = Lists.newArrayList();
    private int maxEffects = 4;
    private Map<String, Long> playerDamageMap = Maps.newHashMap();
    private Room currentRoom;
    private int effectsTickBucket = 0;
    private Set<CoolDown> coolDowns = Sets.newHashSet();
    private final Experience experience = new Experience();
    // The messages used when dealing damage
    private final Set<CreeperMessage> attackMessages;
    // The messages used when landing critical attacks
    private final Set<CreeperMessage> criticalAttackMessages;
    // Things the NPC randomly says during battle
    private final Set<CreeperMessage> battleMessages;
    // Things that npcs say randomly when idle
    private final Set<CreeperMessage> idleMessages;
    private final Optional<BufferedImage> art;

    private final Interner<String> interner = Interners.newWeakInterner();


    protected Npc(GameManager gameManager,
                  String name,
                  String colorName,
                  long lastPhraseTimestamp,
                  Stats stats,
                  String dieMessage,
                  Temperament temperament,
                  Set<Area> roamAreas,
                  Set<String> validTriggers,
                  Loot loot,
                  Set<SpawnRule> spawnRules,
                  Set<CreeperMessage> attackMessages,
                  Set<CreeperMessage> criticalAttackMessages,
                  Set<CreeperMessage> battleMessages,
                  Set<CreeperMessage> idleMessages,
                  Optional<BufferedImage> art) {
        this.gameManager = gameManager;
        this.name = name;
        this.colorName = colorName;
        this.lastPhraseTimestamp = lastPhraseTimestamp;
        this.stats = stats;
        this.dieMessage = dieMessage;
        this.roamAreas = roamAreas;
        this.validTriggers = validTriggers;
        this.loot = loot;
        this.spawnRules = spawnRules;
        this.temperament = temperament;
        this.attackMessages = attackMessages;
        this.criticalAttackMessages = criticalAttackMessages;
        this.battleMessages = battleMessages;
        this.idleMessages = idleMessages;
        this.art = art;
    }

    @Override
    public void run() {
        synchronized (interner.intern(getEntityId())) {
            try {
                if (isAlive.get()) {
                    if (effectsTickBucket == 5) {

                        List<Effect> addEffects = Lists.newArrayList();
                        effectsToApply.drainTo(addEffects);
                        processAddEffect(addEffects);

                        // START Process NPC Effects
                        Iterator<Effect> iterator = effects.iterator();
                        while (iterator.hasNext()) {
                            Effect effect = iterator.next();
                            if (effect.getEffectApplications() >= effect.getMaxEffectApplications()) {
                                Optional<Room> npcCurrentRoom = gameManager.getRoomManager().getNpcCurrentRoom(this);
                                if (npcCurrentRoom.isPresent()) {
                                    Room room = npcCurrentRoom.get();
                                    gameManager.writeToRoom(room.getRoomId(), Color.BOLD_ON + Color.GREEN + "[effect] " + Color.RESET + effect.getEffectName() + " has worn off of " + getName() + "\r\n");
                                }
                                gameManager.getEffectsManager().removeDurationStats(effect, this);
                                iterator.remove();
                            } else {
                                effect.setEffectApplications(effect.getEffectApplications() + 1);
                                effectsTickBucket = effectsTickBucket + 1;
                                gameManager.getEffectsManager().application(effect, this);
                            }
                        }
                        // END Process Npc Effects

                        effectsTickBucket = 0;
                    } else {
                        effectsTickBucket++;
                    }

                    List<NpcStatsChange> npcStatsChangeList = Lists.newArrayList();
                    npcStatsChanges.drainTo(npcStatsChangeList);
                    for (NpcStatsChange npcStatsChange : npcStatsChangeList) {
                        processNpcStatChange(npcStatsChange);
                    }
                    if (!isActiveCooldown(CoolDownType.NPC_FIGHT) && !isActiveCooldown(CoolDownType.NPC_ROAM) && currentRoom != null) {
                        if (getRandPercent(.001)) {
                            gameManager.getNpcMover().roam(getEntityId());
                        }
                    }
                    tickAllActiveCoolDowns();
                }
            } catch (Exception e) {
                e.printStackTrace();
                SentryManager.logSentry(this.getClass(), e, "NPC Ticker Problem!");
            }
        }
    }

    public String getName() {
        return name;
    }

    private void processAddEffect(List<Effect> addEffects) {
        for (Effect effect: addEffects) {
            if (effects.size() >= maxEffects) {
                continue;
            }
            effects.add(effect);
        }
    }

    private void processNpcStatChange(NpcStatsChange npcStatsChange) {
        try {
            if (npcStatsChange.getPlayer().isActive(CoolDownType.DEATH) && !npcStatsChange.isItemDamage()) {
                return;
            }
            if (!isAlive.get()) {
                return;
            }
            if (npcStatsChange.getStats() == null) {
                return;
            }
            for (String message : npcStatsChange.getDamageStrings()) {
                if (!npcStatsChange.getPlayer().isActive(CoolDownType.DEATH)) {
                    gameManager.getChannelUtils().write(npcStatsChange.getPlayer().getPlayerId(), message + "\r\n", true);
                }
            }
            StatsHelper.combineStats(getStats(), npcStatsChange.getStats());
            long amt = npcStatsChange.getStats().getCurrentHealth();
            long damageReportAmt = -npcStatsChange.getStats().getCurrentHealth();
            if (getStats().getCurrentHealth() < 0) {
                damageReportAmt = -amt + getStats().getCurrentHealth();
                getStats().setCurrentHealth(0);
            }
            long damage = 0;
            if (getPlayerDamageMap().containsKey(npcStatsChange.getPlayer().getPlayerId())) {
                damage = getPlayerDamageMap().get(npcStatsChange.getPlayer().getPlayerId());
            }
            addDamageToMap(npcStatsChange.getPlayer().getPlayerId(), damage + damageReportAmt);
            if (getStats().getCurrentHealth() == 0) {
                killNpc(npcStatsChange.getPlayer());
                return;
            }
            if (npcStatsChange.getPlayerStatsChange() != null) {
                for (String message : npcStatsChange.getPlayerDamageStrings()) {
                    if (!npcStatsChange.getPlayer().isActive(CoolDownType.DEATH)) {
                        gameManager.getChannelUtils().write(npcStatsChange.getPlayer().getPlayerId(), message + "\r\n", true);
                        npcStatsChange.getPlayer().updatePlayerHealth(npcStatsChange.getPlayerStatsChange().getCurrentHealth(), this);
                    }
                }
            }
        } catch (Exception e) {
            SentryManager.logSentry(this.getClass(), e, "Problem processing NPC Stat Change!");
        }
    }

    public boolean isActiveCooldown(CoolDownType coolDownType) {
        for (CoolDown c : coolDowns) {
            if (c.getCoolDownType().equals(coolDownType)) {
                if (c.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean getRandPercent(double percent) {
        double rangeMin = 0;
        double rangeMax = 100;
        double randomValue = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
        return randomValue <= percent;
    }

    private void tickAllActiveCoolDowns() {
        Iterator<CoolDown> iterator = coolDowns.iterator();
        while (iterator.hasNext()) {
            CoolDown coolDown = iterator.next();
            if (coolDown.isActive()) {
                coolDown.decrementTick();
            } else {
                iterator.remove();
            }
        }
    }

    public Stats getStats() {
        return stats;
    }

    public Map<String, Long> getPlayerDamageMap() {
        return playerDamageMap;
    }

    public void addDamageToMap(String playerId, long amt) {
        playerDamageMap.put(playerId, amt);
    }

    private void killNpc(Player player) {
        isAlive.set(false);
        player.removeActiveAlertStatus(this);
        Map<String, Double> damagePercents;
        Item corpse = Item.createCorpseItem(getName(), getLoot());
        if (!player.isActive(CoolDownType.DEATH)) {
            gameManager.writeToPlayerCurrentRoom(player.getPlayerId(), getDieMessage() + "\r\n");
        }
        damagePercents = gameManager.processExperience(this, getCurrentRoom());
        gameManager.getEntityManager().saveItem(corpse);
        Integer roomId = gameManager.getRoomManager().getNpcCurrentRoom(this).get().getRoomId();
        Room room = gameManager.getRoomManager().getRoom(roomId);
        room.addPresentItem(corpse.getItemId());
        gameManager.getItemDecayManager().addItem(corpse);
        getCurrentRoom().removePresentNpc(getEntityId());
        gameManager.getEntityManager().deleteNpcEntity(getEntityId());
        player.removeActiveFight(this);
        for (Map.Entry<String, Double> playerDamagePercent : damagePercents.entrySet()) {
            Player p = gameManager.getPlayerManager().getPlayer(playerDamagePercent.getKey());
            if (p == null) {
                continue;
            }
            Double playerDamagePercentValue = playerDamagePercent.getValue();

            int playerLevel = (int) Levels.getLevel(gameManager.getStatsModifierFactory().getStatsModifier(p).getExperience());
            int npcLevel = (int) Levels.getLevel(this.getStats().getExperience());

            long xpEarned = (long) (experience.calculateNpcXp(playerLevel, npcLevel) * playerDamagePercentValue);
            // example is fire sauce
            Optional<Double> xpModifier = player.getXpModifier(player.getCoolDowns());
            if (xpModifier.isPresent()) {
                player.writeMessage(Color.BOLD_ON + Color.GREEN + "[XP+ Modifier " + xpModifier.get() + "x]" + Color.RESET);
                xpEarned = Math.round(xpEarned * xpModifier.get());
            }
            p.addExperience(xpEarned);
            gameManager.getChannelUtils().write(p.getPlayerId(), getBattleReport(xpEarned) + "\r\n", true);
            p.addNpcKillLog(getName());
        }
    }

    public Loot getLoot() {
        return loot;
    }

    public String getDieMessage() {
        return dieMessage;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    private String getBattleReport(long xpEarned) {
        StringBuilder sb = new StringBuilder();
        sb.append(Color.MAGENTA).append("Battle Report----------------------------").append(Color.RESET).append("\r\n");
        sb.append("You killed a ");
        sb.append(getColorName());
        sb.append(" for ");
        sb.append(Color.GREEN);
        sb.append("+");
        sb.append(NumberFormat.getNumberInstance(Locale.US).format(xpEarned));
        sb.append(Color.RESET);
        sb.append(" experience points.");
        sb.append("\r\n");

        Set<Map.Entry<String, Long>> entries = getPlayerDamageMap().entrySet();
        Table t = new Table(2, BorderStyle.CLASSIC_COMPATIBLE,
                ShownBorders.NONE);

        t.setColumnWidth(0, 14, 24);
        t.setColumnWidth(1, 10, 13);
        t.addCell("Player");
        t.addCell("Damage");
        for (Map.Entry<String, Long> entry : entries) {
            Player player = gameManager.getPlayerManager().getPlayer(entry.getKey());
            String name = null;
            if (player != null) {
                name = player.getPlayerName();
            }
            long damageAmt = entry.getValue();
            t.addCell(name);
            t.addCell(NumberFormat.getNumberInstance(Locale.US).format(damageAmt));
        }
        sb.append(t.render());
        return sb.toString();
    }

    public String getColorName() {
        return colorName;
    }

    public void setLoot(Loot loot) {
        this.loot = loot;
    }

    public Set<CoolDown> getCoolDowns() {
        return coolDowns;
    }

    public Set<String> getValidTriggers() {
        return validTriggers;
    }

    public Set<Area> getRoamAreas() {
        return roamAreas;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public long getLastPhraseTimestamp() {
        return lastPhraseTimestamp;
    }

    public void setLastPhraseTimestamp(long lastPhraseTimestamp) {
        this.lastPhraseTimestamp = lastPhraseTimestamp;
    }

    public void npcSay(Integer roomId, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append(RED);
        sb.append(name).append(": ").append(message);
        sb.append(RESET);
    }

    public Optional<SpawnRule> getSpawnRuleByArea(Area area) {
        Set<SpawnRule> spawnRules = getSpawnRules();
        for (SpawnRule spawnRule : spawnRules) {
            if (spawnRule.getArea().equals(area)) {
                return Optional.of(spawnRule);
            }
        }
        return Optional.empty();
    }

    public Set<SpawnRule> getSpawnRules() {
        return spawnRules;
    }

    public void addEffect(Effect effect) {
        this.effectsToApply.add(effect);
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public long getExperience() {
        return getStats().getExperience();
    }

    public Temperament getTemperament() {
        return temperament;
    }

    public AtomicBoolean getIsAlive() {
        return isAlive;
    }

    public void doHealthDamage(Player player, List<String> damageStrings, long amt) {
        NpcStatsChange npcStatsChange =
                new NpcStatsChangeBuilder().setStats(new StatsBuilder().setCurrentHealth(amt).createStats()).setDamageStrings(damageStrings).setPlayer(player).createNpcStatsChange();
        addNpcDamage(npcStatsChange);
    }

    public void addNpcDamage(NpcStatsChange npcStatsChange) {
        if (!isActiveCooldown(CoolDownType.NPC_FIGHT)) {
            addCoolDown(new CoolDown(CoolDownType.NPC_FIGHT));
        } else {
            for (CoolDown coolDown : coolDowns) {
                if (coolDown.getCoolDownType().equals(CoolDownType.NPC_FIGHT)) {
                    coolDown.setNumberOfTicks(coolDown.getOriginalNumberOfTicks());
                }
            }
        }
        this.npcStatsChanges.add(npcStatsChange);
    }

    public void addCoolDown(CoolDown coolDown) {
        this.coolDowns.add(coolDown);
    }

    public NpcLevelColor getLevelColor(int playerLevel) {
        return experience.getLevelColor(playerLevel, (int) Levels.getLevel(this.getStats().getExperience()));
    }

    public DamageProcessor getDamageProcessor() {
        return new BasicNpcPlayerDamageProcessor();
    }

    public enum NpcLevelColor {

        RED(Color.RED + "Red"),
        ORANGE(Color.CYAN + "Cyan"),
        YELLOW(Color.YELLOW + "Yellow"),
        GREEN(Color.GREEN + "Green"),
        WHITE(Color.WHITE + "White");

        private final String color;

        NpcLevelColor(String color) {
            this.color = color;
        }

        public String getColor() {
            return "(" + Color.BOLD_ON + color + Color.RESET + ")";
        }
    }

    public CreeperMessage getRandomAttackMessage() {

        if (attackMessages == null || attackMessages.size() == 0) {
            return new CreeperMessage(CreeperMessage.Type.NORMAL, "Somebody for got to configure attack messages. - " + this.getName());
        }

        int size = attackMessages.size();
        int item = random.nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(CreeperMessage attackMessage : attackMessages) {
            if (i == item) {
                return attackMessage;
            }
            i++;
        }
        return null;
    }

    public Set<CreeperMessage> getAttackMessages() {
        return attackMessages;
    }

    public String buildAttackMessage(String playerName) {
        CreeperMessage randomAttackMessage = getRandomAttackMessage();
        Map<String, String> valueMap = Maps.newHashMap();
        valueMap.put("player-name", playerName);
        valueMap.put("npc-name", this.getName());
        valueMap.put("npc-color-name", this.getColorName());
        return ColorizedTextTemplate.renderFromTemplateLanguage(valueMap, randomAttackMessage.getMessage());
    }

    public Set<CreeperMessage> getCriticalAttackMessages() {
        return criticalAttackMessages;
    }

    public Set<CreeperMessage> getBattleMessages() {
        return battleMessages;
    }

    public Set<CreeperMessage> getIdleMessages() {
        return idleMessages;
    }

    public Optional<BufferedImage> getArt() {
        return art;
    }
}
