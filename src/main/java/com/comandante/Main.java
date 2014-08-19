package com.comandante;

import com.comandante.telnetserver.TelnetServer;
import com.google.common.base.Optional;

public class Main {

    public static void main(String[] args) throws Exception {
        Room lobby = new Room(1, Optional.of(2), Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.<Integer>absent(), "This is the lobby.");
        Room hallway = new Room(2, Optional.of(3), Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.of(1), "This is the hallway.");
        Room bedroom = new Room(3, Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.of(2), "This is the bedroom");

        RoomManager roomManager = new RoomManager();
        roomManager.startUp();

        roomManager.addRoom(lobby);
        roomManager.addRoom(hallway);
        roomManager.addRoom(bedroom);

        TelnetServer telnetServer = new TelnetServer(8080);
        telnetServer.run(roomManager);
    }
}
