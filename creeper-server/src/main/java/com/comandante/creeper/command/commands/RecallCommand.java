package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.CoolDown;
import com.comandante.creeper.player.CoolDownType;
import com.comandante.creeper.player.PlayerMovement;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class RecallCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("recall");
    final static String description = "Return to the lobby, once every 5 minutes.";
    final static String correctUsage = "back";

    public RecallCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (player.isActive(CoolDownType.DETAINMENT)) {
                write("You can not recall while being detained.");
                return;
            }
            if (player.isActiveFights()) {
                write("You can not recall while in a fight.");
                return;
            }
            if (player.isActive(CoolDownType.PLAYER_RECALL)) {
                write("You can not recall right now.");
                return;
            }
            PlayerMovement playerMovement = new PlayerMovement(player, player.getCurrentRoom().getRoomId(), GameManager.LOBBY_ID, "vanished into the ether.");
            player.addCoolDown(new CoolDown(CoolDownType.PLAYER_RECALL));
            player.movePlayer(playerMovement);
        });
    }

}