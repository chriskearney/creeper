package com.comandante.command;


import com.comandante.model.Movement;
import com.comandante.managers.GameManager;
import com.comandante.model.Player;
import com.comandante.model.Room;
import com.comandante.server.CreeperSessionState;
import com.google.common.base.Optional;
import org.jboss.netty.channel.MessageEvent;

public class DefaultCommandHandler {

    GameManager gameManager;
    CreeperSessionState creeperSessionState;
    MessageEvent event;

    public DefaultCommandHandler(GameManager gameManager, CreeperSessionState creeperSessionState, MessageEvent event) {
        this.gameManager = gameManager;
        this.creeperSessionState = creeperSessionState;
        this.event = event;
    }

    public void handle(DefaultCommandType cmdType) {
        final String originalMessage = cmdType.getOriginalMessage();
        final Player player = _player();
        Optional<Movement> movementOptional = Optional.absent();
        Room currentRoom = null;

        switch(cmdType) {
            case MOVE_NORTH:
                currentRoom = _currentRoom();
                if (!currentRoom.getNorthId().isPresent()) {
                    event.getChannel().write("There's no northern exit.\r\n");
                    break;
                }
                movementOptional = Optional.of(
                        new Movement(player, currentRoom.getRoomId(), currentRoom.getNorthId().get(), cmdType));
                break;
            case MOVE_SOUTH:
                currentRoom = _currentRoom();
                if (!currentRoom.getSouthId().isPresent()) {
                    event.getChannel().write("There's no southern exit.\r\n");
                    break;
                }
                movementOptional = Optional.of(
                        new Movement(player, currentRoom.getRoomId(), currentRoom.getSouthId().get(), cmdType));
                break;
            case MOVE_EAST:
                currentRoom = _currentRoom();
                if (!currentRoom.getEastId().isPresent()) {
                    event.getChannel().write("There's no eastern exit.\r\n");
                    break;
                }
                movementOptional = Optional.of(
                        new Movement(player, currentRoom.getRoomId(), currentRoom.getEastId().get(), cmdType));
                break;
            case MOVE_WEST:
                currentRoom = _currentRoom();
                if (!currentRoom.getWestId().isPresent()) {
                    event.getChannel().write("There's no western exit.\r\n");
                    break;
                }
                movementOptional = Optional.of(
                        new Movement(player, currentRoom.getRoomId(), currentRoom.getWestId().get(), cmdType));
                break;
            case SAY:
                gameManager.say(player, originalMessage.replaceFirst("^say ", ""));
                break;
            case GOSSIP:
                String s = originalMessage.replaceFirst("^gossip ", "");
                gameManager.gossip(player, s);
                break;
            case UNKNOWN:
                gameManager.currentRoomLogic(creeperSessionState, event);
                break;
        }

        if (movementOptional.isPresent()) {
            gameManager.movePlayer(movementOptional.get());
            gameManager.currentRoomLogic(creeperSessionState, event);
        }
    }

    private Player _player() {
        return gameManager.getPlayerManager().getPlayer(creeperSessionState.getUsername().get());
    }

    private Room _currentRoom() {
        return gameManager.getPlayerCurrentRoom(_player()).get();
    }

}
