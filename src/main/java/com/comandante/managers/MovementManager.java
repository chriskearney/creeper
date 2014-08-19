package com.comandante.managers;

import com.comandante.command.CommandType;
import com.comandante.model.Movement;
import com.comandante.model.Player;
import com.comandante.model.Room;
import com.google.common.base.Optional;
import org.jboss.netty.channel.MessageEvent;

public class MovementManager {

    public static Optional<Movement> moveNorth(CommandType cmdType, Player player, Room currentRoom, MessageEvent event) {
        if (!currentRoom.getNorthId().isPresent()) {
            event.getChannel().write("There's no northern exit.\r\n");
            return Optional.absent();
        }
        return Optional.of(
                new Movement(player, currentRoom.getRoomId(), currentRoom.getNorthId().get(), cmdType));
    }

    public static Optional<Movement> moveSouth(CommandType cmdType, Player player, Room currentRoom, MessageEvent event) {
        if (!currentRoom.getSouthId().isPresent()) {
            event.getChannel().write("There's no southern exit.\r\n");
            return Optional.absent();
        }
        return Optional.of(
                new Movement(player, currentRoom.getRoomId(), currentRoom.getSouthId().get(), cmdType));
    }

    public static Optional<Movement> moveEast(CommandType cmdType, Player player, Room currentRoom, MessageEvent event) {
        if (!currentRoom.getEastId().isPresent()) {
            event.getChannel().write("There's no eastern exit.\r\n");
            return Optional.absent();
        }
        return Optional.of(
                new Movement(player, currentRoom.getRoomId(), currentRoom.getEastId().get(), cmdType));
    }
    
    public static Optional<Movement> moveWest(CommandType cmdType, Player player, Room currentRoom, MessageEvent event) {
        if (!currentRoom.getWestId().isPresent()) {
            event.getChannel().write("There's no western exit.\r\n");
            return Optional.absent();
        }
        return Optional.of(
                new Movement(player, currentRoom.getRoomId(), currentRoom.getWestId().get(), cmdType));
    }
}
