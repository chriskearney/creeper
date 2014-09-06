package com.comandante.creeper.npc;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.world.Area;
import com.comandante.creeper.world.Room;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class NpcMover {

    static Random random = new Random();

    public void roam(final GameManager gameManager, String npcId) {
        final Npc npcEntity = gameManager.getEntityManager().getNpcEntity(npcId);
        Room npcCurrentRoom = gameManager.getRoomManager().getNpcCurrentRoom(npcEntity).get();
        Set<Integer> possibleExits = getPossibleExits(npcCurrentRoom);
        Predicate<Integer> isRoamable = new Predicate<Integer>() {
            @Override
            public boolean apply(Integer roomId) {
                Room room = gameManager.getRoomManager().getRoom(roomId);
                for (Area roomArea : room.getAreas()) {
                    if (npcEntity.getRoamAreas().get().contains(roomArea)) {
                        return true;
                    }
                }
                return false;
            }
        };
        List<Integer> canRoam = Lists.newArrayList(Iterables.filter(possibleExits, isRoamable));
        Integer destinationRoomId = canRoam.get(random.nextInt(canRoam.size()));
        String exitMessage = getExitMessage(npcCurrentRoom, destinationRoomId);
        npcCurrentRoom.getNpcIds().remove(npcId);
        gameManager.roomSay(npcCurrentRoom.getRoomId(), npcEntity.getColorName() + " " + exitMessage, "");
        gameManager.getRoomManager().getRoom(destinationRoomId).getNpcIds().add(npcId);
        gameManager.roomSay(destinationRoomId, npcEntity.getColorName() + " has arrived.", "");
    }

    public String getExitMessage(Room room, Integer exitRoomId) {
        if (room.getUpId().isPresent() && room.getUpId().get().equals(exitRoomId)) {
            return "exited up.";
        }
        if (room.getDownId().isPresent() && room.getDownId().get().equals(exitRoomId)) {
            return "exited down.";
        }
        if (room.getNorthId().isPresent() && room.getNorthId().get().equals(exitRoomId)) {
            return "exited to the north.";
        }
        if (room.getSouthId().isPresent() && room.getSouthId().get().equals(exitRoomId)) {
            return "exited to the south.";
        }
        if (room.getEastId().isPresent() && room.getEastId().get().equals(exitRoomId)) {
            return "exited to the east.";
        }
        if (room.getWestId().isPresent() && room.getWestId().get().equals(exitRoomId)) {
            return "exited to the west..";
        }
        return "";
    }

    public Set<Integer> getPossibleExits(Room room) {
        List<Optional<Integer>> opts = Lists.newArrayList();
        opts.add(room.getDownId());
        opts.add(room.getUpId());
        opts.add(room.getNorthId());
        opts.add(room.getSouthId());
        opts.add(room.getEastId());
        opts.add(room.getWestId());

        Set<Integer> exits = Sets.newHashSet();
        for (Optional<Integer> opt: opts) {
            if (opt.isPresent()){
                exits.add(opt.get());
            }
        }
        return exits;
    }

}
