package com.comandante.creeper;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.managers.PlayerManager;
import com.comandante.creeper.managers.RoomManager;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.server.CreeperServer;
import com.google.common.base.Optional;

public class Main {

    public static void main(String[] args) throws Exception {
        Room lobby = new Room(1, Optional.of(2), Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.<Integer>absent(),
                "This is the lobby. It's pretty empty and the paint still smells fresh.");
        Room hallway = new Room(2, Optional.of(3), Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.of(1),
                "This is the hallway. It's long and hallway-ish with exposed wires and floorboards showing.");
        Room intake = new Room(3, Optional.<Integer>absent(), Optional.of(6), Optional.<Integer>absent(), Optional.of(2),
                "This is the intake area.  People are lined up like cattle waiting to be prodded.");
        Room janitorialCloset = new Room(6, Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.of(3), Optional.<Integer>absent(),
                "You find yourself in the janitorial closet.  It smells like bleach.");

        RoomManager roomManager = new RoomManager();
        roomManager.addRoom(lobby);
        roomManager.addRoom(hallway);
        roomManager.addRoom(intake);
        roomManager.addRoom(janitorialCloset);
        PlayerManager playerManager = new PlayerManager();
        GameManager gameManager = new GameManager(roomManager, playerManager);

        CreeperServer creeperServer = new CreeperServer(8080);
        creeperServer.run(gameManager);

        System.out.println("Creeper started.");
    }
}
