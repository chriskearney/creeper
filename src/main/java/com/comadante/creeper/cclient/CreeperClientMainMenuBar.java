package com.comadante.creeper.cclient;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class CreeperClientMainMenuBar extends JMenuBar {

    public CreeperClientMainMenuBar() {

        // FILE MENU
        JMenu file = new JMenu("File");
        JMenuItem connect = new JMenuItem("Connect");
        JMenuItem disconnect = new JMenuItem("Disconnect");
        file.add(connect);
        file.add(disconnect);
        file.addSeparator();
        JMenuItem exit = new JMenuItem("Exit");
        file.add(exit);

        // EDIT MENU
        JMenu edit = new JMenu("Edit");
        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);

        // WINDOW MENU
        JMenu window = new JMenu("Window");
        JMenuItem gossip = new JMenuItem("Gossip");
        JMenuItem stats = new JMenuItem("Stats");
        JMenuItem map = new JMenuItem("Map");
        JMenuItem near = new JMenuItem("Near");
        JMenuItem inventory = new JMenuItem("Inventory");
        window.add(gossip);
        window.add(stats);
        window.add(map);
        window.add(near);
        window.add(inventory);

        // HELP MENU
        JMenu help = new JMenu("Help");
        JMenuItem documentation = new JMenuItem("Documentation");
        JMenuItem about = new JMenuItem("About");
        help.add(documentation);
        help.add(about);

        add(file);
        add(edit);
        add(window);
        add(help);
    }
}
