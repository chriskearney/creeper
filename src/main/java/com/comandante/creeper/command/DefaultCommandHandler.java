package com.comandante.creeper.command;


import com.comandante.creeper.managers.MovementManager;
import com.comandante.creeper.model.Movement;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Optional;
import org.jboss.netty.channel.MessageEvent;

public class DefaultCommandHandler {

    private final GameManager gameManager;
    private final CreeperSession creeperSession;
    private final MessageEvent event;

    public DefaultCommandHandler(GameManager gameManager, CreeperSession creeperSession, MessageEvent event) {
        this.gameManager = gameManager;
        this.creeperSession = creeperSession;
        this.event = event;
    }

    public void handle(DefaultCommandType cmdType) {
        final String originalMessage = cmdType.getOriginalMessage();
        final Player player = _player();
        Optional<Movement> movement = Optional.absent();

        switch(cmdType) {
            case MOVE_NORTH:
                movement = MovementManager.moveNorth(cmdType, player, _currentRoom(), event);
                break;
            case MOVE_SOUTH:
                movement = MovementManager.moveSouth(cmdType, player, _currentRoom(), event);
                break;
            case MOVE_EAST:
                movement = MovementManager.moveEast(cmdType, player, _currentRoom(), event);
                break;
            case MOVE_WEST:
                movement = MovementManager.moveWest(cmdType, player, _currentRoom(), event);
                break;
            case SAY:
                gameManager.say(player, originalMessage.replaceFirst("^say ", ""));
                break;
            case TELL:
                gameManager.tell(player, originalMessage);
                break;
            case GOSSIP:
                gameManager.gossip(player, originalMessage.replaceFirst("^gossip ", ""));
                break;
            case WHO:
                gameManager.who(player);
                break;
            case WHOAMI:
                player.getChannel().write(player.getPlayerName() + "\r\n");
                break;
            case HELP:
                gameManager.getHelpManager().printHelp(player, originalMessage);
                break;
            case UNKNOWN:
                gameManager.currentRoomLogic(creeperSession, event);
                break;
        }

        if (movement.isPresent()) {
            gameManager.movePlayer(movement.get());
            gameManager.currentRoomLogic(creeperSession, event);
        }
    }

    private Player _player() {
        return gameManager.getPlayerManager().getPlayerByUsername(creeperSession.getUsername().get());
    }

    private Room _currentRoom() {
        return gameManager.getPlayerCurrentRoom(_player()).get();
    }

}
