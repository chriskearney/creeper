package com.comandante.creeper.api;

import com.codahale.metrics.annotation.Timed;
import com.comandante.creeper.command.commands.MovementCommand;
import com.comandante.creeper.common.CreeperUtils;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.CreeperClientStatusBarDetails;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.stats.Levels;
import com.comandante.creeper.world.model.Coords;
import com.comandante.creeper.world.model.RemoteExit;
import com.comandante.creeper.world.model.Room;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import events.CreeperToSSEEventListener;
import events.ListenerService;
import io.dropwizard.auth.Auth;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.annotation.security.PermitAll;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource {

    private final GameManager gameManager;
    private final ListenerService listenerService;
    private final ObjectMapper objectMapper;

    public ApiResource(GameManager gameManager, ObjectMapper objectMapper) {
        this.gameManager = gameManager;
        this.listenerService = gameManager.getListenerService();
        this.objectMapper = objectMapper;
    }

    @GET
    @Timed
    @PermitAll
    @Path("/clientdata")
    public CreeperClientData getClientData(@Auth Player player) {
        final Room playerCurrentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
        String map = gameManager.getMapsManager().drawMap(playerCurrentRoom.getRoomId(), new Coords(10, 10));

        String prompt = gameManager.buildPrompt(player.getPlayerId());

        String lookString = player.getLookString();

        List<String> rolledUpInventory = player.getRolledUpInventory();
        StringJoiner stringJoiner = new StringJoiner("\n");
        for (String s : rolledUpInventory) {
            stringJoiner.add(s);
        }
        String inventory = stringJoiner.toString();

        List<String> recent = gameManager.getGossipCache().getRecent(15);
        StringJoiner gossipJoiner = new StringJoiner("\n");
        for (String s : recent) {
            gossipJoiner.add(s);
        }
        String gossip = gossipJoiner.toString();

        CreeperClientStatusBarDetails clientStatusBarDetails = player.getClientStatusBarDetails();

        return new CreeperClientData(map, prompt, lookString, inventory, gossip, clientStatusBarDetails);
    }


    @GET
    @Path("/events")
    @PermitAll
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput streaming(@Auth Player player) {
        final EventOutput eventOutput = new EventOutput();

        listenerService.registerListener(new CreeperToSSEEventListener(player.getPlayerId(), eventOutput, objectMapper));

        return eventOutput;
    }

    @POST
    @Path("/gossip")
    @PermitAll
    public void gossip(@Auth Player player, @FormParam("message") String message) {
        gameManager.gossip(player, message, true);
    }

    @POST
    @Path("/use")
    @PermitAll
    public void use(@Auth Player player, @FormParam("itemId") String itemId) {
        Optional<Item> inventoryItemById = player.getInventoryItemById(itemId);
        gameManager.getItemUseHandler().handle(player, inventoryItemById.get(), Optional.empty());
    }

    @POST
    @Path("/drop")
    @PermitAll
    public void drop(@Auth Player player, @FormParam("itemId") String itemId) {
        player.dropItem(itemId, true);
    }

    @POST
    @Path("/equip")
    @PermitAll
    public void equip(@Auth Player player, @FormParam("itemId") String itemId) {
        Optional<Item> inventoryItemById = player.getInventoryItemById(itemId);
        inventoryItemById.ifPresent(item -> player.equip(item, true));
    }

    @POST
    @Path("/show")
    @PermitAll
    public void show(@Auth Player player, @FormParam("itemId") String itemId) {
        player.show(itemId);
    }

    @POST
    @Path("/attack")
    @PermitAll
    public void attack(@Auth Player player, @FormParam("npcId") String npcId) {
        Npc npcEntity = gameManager.getEntityManager().getNpcEntity(npcId);
        if (npcEntity != null) {
            if (player.getCurrentRoom().getNpcIds().contains(npcEntity.getEntityId())) {
                if (player.addActiveFight(npcEntity)) {
                    gameManager.writeToPlayerCurrentRoom(player.getPlayerId(), player.getPlayerName() + " has attacked a " + npcEntity.getColorName());
                }
            }
        }
    }

    @POST
    @Path("/look")
    @PermitAll
    public void look(@Auth Player player, @FormParam("npcId") String npcId, @FormParam("playerId") String playerId) {
        if (!Strings.isNullOrEmpty(npcId)) {
            Npc npcEntity = gameManager.getEntityManager().getNpcEntity(npcId);
            if (npcEntity != null) {
                if (player.getCurrentRoom().getNpcIds().contains(npcEntity.getEntityId())) {
                    gameManager.getChannelUtils().write(player.getPlayerId(), gameManager.getLookString(npcEntity, Levels.getLevel(gameManager.getStatsModifierFactory().getStatsModifier(player).getExperience())) + "\r\n");
                }
            }
        }

        if (!Strings.isNullOrEmpty(playerId)) {
            Player foundPlayer = gameManager.getPlayerManager().getPlayer(playerId);
            if (foundPlayer != null) {
                gameManager.getChannelUtils().write(player.getPlayerId(), foundPlayer.getLookString() + "\r\n", true);
                if (!foundPlayer.getPlayerId().equals(playerId)) {
                    gameManager.getChannelUtils().write(foundPlayer.getPlayerId(), player.getPlayerName() + " looks at you.", true);
                }
            }
        }
    }

    @POST
    @Path("/pick")
    @PermitAll
    public void pick(@Auth Player player, @FormParam("itemId") String itemId) {
        if (!Strings.isNullOrEmpty(itemId)) {
            if (gameManager.acquireItemFromRoom(player, itemId)) {
                String playerName = player.getPlayerName();
                Optional<Item> itemEntity = gameManager.getEntityManager().getItemEntity(itemId);
                gameManager.roomSay(player.getCurrentRoom().getRoomId(), playerName + " picked up " + itemEntity.get().getItemName(), player.getPlayerId(), true);
            }
        }
    }

    @POST
    @Path("/compare")
    @PermitAll
    public void compare(@Auth Player player, @FormParam("playerId") String playerId) {
        if (!Strings.isNullOrEmpty(playerId)) {
            Player foundPlayer = gameManager.getPlayerManager().getPlayer(playerId);
            if (foundPlayer == null) {
                return;
            }
            String targetLookString = foundPlayer.getLookString();
            gameManager.getChannelUtils().write(player.getPlayerId(), CreeperUtils.printStringsNextToEachOther(Lists.newArrayList(player.getLookString(), targetLookString), " | ")+ "\r\n", true);
        }
    }

    @POST
    @Path("/move")
    @PermitAll
    public void move(@Auth Player player, @FormParam("direction") String dir) {
        Optional<Room.Direction> from = Room.Direction.from(dir);
        if (!from.isPresent()) {
            return;
        }

        Optional<PlayerMovement> playerMovement = player.getCurrentRoom().derivePlayerMovement(player, from.get());
        if (!playerMovement.isPresent()) {
            return;
        }
        player.movePlayer(playerMovement.get());
    }
}