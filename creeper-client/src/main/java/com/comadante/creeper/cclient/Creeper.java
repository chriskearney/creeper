package com.comadante.creeper.cclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.ssh.jsch.JSchShellTtyConnector;
import com.terminal.ui.ResetEvent;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.concurrent.Executors;

public class Creeper extends CreeperClientMainFrame {

    public static String hostname = "creeper.ktwit.net";
    public static Integer httpPort = 9000;

    public Creeper(GossipWindow gossipWindow,
                   MapPanel mapPanel,
                   StatsWindow statsWindow,
                   ConsolePanel consoleWindow,
                   InventoryPanel inventoryPanel,
                   NearPanel nearMeWindow) {
        super(consoleWindow, gossipWindow, mapPanel, statsWindow, inventoryPanel, nearMeWindow);
    }

    public static void main(final String[] arg) throws Exception {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        final ObjectMapper objectMapper = registerJdkModuleAndGetMapper();
        final BasicAuthStringSupplier basicAuthStringSupplier = new BasicAuthStringSupplier();
        final EventBus eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
        final CreeperApiHttpClient creeperApiHttpClient = new CreeperApiHttpClient(hostname, httpPort, eventBus, basicAuthStringSupplier, objectMapper);
        creeperApiHttpClient.startAsync().awaitRunning();
        final GossipWindow gossipWindow = new GossipWindow(new Input(line -> creeperApiHttpClient.gossip(line), null), objectMapper);
        final ConsoleStatusBar consoleStatusBar = new ConsoleStatusBar(objectMapper);
        final StatsWindow statsWindow = new StatsWindow(objectMapper);
        final ConsolePanel consoleWindow = new ConsolePanel(consoleStatusBar, getMovementHandler(creeperApiHttpClient), Lists.newArrayList(basicAuthStringSupplier), () -> new JSchShellTtyConnector(hostname, "bridge", "b"), objectMapper);
        final MapStatusBar mapStatusBar = new MapStatusBar("", objectMapper);
        final MapPanel mapPanel = new MapPanel(mapStatusBar, getMovementHandler(creeperApiHttpClient), objectMapper);
        final InventoryPanel inventoryPanel = new InventoryPanel(objectMapper, getUseItemIdHandler(creeperApiHttpClient));
        final NearPanel nearMeWindow = new NearPanel(objectMapper, getNearMeHandler(creeperApiHttpClient));

        eventBus.register(mapStatusBar);
        eventBus.register(mapPanel);
        eventBus.register(gossipWindow);
        eventBus.register(statsWindow);
        eventBus.register(consoleStatusBar);
        eventBus.register(inventoryPanel);
        eventBus.register(basicAuthStringSupplier);
        eventBus.register(consoleWindow);
        eventBus.register(nearMeWindow);


        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                consoleWindow.getInput().getField().requestFocus();
            }
        });

        inventoryPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                consoleWindow.getInput().getField().requestFocus();
            }
        });

        nearMeWindow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                consoleWindow.getInput().getField().requestFocus();
            }
        });

        Creeper creeper = new Creeper(gossipWindow, mapPanel, statsWindow, consoleWindow, inventoryPanel, nearMeWindow);

    }

    public static ObjectMapper registerJdkModuleAndGetMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        Jdk8Module module = new Jdk8Module();
        module.configureAbsentsAsNulls(true);
        objectMapper.registerModule(module);
        return objectMapper;
    }

    private static MapPanel.MapWindowMovementHandler getMovementHandler(CreeperApiHttpClient creeperApiHttpClient) {
        return new MapPanel.MapWindowMovementHandler() {
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

    private static InventoryPanel.UseItemIdHandler getUseItemIdHandler(CreeperApiHttpClient creeperApiHttpClient) {
        return new InventoryPanel.UseItemIdHandler() {
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
