package com.comandante;

import com.comandante.telnetserver.TelnetServer;
import com.google.common.base.Optional;
import org.fusesource.jansi.Ansi;

public class Main {

    public static void main(String[] args) throws Exception {
        Room lobby = new Room(1, Optional.of(2), Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.<Integer>absent(),
                "This is the lobby. It's pretty empty and the paint still smells fresh.");
        Room hallway = new Room(2, Optional.of(3), Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.of(1),
                "This is the hallway. It's long and hallway-ish with exposed wires and floorboards showing.");
        Room bedroom = new Room(3, Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.<Integer>absent(), Optional.of(2),
                "This is the bedroom.  It's for sleeping.");

        GameManager gameManager = new GameManager();
        gameManager.addRoom(lobby);
        gameManager.addRoom(hallway);
        gameManager.addRoom(bedroom);

        Ansi ansi = new Ansi();
        ansi.fg(Ansi.Color.RED);
        String hello = ansi.render("hello").toString();
        System.out.println(hello);


        TelnetServer telnetServer = new TelnetServer(8080);
        telnetServer.run(gameManager);
    }
}
