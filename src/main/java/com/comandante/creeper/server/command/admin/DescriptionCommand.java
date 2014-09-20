package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.CreeperEntry;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.server.MultiLineInputManager;
import com.comandante.creeper.server.command.Command;
import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DescriptionCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("desc", "description");
    final static String description = "Set the description command. For admins only.";
    final static boolean isAdminOnly = true;

    public DescriptionCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (creeperSession.getGrabMultiLineInput().isPresent()) {
                MultiLineInputManager multiLineInputManager = gameManager.getMultiLineInputManager();
                UUID uuid = creeperSession.getGrabMultiLineInput().get().getKey();
                String multiLineInput = multiLineInputManager.retrieveMultiLineInput(uuid);
                currentRoom.setRoomDescription(multiLineInput);
                creeperSession.setGrabMultiLineInput(Optional.<CreeperEntry<UUID, Command>>absent());
                return;
            }
            write("You are now in multi-line mode.  Type \"done\" on an empty line to exit and save.\r\n");
            creeperSession.setGrabMultiLineInput(Optional.of(
                    new CreeperEntry<UUID, Command>(gameManager.getMultiLineInputManager().createNewMultiLineInput(), this)));
        } finally {
           super.messageReceived(ctx, e);
        }
    }
}
