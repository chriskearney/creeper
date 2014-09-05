package com.comandante.creeper.server.command;

import com.comandante.creeper.CreeperEntry;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.room.Room;
import com.comandante.creeper.server.CreeperSession;
import com.comandante.creeper.server.MultiLineInputManager;
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
        try {
            GameManager gameManager = getGameManager();
            CreeperSession session = extractCreeperSession(e.getChannel());
            Player player = gameManager.getPlayerManager().getPlayer(getPlayerId(session));
            if (session.getGrabMultiLineInput().isPresent()) {
                MultiLineInputManager multiLineInputManager = gameManager.getMultiLineInputManager();
                UUID uuid = session.getGrabMultiLineInput().get().getKey();
                String multiLineInput = multiLineInputManager.retrieveMultiLineInput(uuid);
                Room playerCurrentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
                playerCurrentRoom.setRoomDescription(multiLineInput);
                session.setGrabMultiLineInput(Optional.<CreeperEntry<UUID, Command>>absent());
                return;
            }
            final String playerId = getPlayerId(session);
            gameManager.getChannelUtils().writeNoPrompt(playerId, "\n\n ENTERING MULTI LINE INPUT MODE.  TYPE \"DONE\" ON AN EMPTY LINE TO EXIT");
            session.setGrabMultiLineInput(Optional.of(
                    new CreeperEntry<UUID, Command>(gameManager.getMultiLineInputManager().createNewMultiLineInput(), this)));
            //e.getChannel().getPipeline().addLast("multi_line", new MultiLineInputHandler(gameManager));
        } finally {
           super.messageReceived(ctx, e);
        }
    }
}
