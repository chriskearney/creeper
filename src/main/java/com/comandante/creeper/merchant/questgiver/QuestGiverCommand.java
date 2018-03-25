package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.server.model.CreeperSession;
import com.comandante.creeper.server.player_communication.ChannelCommunicationUtils;
import com.comandante.creeper.world.model.Room;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestGiverCommand extends SimpleChannelUpstreamHandler {

    public final List<String> validTriggers;
    public final String description;
    public final GameManager gameManager;
    public final PlayerManager playerManager;
    public final ChannelCommunicationUtils channelUtils;
    public CreeperSession creeperSession;
    public Player player;
    public String playerId;
    public Room currentRoom;
    public List<String> originalMessageParts;
    public String rootCommand;
    private Merchant merchant;

    public static String PIPELINE_NAME = "executed_questgiver_command";

    public QuestGiverCommand(GameManager gameManager, Merchant merchant, List<String> validTriggers, String description) {
        this.gameManager = gameManager;
        this.playerManager = gameManager.getPlayerManager();
        this.channelUtils = gameManager.getChannelUtils();
        this.validTriggers = validTriggers;
        this.description = description;
        this.merchant = merchant;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession creeperSession = extractCreeperSession(e.getChannel());
            e.getChannel().getPipeline().remove("executed_command");
            e.getChannel().getPipeline().remove(PIPELINE_NAME);
//            write(getMerchant().getWelcomeMessage() + "\r\n");
//            write(getMerchant().getQuestsMenu() + "\r\n");
            write(getPrompt());
//            gameManager.getChannelUtils().write(playerId, getPrompt(), true);
            if (creeperSession.getGrabMerchant().isPresent()) {
                return;
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    public void configure(MessageEvent e) {
        this.creeperSession = extractCreeperSession(e.getChannel());
        this.player = playerManager.getPlayer(extractPlayerId(creeperSession));
        this.playerId = player.getPlayerId();
        this.currentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
        this.originalMessageParts = getOriginalMessageParts(e);
        rootCommand = getRootCommand(e);
    }

    public CreeperSession extractCreeperSession(Channel channel) {
        return (CreeperSession) channel.getAttachment();
    }


    public String extractPlayerId(CreeperSession creeperSession) {
        return Creeper.createPlayerId(creeperSession.getUsername().get());
    }

    public String getRootCommand(MessageEvent e) {
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

    public static String getPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append( "[QUESTS - LIST | REVIEW <#> | ACCEPT <#> | COMPLETE | LEAVE] ");
        return sb.toString();
    }

    public <T> T createObj(Merchant merchant, String nameclass) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Class<T> clazz = (Class<T>) Class.forName(nameclass);

        T t = clazz.getConstructor(Merchant.class, GameManager.class).newInstance(merchant, gameManager);

        // assumes the target class has a no-args Constructor
        return t;
    }


    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}
