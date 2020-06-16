package com.comandante.creeper.player;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemBuilder;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.server.player_communication.Color;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.common.base.Joiner;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerManagement implements PlayerManagementMBean {

    private final GameManager gameManager;
    private final String playerId;

    public PlayerManagement(GameManager gameManager, String playerId) {
        this.gameManager = gameManager;
        this.playerId = playerId;
    }

    @Override
    public void setMarkForDelete(boolean isMark) {
        Interner<String> interner = findInterner();
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.setIsMarkedForDelete(isMark);
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public boolean getMarkForDelete() {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return false;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        return playerMetadata.isMarkedForDelete();
    }

    @Override
    public long getGold() {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return 0L;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        return playerMetadata.getGold();
    }

    @Override
    public long getGoldInBankAmount() {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return 0L;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        return playerMetadata.getGoldInBank();
    }

    @Override
    public void setGoldInBankAmount(long amt) {
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()){
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.setGoldInBank(amt);
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public void setGold(long amt) {
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()){
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.setGold(amt);
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public void setHealth(long amt) {
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()){
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.getStats().setCurrentHealth(amt);
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public String getPassword() {
        StringBuilder shadowPwd = new StringBuilder();
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return "";
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        String password = playerMetadata.getPassword();
        for (int i = 0; i < password.length(); i++) {
            shadowPwd.append("*");
        }
        return shadowPwd.toString();
    }

    @Override
    public void setPassword(String password) {
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()){
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.setPassword(password);
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public void setMana(long amt) {
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()){
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.getStats().setCurrentMana(amt);
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public long getHealth() {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return 0L;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        return playerMetadata.getStats().getCurrentHealth();
    }

    @Override
    public void sendAdminMessage(String message) {
        gameManager.getChannelUtils().write(playerId, Color.BOLD_ON + Color.RED + "[ADMIN] " + message + Color.RESET + "\r\n", true);
    }

    @Override
    public long getMana() {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return 0L;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        return playerMetadata.getStats().getCurrentMana();
    }

    @Override
    public void setExperience(long amt) {
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()){
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.getStats().setExperience(amt);
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public long getExperience() {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return 0L;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        return playerMetadata.getStats().getExperience();
    }

    @Override
    public void setRoles(String roles) {
        String[] split = roles.split(",");
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()){
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.resetPlayerRoles();
            for (String roleType : split) {
                PlayerRole byType = PlayerRole.getByType(roleType);
                if (byType == null) {
                    continue;
                }
                playerMetadata.addPlayerRole(byType);
            }
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public String getRoles() {
        List<String> rolesList = Lists.newArrayList();
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return "";
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        Set<PlayerRole> playerRoleSet = playerMetadata.getPlayerRoleSet();
        for (PlayerRole next : playerRoleSet) {
            rolesList.add(next.getRoleType());
        }
        return Joiner.on(",").join(rolesList);
    }

    @Override
    public Map<String, String> getInventory() {
        Map<String, String> inventoryContents = Maps.newHashMap();
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return inventoryContents;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        List<String> inventory = playerMetadata.getInventory();
        for (String itemId : inventory) {
            Optional<Item> itemEntityOptional = gameManager.getEntityManager().getItemEntity(itemId);
            if (!itemEntityOptional.isPresent()) {
                continue;
            }
            Item itemEntity = itemEntityOptional.get();
            String itemName = itemEntity.getItemName();
            final String msgWithoutColorCodes =
                    itemName.replaceAll("\u001B\\[[;\\d]*m", "");
            inventoryContents.put(itemEntity.getItemId(), msgWithoutColorCodes);
        }
        return inventoryContents;
    }

    @Override
    public Map<String, String> getLockerInventory() {
        Map<String, String> inventoryContents = Maps.newHashMap();
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()){
            return inventoryContents;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        List<String> inventory = playerMetadata.getLockerInventory();
        for (String itemId : inventory) {
            Optional<Item> itemEntityOptional = gameManager.getEntityManager().getItemEntity(itemId);
            if (!itemEntityOptional.isPresent()) {
                continue;
            }
            Item itemEntity = itemEntityOptional.get();
            String itemName = itemEntity.getItemName();
            final String msgWithoutColorCodes =
                    itemName.replaceAll("\u001B\\[[;\\d]*m", "");
            inventoryContents.put(itemEntity.getItemId(), msgWithoutColorCodes);
        }
        return inventoryContents;
    }

    @Override
    public String createItemInInventory(String internalItemName){
        Optional<ItemMetadata> itemMetadata = gameManager.getItemStorage().get(internalItemName);
        if (!itemMetadata.isPresent()) {
            return "No such item exists with internal name: " + internalItemName;
        }
        Item item = new ItemBuilder().from(itemMetadata.get()).create();
        gameManager.getEntityManager().saveItem(item);
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()){
                return "";
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.addInventoryEntityId(item.getItemId());
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
        final String msgWithoutColorCodes = item.getItemName().replaceAll("\u001B\\[[;\\d]*m", "");
        return msgWithoutColorCodes + " created.";
    }

    @Override
    public void setPlayerClass(String playerClassName) {
        List<PlayerClass> collect = Arrays.stream(PlayerClass.values()).filter(playerClass -> playerClass.getIdentifier().equalsIgnoreCase(playerClassName)).collect(Collectors.toList());
        if (collect.size() == 0) {
            return;
        }
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.setPlayerClass(collect.get(0));
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }

    }

    @Override
    public String getPlayerClass() {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()) {
            return "";
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        return playerMetadata.getPlayerClass().getIdentifier();
    }

    @Override
    public void clearAllCoolDowns() {
        synchronized (findInterner().intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.resetCoolDowns();

            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    @Override
    public void detain() {
        synchronized (findInterner().intern(playerId)) {
            gameManager.detainPlayer(gameManager.getPlayerManager().getPlayer(playerId));
        }
    }

    private Interner<String> findInterner() {
        Player player = gameManager.getPlayerManager().getPlayer(playerId);
        Interner<String> interner;
        if (player == null) {
            interner = Interners.newWeakInterner();
        } else {
            interner = player.getInterner();
        }
        return interner;
    }
}