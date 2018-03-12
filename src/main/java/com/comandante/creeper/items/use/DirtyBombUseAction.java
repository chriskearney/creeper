package com.comandante.creeper.items.use;

import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.items.ItemUseAction;
import com.comandante.creeper.items.ItemUseHandler;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcStatsChangeBuilder;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.stats.StatsBuilder;
import com.comandante.creeper.world.model.Room;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class DirtyBombUseAction implements ItemUseAction {

    private final ItemMetadata itemMetadata;

    public DirtyBombUseAction(ItemMetadata itemMetadata) {
        this.itemMetadata = itemMetadata;
    }

    @Override
    public String getInternalItemName() {
        return itemMetadata.getInternalItemName();
    }

    @Override
    public void executeAction(GameManager gameManager, Player player, Item item, Optional<UseCommand.UseItemOn> useItemOn) {
        Room currentRoom = player.getCurrentRoom();
        if (currentRoom.getRoomId().equals(1)) {
            gameManager.writeToPlayerCurrentRoom(player.getPlayerId(), player.getPlayerName() + " tried to detonate a " + item.getItemName() + "!");
            return;
        }
        Set<String> npcIds = currentRoom.getNpcIds();
        for (String npcId : npcIds) {
            Npc npc = gameManager.getEntityManager().getNpcEntity(npcId);
            gameManager.writeToPlayerCurrentRoom(player.getPlayerId(), npc.getColorName() + " is heavily damaged by a " + item.getItemName() + "!" + Color.YELLOW + " +" + NumberFormat.getNumberInstance(Locale.US).format(900000000) + Color.RESET + Color.BOLD_ON + Color.RED + " DAMAGE" + Color.RESET);
            NpcStatsChangeBuilder npcStatsChangeBuilder = new NpcStatsChangeBuilder();
            final String fightMsg = Color.BOLD_ON + Color.RED + "[attack] " + Color.RESET + Color.YELLOW + " +" + NumberFormat.getNumberInstance(Locale.US).format(900000000) + Color.RESET + Color.BOLD_ON + Color.RED + " DAMAGE" + Color.RESET + " done to " + npc.getColorName();
            npcStatsChangeBuilder.setStats(new StatsBuilder().setCurrentHealth(-900000000).createStats());
            npcStatsChangeBuilder.setDamageStrings(Arrays.asList(fightMsg));
            npcStatsChangeBuilder.setPlayer(player);
            npcStatsChangeBuilder.setIsItemDamage(true);
            npc.addNpcDamage(npcStatsChangeBuilder.createNpcStatsChange());
        }
        Set<Player> presentPlayers = currentRoom.getPresentPlayers();
        for (Player presentPlayer : presentPlayers) {
            if (allRadSuit(presentPlayer)) {
                gameManager.writeToPlayerCurrentRoom(player.getPlayerId(), presentPlayer.getPlayerName() + " is immune to " + item.getItemName() + "!");
                continue;
            }
            gameManager.writeToPlayerCurrentRoom(player.getPlayerId(), presentPlayer.getPlayerName() + " is heavily damaged by a " + item.getItemName() + "!");
            presentPlayer.updatePlayerHealth(-Long.MAX_VALUE, null);
        }
    }

    @Override
    public void postExecuteAction(GameManager gameManager, Player player, Item item) {
        ItemUseHandler.incrementUses(item);
        if (item.isDisposable()) {
            if (item.getNumberOfUses() < item.getMaxUses()) {
                gameManager.getEntityManager().saveItem(item);
            } else {
                player.removeInventoryId(item.getItemId());
                gameManager.getEntityManager().removeItem(item);
            }
        }
    }

    @Override
    public Set<Effect> getEffects() {
        return null;
    }

    private boolean allRadSuit(Player player) {
 /*       if (player.getSlotItem(EquipmentSlotType.CHEST) == null || !Objects.equals(player.getSlotItem(EquipmentSlotType.CHEST).getItemTypeId(), ItemType.RADSUIT_CHESTPLATE.getItemTypeCode())) {
            return false;
        }
        if (player.getSlotItem(EquipmentSlotType.LEGS)  == null || !Objects.equals(player.getSlotItem(EquipmentSlotType.LEGS).getItemTypeId(), ItemType.RADSUIT_LEGGINGS.getItemTypeCode())) {
            return false;
        }
        if (player.getSlotItem(EquipmentSlotType.HEAD)  == null || !Objects.equals(player.getSlotItem(EquipmentSlotType.HEAD).getItemTypeId(), ItemType.RADSUIT_HELMET.getItemTypeCode())) {
            return false;
        }
        if (player.getSlotItem(EquipmentSlotType.WRISTS)  == null || !Objects.equals(player.getSlotItem(EquipmentSlotType.WRISTS).getItemTypeId(), ItemType.RADSUIT_BRACERS.getItemTypeCode())) {
            return false;
        }
        if (player.getSlotItem(EquipmentSlotType.FEET) == null || !Objects.equals(player.getSlotItem(EquipmentSlotType.FEET).getItemTypeId(), ItemType.RADSUIT_BOOTS.getItemTypeCode())) {
            return false;
        }*/
        return true;

    }
}
