package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TitleCommand extends Command {


    final static List<String> validTriggers = Arrays.asList("title");
    final static String description = "Set the title on the current room.";
    final static String correctUsage = "title <new title>";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);

    public TitleCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            originalMessageParts.remove(0);
            currentRoom.setRoomTitle(Joiner.on(" ").join(originalMessageParts));
            write("Titled saved.");
        });
    }
}
