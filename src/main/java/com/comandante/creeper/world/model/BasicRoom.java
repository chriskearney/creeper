package com.comandante.creeper.world.model;

import com.comandante.creeper.core_game.GameManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BasicRoom extends Room {
    @Override
    public void run() {
        super.run();
        // System.out.println(getRoomTitle() + " tick.");
    }

    public BasicRoom(Integer roomId,
                     String roomTitle,
                     Integer floorId,
                     Optional<Integer> northId,
                     Optional<Integer> southId,
                     Optional<Integer> eastId,
                     Optional<Integer> westId,
                     Optional<Integer> upId,
                     Optional<Integer> downId,
                     List<RemoteExit> enterExits,
                     String roomDescription,
                     Set<String> roomTags,
                     Set<Area> areas,
                     Map<String, String> notables,
                     Set<String> requiredInternalItemNames,
                     Optional<Integer> minimumLevel,
                     GameManager gameManager) {
        super(roomId, roomTitle, floorId, northId, southId, eastId, westId, upId, downId, enterExits, roomDescription, roomTags, areas, notables, requiredInternalItemNames, minimumLevel, gameManager);
    }
}
