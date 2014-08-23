package com.comandante.creeper;

import com.comandante.creeper.command.DefaultCommandHandler;
import com.comandante.creeper.command.commands.CommandService;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.managers.PlayerManager;
import com.comandante.creeper.managers.PlayerManagerMapDB;
import com.comandante.creeper.managers.RoomManager;
import com.comandante.creeper.model.npc.Npc;
import com.comandante.creeper.model.npc.NpcType;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.server.CreeperServer;
import com.google.common.base.Optional;
import org.apache.commons.codec.binary.Base64;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;

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

        DB db = DBMaker.newFileDB(new File("creeperDb"))
                .closeOnJvmShutdown()
                .encryptionEnable("creepandicrawl")
                .make();

        PlayerManager playerManager = new PlayerManagerMapDB(db);
        if (playerManager.getPlayerMetadata(new Player("chris").getPlayerId()) == null) {
            System.out.println("Creating Chris User.");
            playerManager.savePlayerMetadata(new PlayerMetadata("chris", "poop", new String(Base64.encodeBase64("chris".getBytes()))));
        }

        if (playerManager.getPlayerMetadata(new Player("brian").getPlayerId()) == null) {
            System.out.println("Creating Brian User.");
            playerManager.savePlayerMetadata(new PlayerMetadata("brian", "poop", new String(Base64.encodeBase64("brian".getBytes()))));
        }

        GameManager gameManager = new GameManager(roomManager, playerManager);

        Npc derper1 = new Npc(NpcType.DERPER);
        gameManager.getNpcManager().saveNpc(derper1);
        roomManager.getRoom(lobby.getRoomId()).addPresentNpc(derper1.getNpcId());
        Npc derper2 = new Npc(NpcType.DERPER);
        gameManager.getNpcManager().saveNpc(derper2);
        roomManager.getRoom(lobby.getRoomId()).addPresentNpc(derper2.getNpcId());

        Npc derper3 = new Npc(NpcType.DERPER);
        gameManager.getNpcManager().saveNpc(derper3);
        roomManager.getRoom(janitorialCloset.getRoomId()).addPresentNpc(derper3.getNpcId());

        CommandService commandService = new CommandService();
        DefaultCommandHandler defaultCommandHandler = new DefaultCommandHandler(gameManager, commandService);

        CreeperServer creeperServer = new CreeperServer(8080, db);
        creeperServer.run(gameManager, defaultCommandHandler);

        System.out.println("Creeper started.");
    }
}
