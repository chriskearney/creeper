package com.comandante.creeper.server.command;

import com.comandante.creeper.Main;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.fight.FightManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.CreeperSession;
import com.comandante.creeper.world.Coords;
import com.comandante.creeper.world.FloorManager;
import com.comandante.creeper.world.MapMatrix;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.world.RoomManager;
import com.comandante.creeper.world.WorldExporter;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command extends SimpleChannelUpstreamHandler {

    public final List<String> validTriggers;
    public final String description;
    public final boolean isAdminCommand;

    public final GameManager gameManager;
    public final FloorManager floorManager;
    public final MapsManager mapsManager;
    public final EntityManager entityManager;
    public final RoomManager roomManager;
    public final PlayerManager playerManager;
    public final ChannelUtils channelUtils;
    public final FightManager fightManager;
    public CreeperSession creeperSession;
    public Player player;
    public Room currentRoom;
    public String playerId;
    public MapMatrix mapMatrix;
    public Coords currentRoomCoords;
    public List<String> originalMessageParts;
    public WorldExporter worldExporter;
    public PlayerMetadata playerMetadata;


    protected Command(GameManager gameManager, List<String> validTriggers, String description) {
        this(gameManager, validTriggers, description, false);
    }

    protected Command(GameManager gameManager, List<String> validTriggers, String description, boolean isAdminCommand) {
        this.gameManager = gameManager;
        this.validTriggers = validTriggers;
        this.description = description;
        this.isAdminCommand = isAdminCommand;
        this.floorManager = gameManager.getFloorManager();
        this.mapsManager = gameManager.getMapsManager();
        this.roomManager = gameManager.getRoomManager();
        this.entityManager = gameManager.getEntityManager();
        this.playerManager = gameManager.getPlayerManager();
        this.channelUtils = gameManager.getChannelUtils();
        this.fightManager = gameManager.getFightManager();
        this.worldExporter = new WorldExporter(roomManager, mapsManager, floorManager, entityManager);
    }

    public void configure(MessageEvent e) {
        this.creeperSession = extractCreeperSession(e.getChannel());
        this.player = playerManager.getPlayer(extractPlayerId(creeperSession));
        this.playerId = player.getPlayerId();
        this.currentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
        this.mapMatrix  = mapsManager.getFloorMatrixMaps().get(currentRoom.getFloorId());
        this.currentRoomCoords = mapMatrix.getCoords(currentRoom.getRoomId());
        this.originalMessageParts = getOriginalMessageParts(e);
        this.playerMetadata = gameManager.getPlayerManager().getPlayerMetadata(playerId);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        e.getChannel().getPipeline().remove(ctx.getHandler());
        String playerId = extractPlayerId(extractCreeperSession(e.getChannel()));
        String prompt = gameManager.getPlayerManager().buildPrompt(playerId);
        gameManager.getChannelUtils().write(playerId, prompt, true);
        super.messageReceived(ctx, e);
    }

    public CreeperSession extractCreeperSession(Channel channel) {
        return (CreeperSession) channel.getAttachment();
    }


    public String extractPlayerId(CreeperSession creeperSession) {
        return Main.createPlayerId(creeperSession.getUsername().get());
    }

    public String getRootCommand(MessageEvent e){
        String origMessage = (String) e.getMessage();
        return origMessage.split(" ")[0].toLowerCase();
    }

    public List<String> getOriginalMessageParts(MessageEvent e) {
        String origMessage = (String) e.getMessage();
        return new ArrayList<>(Arrays.asList(origMessage.split(" ")));
    }

    public void write(String msg) {
        channelUtils.write(playerId, msg);
    }

    public void write(String msg, boolean leadingBlankLine) {
        channelUtils.write(playerId, msg, leadingBlankLine);
    }

    public void currentRoomLogic(){
        gameManager.currentRoomLogic(playerId);
    }

    public void printMap() {
        write(currentRoom.getMapData().get());
    }

    public String getPrompt() {
        return playerManager.buildPrompt(playerId);
    }
}