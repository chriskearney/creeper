package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.common.CreeperEntry;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.comandante.creeper.server.multiline.MultiLineInputManager;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class DescriptionCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("desc", "description");
    final static String description = "Edit the description for the current room.";
    final static String correctUsage = "description";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);


    public DescriptionCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        DescriptionCommand descriptionCommand = this;
        execCommand(ctx, e, () -> {
            if (creeperSession.getGrabMultiLineInput().isPresent()) {
                MultiLineInputManager multiLineInputManager = gameManager.getMultiLineInputManager();
                UUID uuid = creeperSession.getGrabMultiLineInput().get().getKey();
                String multiLineInput = multiLineInputManager.retrieveMultiLineInput(uuid);
                currentRoom.setRoomDescription(multiLineInput);
                creeperSession.setGrabMultiLineInput(Optional.empty());
                return;
            }
            if (originalMessageParts.size() > 1) {
                String possibleNotable = originalMessageParts.get(1);
                for (Map.Entry<String, String> notable : currentRoom.getNotables().entrySet()) {
                    if (notable.getKey().equalsIgnoreCase(possibleNotable)) {
                        if (originalMessageParts.size() > 2) {
                            originalMessageParts.remove(0);
                            originalMessageParts.remove(0);
                            String newDesc = Joiner.on(" ").join(originalMessageParts);
                            notable.setValue(newDesc);
                            write("Notable: "  + notable.getKey() + " description is set to: " + newDesc);
                            return;
                        }
                    }
                }
            }
            write("You are now in multi-line mode.  Type \"done\" on an empty line to exit and save.\r\n");
            creeperSession.setGrabMultiLineInput(Optional.of(
                    new CreeperEntry<>(gameManager.getMultiLineInputManager().createNewMultiLineInput(), descriptionCommand)));
        });
    }
}
