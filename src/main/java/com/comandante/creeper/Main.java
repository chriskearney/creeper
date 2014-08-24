package com.comandante.creeper;

import com.comandante.creeper.command.CommandService;
import com.comandante.creeper.command.DefaultCommandHandler;
import com.comandante.creeper.managers.EntityManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.managers.PlayerManager;
import com.comandante.creeper.managers.PlayerManagerMapDB;
import com.comandante.creeper.managers.RoomManager;
import com.comandante.creeper.model.BasicRoom;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import com.comandante.creeper.npc.Derper;
import com.comandante.creeper.server.CreeperServer;
import com.google.common.base.Optional;
import org.apache.commons.codec.binary.Base64;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {

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

        RoomManager roomManager = new RoomManager();
        EntityManager entityManager = new EntityManager(roomManager);
        GameManager gameManager = new GameManager(roomManager, playerManager, entityManager);
        entityManager.addEntity(new BasicRoom(
                1,
                "Lobby",
                Optional.of(2),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "This is the lobby. It's pretty empty and the paint still smells fresh.\r\n"));
        entityManager.addEntity(new Derper(gameManager, 1));
        CommandService commandService = new CommandService();
        DefaultCommandHandler defaultCommandHandler = new DefaultCommandHandler(gameManager, commandService);

        CreeperServer creeperServer = new CreeperServer(8080, db);
        creeperServer.run(gameManager, defaultCommandHandler);

        System.out.println("Creeper started.");
    }
}
