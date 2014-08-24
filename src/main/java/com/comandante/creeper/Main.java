package com.comandante.creeper;

import com.comandante.creeper.command.CommandService;
import com.comandante.creeper.command.DefaultCommandHandler;
import com.comandante.creeper.managers.EntityManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.managers.PlayerManager;
import com.comandante.creeper.managers.PlayerManagerMapDB;
import com.comandante.creeper.managers.RoomManager;
import com.comandante.creeper.model.BasicRoom;
import com.comandante.creeper.model.KeyItem;
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

        entityManager.addEntity(new BasicRoom(
                1,
                "Entrance to Federation Training Encampment",
                Optional.of(2),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "You are standing before the Federation Training Encampment. A huge wall surrounds the base. To the north you see a two doors leading inside. Above the doors you sits the symbol of the Federation, a single red star with five points and a raised fist in the center. Sentries along the encampment walls begin to eye you suspiciously. You then remember that malingering in front of a Federation base could prove to be a fatal mistake.\r\n"));

        entityManager.addEntity(new BasicRoom(
                2,
                "Quarter Deck",
                Optional.of(3),
                Optional.of(1),
                Optional.of(4),
                Optional.of(5),
                Optional.of(6),
                Optional.<Integer>absent(),
                "You are standing on the quarter deck of the Training Encampment. Federation flags line the walls of this large room. A statue of the Grand Marshal of the Federation sit in the back. A Private on watch is behind a desk in the center of the room. To the west you hear the sounds of gun fire. To the east a sentry stands by a door waiting to scan the credentials of anyone looking for access to the armory. A staircase leads up stairs. You get the feeling only high ranking officers are allowed on the second floor. To the north is a door leading to the training fields.\r\n"));

        entityManager.addEntity(new BasicRoom(
                3,
                "Training Field",
                Optional.of(7),
                Optional.of(2),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "You are standing on the center of a massive training field. You see a large field with a track surrounding it. A main pathway connects from the Port to the north to the main Federation building. Soldiers of all ranks are going about their business here.\r\n"));

        entityManager.addEntity(new BasicRoom(
                4,
                "Armory",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(2),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "You are standing in the Federation Training Encampment armory. A counter extends from wall to wall separating you from the stock. A Lieutenant is standing behind the counter filling out paper work. You can see shelves extending to the back of the room fully stocked with Federation issued weapons. The door closes and locks behind you.\r\n"));

        entityManager.addEntity(new BasicRoom(
                5,
                "Firing Range",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(2),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "You are standing in the Federation Training Encampment firing range. You see many rows each separated by a blaster proof divider. At the end of each row sits a target. The sounds of weapon fire fills the room. Straight ahead you see an empty row. Down Range a target is hovering, waiting to be shot.\r\n"));

        entityManager.addEntity(new BasicRoom(
                6,
                "Marshal's Office",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(2),
                "You are standing in the Marshal's office. A beautiful wooden desk sits in the center of the room. Shelves and bookcases line the walls showing off some of the Marshals accomplishments. A floor to ceiling window in the back of the room looks over the training fields. \r\n"));

        entityManager.addEntity(new BasicRoom(
                7,
                "Port",
                Optional.<Integer>absent(),
                Optional.of(3),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "You are standing in the main port of the Federation Encampment.\r\n"));

        entityManager.addEntity(new Derper(gameManager, 1));

        KeyItem keyItem = new KeyItem();
        entityManager.addItem(keyItem);
        gameManager.placeItemInRoom(1, keyItem.getItemId());

        CommandService commandService = new CommandService();
        DefaultCommandHandler defaultCommandHandler = new DefaultCommandHandler(gameManager, commandService);

        CreeperServer creeperServer = new CreeperServer(8080, db);
        creeperServer.run(gameManager, defaultCommandHandler);

        System.out.println("Creeper started.");
    }
}
