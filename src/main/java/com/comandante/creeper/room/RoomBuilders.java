package com.comandante.creeper.room;

import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.spawner.ItemSpawner;
import com.comandante.creeper.spawner.SpawnRule;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * Created by Brian Kearney on 8/24/2014.
 */
public class RoomBuilders {

    public static void buildFedTraining(GameManager gameManager) {
        EntityManager entityManager = gameManager.getEntityManager();

        BasicRoom basicRoom = new BasicRoomBuilder().setRoomId(1).setRoomTitle("Entrance to Federation Training Encampment").setNorthId(Optional.of(2)).setSouthId(Optional.<Integer>absent()).setEastId(Optional.<Integer>absent()).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("You are standing before the Federation Training Encampment. A huge wall surrounds the base. To the north you see a two doors leading inside. Above the doors you sits the symbol of the Federation, a single red star with five points and a raised fist in the center. Sentries along the encampment walls begin to eye you suspiciously. You then remember that malingering in front of a Federation base could prove to be a fatal mistake.\r\n").createBasicRoom();

        basicRoom.addItemSpawner(new ItemSpawner(ItemType.BEER, new SpawnRule(20, 3, 3, 100), gameManager));
        basicRoom.addItemSpawner(new ItemSpawner(ItemType.KEY, new SpawnRule(30, 1, 10, 100), gameManager));
        basicRoom.setAreas(Sets.newHashSet(Area.NEWBIE_ZONE));


        entityManager.addEntity(basicRoom);

        BasicRoom room1 = new BasicRoomBuilder().setRoomId(2).setRoomTitle("Quarter Deck").setNorthId(Optional.of(3)).setSouthId(Optional.of(1)).setEastId(Optional.of(4)).setWestId(Optional.of(5)).setUpId(Optional.of(6)).setDownId(Optional.<Integer>absent()).setRoomDescription("You are standing on the quarter deck of the Training Encampment. Federation flags line the walls of this large room. A statue of the Grand Marshal of the Federation sit in the back. A Private on watch is behind a desk in the center of the room. To the west you hear the sounds of gun fire. To the east a sentry stands by a door waiting to scan the credentials of anyone looking for access to the armory. A staircase leads up stairs. You get the feeling only high ranking officers are allowed on the second floor. To the north is a door leading to the training fields.\r\n").createBasicRoom();
        room1.setAreas(Sets.newHashSet(Area.NEWBIE_ZONE));
        entityManager.addEntity(room1);
        BasicRoom room2 = new BasicRoomBuilder().setRoomId(3).setRoomTitle("Training Field").setNorthId(Optional.of(8)).setSouthId(Optional.of(2)).setEastId(Optional.<Integer>absent()).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("You are standing on the center of a massive training field. You see a large field with a track" +
                " surrounding it. A main pathway connects from back gate to the north to the main Federation" +
                " building. Soldiers of all ranks are going about their business here.\r\n").createBasicRoom();
        room2.setAreas(Sets.newHashSet(Area.NEWBIE_ZONE));

        entityManager.addEntity(room2);

        BasicRoom room3 = new BasicRoomBuilder().setRoomId(4).setRoomTitle("Armory").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.<Integer>absent()).setWestId(Optional.of(2)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("You are standing in the Federation Training Encampment armory. A counter extends from wall to wall" +
                " separating you from the stock. A Lieutenant is standing behind the counter filling out paper" +
                " work. You can see shelves extending to the back of the room fully stocked with Federation" +
                " issued weapons. The door closes and locks behind you.\r\n").createBasicRoom();
        room3.setAreas(Sets.newHashSet(Area.NEWBIE_ZONE));

        entityManager.addEntity(room3);

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(5).setRoomTitle("Firing Range").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(2)).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("You are standing in the Federation Training Encampment firing range. You see many rows each separated by a blaster proof divider. At the end of each row sits a target. The sounds of weapon fire fills the room. Straight ahead you see an empty row. Down Range a target is hovering, waiting to be shot.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(6).setRoomTitle("Marshal's Office").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.<Integer>absent()).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.of(2)).setRoomDescription("You are standing in the Marshal's office. A beautiful wooden desk sits in the center of the room. Shelves and bookcases line the walls showing off some of the Marshals accomplishments. A floor to ceiling window in the back of the room looks over the training fields. \r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(7).setRoomTitle("Emerald City Bullet Station").setNorthId(Optional.of(100)).setSouthId(Optional.of(8)).setEastId(Optional.of(200)).setWestId(Optional.of(300)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("You are standing in the Emerald City bullet train station. A sign points to the north for the train to Tacoma Space Port, to the west New Portland and the east Shanty town. \r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(8).setRoomTitle("Federation Encampment Back Gate").setNorthId(Optional.of(7)).setSouthId(Optional.of(3)).setEastId(Optional.<Integer>absent()).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("You are standing outside of the Federation Training Encampment. To the North is the Seattle train station.\r\n").createBasicRoom());

    }

    public static void buildNeoPortland(EntityManager entityManager) {

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(200).setRoomTitle("New Portland").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.<Integer>absent()).setWestId(Optional.of(7)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Start of New Portland area.\r\n").createBasicRoom());
    }

    public static void buildOldTown(EntityManager entityManager) {

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(300).setRoomTitle("Old Town").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(7)).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Start of Old Town area.\r\n").createBasicRoom());
    }

    public static void buildSpacePort(EntityManager entityManager) {

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(100).setRoomTitle("Entrance of Tacoma Space Port").setNorthId(Optional.of(101)).setSouthId(Optional.of(7)).setEastId(Optional.<Integer>absent()).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Entrance of Space Port area.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(101).setRoomTitle("Arrival Check In").setNorthId(Optional.of(102)).setSouthId(Optional.of(100)).setEastId(Optional.<Integer>absent()).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Space Port area.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(102).setRoomTitle("Line For Security Check").setNorthId(Optional.of(103)).setSouthId(Optional.of(101)).setEastId(Optional.<Integer>absent()).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Space Port area.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(103).setRoomTitle("Space Station Security Check").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.of(102)).setEastId(Optional.of(107)).setWestId(Optional.of(104)).setUpId(Optional.of(110)).setDownId(Optional.<Integer>absent()).setRoomDescription("Security Check Intersection\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(104).setRoomTitle("Walkway of Terminal 1").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(103)).setWestId(Optional.of(105)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Terminal 1 of the Tacoma Space Port.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(105).setRoomTitle("Busy Walkway of Terminal 1").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(104)).setWestId(Optional.of(106)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Terminal 1 of the Tacoma Space Port.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(106).setRoomTitle("Dead End of Terminal 1").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(105)).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Terminal 1 of the Tacoma Space Port END.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(107).setRoomTitle("Walkway of Terminal 2").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(108)).setWestId(Optional.of(103)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Terminal 2 of the Tacoma Space Port.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(108).setRoomTitle("Busy Walkway Terminal 2").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(109)).setWestId(Optional.of(107)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Terminal 2 of the Tacoma Space Port.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(109).setRoomTitle("Dead End of Terminal 2").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.<Integer>absent()).setWestId(Optional.of(108)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Terminal 2 of the Tacoma Space Port END.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(110).setRoomTitle("Space Port Lobby").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(111)).setWestId(Optional.of(114)).setUpId(Optional.<Integer>absent()).setDownId(Optional.of(103)).setRoomDescription("Tacoma Space Port food court and shopping.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(111).setRoomTitle("Walkway of Terminal 3").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(112)).setWestId(Optional.of(110)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Tacoma Space Port terminal 3.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(112).setRoomTitle("Busy Walkway of Terminal 3").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(113)).setWestId(Optional.of(111)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Tacoma Space Port terminal 3.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(113).setRoomTitle("Dead End").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.<Integer>absent()).setWestId(Optional.of(112)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Tacoma Space Port terminal 3 END.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(114).setRoomTitle("Walkway of Terminal 4").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(110)).setWestId(Optional.of(115)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Tacoma Space Port terminal 4.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(115).setRoomTitle("Busy Walkway of Terminal 4").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(114)).setWestId(Optional.of(116)).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("Tacoma Space Port terminal 4.\r\n").createBasicRoom());

        entityManager.addEntity(new BasicRoomBuilder().setRoomId(116).setRoomTitle("Dead End of Terminal 4").setNorthId(Optional.<Integer>absent()).setSouthId(Optional.<Integer>absent()).setEastId(Optional.of(115)).setWestId(Optional.<Integer>absent()).setUpId(Optional.<Integer>absent()).setDownId(Optional.<Integer>absent()).setRoomDescription("You arrive to the end of Terminal 4.\r\n").createBasicRoom());

    }
}
