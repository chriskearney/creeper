package com.comandante.creeper;

import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.managers.SessionManager;
import com.comandante.creeper.npc.StreetHustler;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.room.Area;
import com.comandante.creeper.room.MapMatrix;
import com.comandante.creeper.room.MapsManager;
import com.comandante.creeper.room.Room;
import com.comandante.creeper.room.RoomLayoutCsvPrototype;
import com.comandante.creeper.room.RoomManager;
import com.comandante.creeper.server.CreeperCommandRegistry;
import com.comandante.creeper.server.CreeperServer;
import com.comandante.creeper.server.command.DropCommand;
import com.comandante.creeper.server.command.GossipCommand;
import com.comandante.creeper.server.command.InventoryCommand;
import com.comandante.creeper.server.command.KillCommand;
import com.comandante.creeper.server.command.LookCommand;
import com.comandante.creeper.server.command.MovementCommand;
import com.comandante.creeper.server.command.PickUpCommand;
import com.comandante.creeper.server.command.SayCommand;
import com.comandante.creeper.server.command.TellCommand;
import com.comandante.creeper.server.command.UnknownCommand;
import com.comandante.creeper.server.command.UseCommand;
import com.comandante.creeper.server.command.WhoCommand;
import com.comandante.creeper.server.command.WhoamiCommand;
import com.comandante.creeper.spawner.NpcSpawner;
import com.comandante.creeper.spawner.SpawnRule;
import com.comandante.creeper.stat.Stats;
import com.comandante.creeper.stat.StatsBuilder;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class Main {

    public static CreeperCommandRegistry creeperCommandRegistry;

    public static void main(String[] args) throws Exception {

        DB db = DBMaker.newFileDB(new File("creeperDb"))
                .closeOnJvmShutdown()
                .encryptionEnable("creepandicrawl")
                .make();

        RoomManager roomManager = new RoomManager();
        PlayerManager playerManager = new PlayerManager(db, new SessionManager());

        EntityManager entityManager = new EntityManager(roomManager, playerManager, db);

        Stats chrisBrianStats = new StatsBuilder().setStrength(7).setWillpower(8).setAim(6).setAgile(5).setArmorRating(4).setMeleSkill(10).setCurrentHealth(100).setMaxHealth(100).setWeaponRatingMin(10).setWeaponRatingMax(20).setNumberweaponOfRolls(1).createStats();
        if (playerManager.getPlayerMetadata(createPlayerId("chris")) == null) {
            System.out.println("Creating Chris User.");
            playerManager.savePlayerMetadata(new PlayerMetadata("chris", "poop", new String(Base64.encodeBase64("chris".getBytes())), chrisBrianStats));
        }

        if (playerManager.getPlayerMetadata(createPlayerId("brian")) == null) {
            System.out.println("Creating Brian User.");
            playerManager.savePlayerMetadata(new PlayerMetadata("brian", "poop", new String(Base64.encodeBase64("brian".getBytes())), chrisBrianStats));
        }

        GameManager gameManager = new GameManager(roomManager, playerManager, entityManager);
        MapMatrix floorMapMatrix = RoomLayoutCsvPrototype.buildRooms(entityManager);
        System.out.print("Building all rooms.");
        MapsManager mapsManager = new MapsManager(roomManager);
        mapsManager.addFloorMatrix(1, floorMapMatrix);
        mapsManager.generateAllMaps(9, 9);
        entityManager.addEntity(new NpcSpawner(new StreetHustler(gameManager), Area.NEWBIE_ZONE, gameManager, new SpawnRule(10, 100, 4, 100)));
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            next.getValue().setAreas(Sets.newHashSet(Area.NEWBIE_ZONE));
        }
        System.out.println("done!");
        creeperCommandRegistry = new CreeperCommandRegistry(new UnknownCommand(gameManager));
        creeperCommandRegistry.addCommand(new DropCommand(gameManager));
        creeperCommandRegistry.addCommand(new GossipCommand(gameManager));
        creeperCommandRegistry.addCommand(new InventoryCommand(gameManager));
        creeperCommandRegistry.addCommand(new KillCommand(gameManager));
        creeperCommandRegistry.addCommand(new LookCommand(gameManager));
        creeperCommandRegistry.addCommand(new MovementCommand(gameManager));
        creeperCommandRegistry.addCommand(new PickUpCommand(gameManager));
        creeperCommandRegistry.addCommand(new SayCommand(gameManager));
        creeperCommandRegistry.addCommand(new TellCommand(gameManager));
        creeperCommandRegistry.addCommand(new UseCommand(gameManager));
        creeperCommandRegistry.addCommand(new WhoamiCommand(gameManager));
        creeperCommandRegistry.addCommand(new WhoCommand(gameManager));

        CreeperServer creeperServer = new CreeperServer(8080, db);
        creeperServer.run(gameManager);

        System.out.println("Creeper started.");
    }

    public static String createPlayerId(String playerName) {
        return new String(Base64.encodeBase64(playerName.getBytes()));
    }
}
