package com.comandante.creeper.builder;

import com.comandante.creeper.managers.EntityManager;
import com.comandante.creeper.model.BasicRoom;
import com.comandante.creeper.model.Color;
import com.google.common.base.Optional;

/**
 * Created by Brian Kearney on 8/24/2014.
 */
public class RoomBuilders {

    public static void buildFedTraining(EntityManager entityManager){

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
                Optional.of(100),
                Optional.of(3),
                Optional.of(200),
                Optional.of(300),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "You are standing in the main port of the Federation Encampment.\r\n"));

    }

    public static void buildNeoPortland(EntityManager entityManager){

        entityManager.addEntity(new BasicRoom(
                200,
                "New Portland",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(7),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Start of New Portland area.\r\n"));
    }

    public static void buildOldTown(EntityManager entityManager){

        entityManager.addEntity(new BasicRoom(
                300,
                "Old Town",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(7),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Start of Old Town area.\r\n"));
    }

    public static void buildSpacePort(EntityManager entityManager) {

        entityManager.addEntity(new BasicRoom(
                100,
                "Entrance of Tacoma Space Port",
                Optional.of(101),
                Optional.of(7),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Entrance of Space Port area.\r\n"));

        entityManager.addEntity(new BasicRoom(
                101,
                "Arrival Check In",
                Optional.of(102),
                Optional.of(100),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Space Port area.\r\n"));

        entityManager.addEntity(new BasicRoom(
                102,
                "Line For Security Check",
                Optional.of(103),
                Optional.of(101),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Space Port area.\r\n"));

        entityManager.addEntity(new BasicRoom(
                103,
                "Space Station Security Check",
                Optional.<Integer>absent(),
                Optional.of(102),
                Optional.of(107),
                Optional.of(104),
                Optional.of(110),
                Optional.<Integer>absent(),
                "Security Check Intersection\r\n"));

        entityManager.addEntity(new BasicRoom(
                104,
                "Walkway of Terminal 1",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(103),
                Optional.of(105),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Terminal 1 of the Tacoma Space Port.\r\n"));

        entityManager.addEntity(new BasicRoom(
                105,
                "Busy Walkway of Terminal 1",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(104),
                Optional.of(106),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Terminal 1 of the Tacoma Space Port.\r\n"));

        entityManager.addEntity(new BasicRoom(
                106,
                "Dead End of Terminal 1",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(105),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Terminal 1 of the Tacoma Space Port END.\r\n"));

        entityManager.addEntity(new BasicRoom(
                107,
                "Walkway of Terminal 2",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(108),
                Optional.of(103),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Terminal 2 of the Tacoma Space Port.\r\n"));

        entityManager.addEntity(new BasicRoom(
                108,
                "Busy Walkway Terminal 2",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(109),
                Optional.of(107),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Terminal 2 of the Tacoma Space Port.\r\n"));

        entityManager.addEntity(new BasicRoom(
                109,
                "Dead End of Terminal 2",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(108),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Terminal 2 of the Tacoma Space Port END.\r\n"));

        entityManager.addEntity(new BasicRoom(
                110,
                "Space Port Lobby",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(111),
                Optional.of(114),
                Optional.<Integer>absent(),
                Optional.of(103),
                "Tacoma Space Port food court and shopping.\r\n"));

        entityManager.addEntity(new BasicRoom(
                111,
                "Walkway of Terminal 3",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(112),
                Optional.of(110),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Tacoma Space Port terminal 3.\r\n"));

        entityManager.addEntity(new BasicRoom(
                112,
                "Busy Walkway of Terminal 3",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(113),
                Optional.of(111),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Tacoma Space Port terminal 3.\r\n"));

        entityManager.addEntity(new BasicRoom(
                113,
                "Dead End",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(112),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Tacoma Space Port terminal 3 END.\r\n"));

        entityManager.addEntity(new BasicRoom(
                114,
                "Walkway of Terminal 4",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(110),
                Optional.of(115),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Tacoma Space Port terminal 4.\r\n"));

        entityManager.addEntity(new BasicRoom(
                115,
                "Busy Walkway of Terminal 4",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(114),
                Optional.of(116),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "Tacoma Space Port terminal 4.\r\n"));

        entityManager.addEntity(new BasicRoom(
                116,
                "Dead End of Terminal 4",
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.of(115),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                Optional.<Integer>absent(),
                "You arrive to the end of Terminal 4.\r\n"));

    }

}
