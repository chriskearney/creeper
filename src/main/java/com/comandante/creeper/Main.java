package com.comandante.creeper;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.builder.RoomBuilders;
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

        RoomManager roomManager = new RoomManager();
        EntityManager entityManager =  new EntityManager(roomManager, db);
        PlayerManager playerManager = new PlayerManagerMapDB(db);
        if (playerManager.getPlayerMetadata(new Player("chris").getPlayerId()) == null) {
            System.out.println("Creating Chris User.");
            playerManager.savePlayerMetadata(new PlayerMetadata("chris", "poop", new String(Base64.encodeBase64("chris".getBytes()))));
        }

        if (playerManager.getPlayerMetadata(new Player("brian").getPlayerId()) == null) {
            System.out.println("Creating Brian User.");
            playerManager.savePlayerMetadata(new PlayerMetadata("brian", "poop", new String(Base64.encodeBase64("brian".getBytes()))));
        }

        GameManager gameManager = new GameManager(roomManager, playerManager, entityManager);

        // build zones

        RoomBuilders.buildFedTraining(entityManager);

        RoomBuilders.buildSpacePort(entityManager);

        RoomBuilders.buildNeoPortland(entityManager);

        RoomBuilders.buildOldtown(entityManager);

        // zones end

        entityManager.addEntity(new Derper(gameManager, 1));

        Item key = ItemType.KEY.create();
        entityManager.addItem(key);
        gameManager.placeItemInRoom(1, key.getItemId());

        Item book = ItemType.BOOK.create();
        entityManager.addItem(book);
        gameManager.placeItemInRoom(1, book.getItemId());

        Item beer = ItemType.BEER.create();
        entityManager.addItem(beer);
        gameManager.placeItemInRoom(1, beer.getItemId());

        CommandService commandService = new CommandService();
        DefaultCommandHandler defaultCommandHandler = new DefaultCommandHandler(gameManager, commandService);

        CreeperServer creeperServer = new CreeperServer(8080, db);
        creeperServer.run(gameManager, defaultCommandHandler);

        System.out.println("Creeper started.");
    }
}
