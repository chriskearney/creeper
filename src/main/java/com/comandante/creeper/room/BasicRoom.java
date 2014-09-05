package com.comandante.creeper.room;

import com.google.common.base.Optional;

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
                     String roomDescription,
                     Set<String> roomTags) {
        super(roomId, roomTitle, floorId, northId, southId, eastId, westId, upId, downId, roomDescription, roomTags);
    }
}
