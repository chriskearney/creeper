package com.ssh;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.ssh.jsch.JSchShellTtyConnector;
import terminal.creeper.ConsoleStatusBar;
import terminal.creeper.ConsoleWindow;
import terminal.creeper.CreeperClientMainFrame;
import terminal.creeper.GossipWindow;
import terminal.creeper.Input;
import terminal.creeper.InventoryWindow;
import terminal.creeper.MainToolbar;
import terminal.creeper.MapStatusBar;
import terminal.creeper.MapWindow;
import terminal.creeper.NearMeHandler;
import terminal.creeper.NearWindow;
import terminal.creeper.StatsWindow;
import terminal.ui.ResetEvent;

import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.Executors;

public class Creeper extends CreeperClientMainFrame {

    public static String hostname = "creeper.ktwit.net";
    public static Integer httpPort = 9000;

    public Creeper(GossipWindow gossipWindow,
                   MapWindow mapWindow,
                   StatsWindow statsWindow,
                   ConsoleWindow consoleWindow,
                   MainToolbar mainToolbar,
                   InventoryWindow inventoryWindow,
                   NearWindow nearMeWindow) {
        super(consoleWindow, gossipWindow, mapWindow, statsWindow, mainToolbar, inventoryWindow, nearMeWindow);
    }

    public static void main(final String[] arg) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        final ObjectMapper objectMapper = registerJdkModuleAndGetMapper();
        final BasicAuthStringSupplier basicAuthStringSupplier = new BasicAuthStringSupplier();
        final EventBus eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
        final CreeperApiHttpClient creeperApiHttpClient = new CreeperApiHttpClient(hostname, httpPort, eventBus, basicAuthStringSupplier, objectMapper);
        creeperApiHttpClient.startAsync().awaitRunning();
        final GossipWindow gossipWindow = new GossipWindow(new Input(line -> creeperApiHttpClient.gossip(line)), objectMapper);
        final ConsoleStatusBar consoleStatusBar = new ConsoleStatusBar(objectMapper);
        final StatsWindow statsWindow = new StatsWindow(objectMapper);
        final ConsoleWindow consoleWindow = new ConsoleWindow(consoleStatusBar, Lists.newArrayList(basicAuthStringSupplier), () -> new JSchShellTtyConnector(hostname, "bridge", "b"));
        final MapStatusBar mapStatusBar = new MapStatusBar("Awaiting connection to creeper.", objectMapper);
        final MapWindow mapWindow = new MapWindow(mapStatusBar, getMovementHandler(creeperApiHttpClient));
        final InventoryWindow inventoryWindow = new InventoryWindow(objectMapper, getUseItemIdHandler(creeperApiHttpClient));
        final NearWindow nearMeWindow = new NearWindow(objectMapper, getNearMeHandler(creeperApiHttpClient));

        eventBus.register(mapStatusBar);
        eventBus.register(mapWindow);
        eventBus.register(gossipWindow);
        eventBus.register(statsWindow);
        eventBus.register(consoleStatusBar);
        eventBus.register(inventoryWindow);
        eventBus.register(basicAuthStringSupplier);
        eventBus.register(consoleWindow);
        eventBus.register(nearMeWindow);

        MainToolbar mainToolbar = new MainToolbar(() -> {
            consoleWindow.connect();
            eventBus.register(consoleStatusBar);
        }, () -> {
            eventBus.post(new ResetEvent());
            eventBus.unregister(consoleStatusBar);
        });

        mainToolbar.addManagedComponent("MAP", mapWindow);
        mainToolbar.addManagedComponent("GOSSIP", gossipWindow);
        mainToolbar.addManagedComponent("INVENTORY", inventoryWindow);
        mainToolbar.addManagedComponent("STATS", statsWindow);
        mainToolbar.addManagedComponent("NEAR", nearMeWindow);
        Creeper creeper = new Creeper(gossipWindow, mapWindow, statsWindow, consoleWindow, mainToolbar, inventoryWindow, nearMeWindow);
    }

    public static ObjectMapper registerJdkModuleAndGetMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        Jdk8Module module = new Jdk8Module();
        module.configureAbsentsAsNulls(true);
        objectMapper.registerModule(module);
        return objectMapper;
    }

    private static MapWindow.MapWindowMovementHandler getMovementHandler(CreeperApiHttpClient creeperApiHttpClient) {
        return new MapWindow.MapWindowMovementHandler() {
            @Override
            public void north() {
                creeperApiHttpClient.move("north");
            }

            @Override
            public void south() {
                creeperApiHttpClient.move("south");
            }

            @Override
            public void east() {
                creeperApiHttpClient.move("east");
            }

            @Override
            public void west() {
                creeperApiHttpClient.move("west");
            }
        };
    }

    private static InventoryWindow.UseItemIdHandler getUseItemIdHandler(CreeperApiHttpClient creeperApiHttpClient) {
        return new InventoryWindow.UseItemIdHandler() {
            @Override
            public void use(String itemId) {
                creeperApiHttpClient.useItem(itemId);
            }

            @Override
            public void drop(String itemId) {
                creeperApiHttpClient.dropItem(itemId);
            }

            @Override
            public void equip(String itemId) {
                creeperApiHttpClient.equip(itemId);
            }

            @Override
            public void show(String itemId) {
                creeperApiHttpClient.show(itemId);
            }
        };
    }

    private static NearMeHandler getNearMeHandler(CreeperApiHttpClient creeperApiHttpClient) {
        return new NearMeHandler() {
            @Override
            public void attack(String entityId) {
                creeperApiHttpClient.attackNpc(entityId);
            }

            @Override
            public void look(Optional<String> npcId, Optional<String> playerId) {
                creeperApiHttpClient.look(npcId, playerId);
            }

            @Override
            public void pick(String itemId) {
                creeperApiHttpClient.pick(itemId);
            }

            @Override
            public void compare(String playerId) {
                creeperApiHttpClient.compare(playerId);
            }

        };
    }
}
