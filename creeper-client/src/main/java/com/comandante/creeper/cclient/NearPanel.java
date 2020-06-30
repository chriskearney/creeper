package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
import com.comandante.creeper.items.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ResetEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class NearPanel extends JPanel {

    private final ObjectMapper objectMapper;
    private final NearMeHandler nearMeHandler;
    private final JList<NearMeItem> nearMeItems;
    private final DefaultListModel<NearMeItem> defaultListModel;
    private final TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Near Me");;

    public NearPanel(ObjectMapper objectMapper, NearMeHandler nearMeHandler) {
        this.objectMapper = objectMapper;
        this.nearMeHandler = nearMeHandler;
        this.border.setTitleColor(Color.white);

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        defaultListModel = new DefaultListModel<NearMeItem>();
        nearMeItems = new JList<>(defaultListModel);
        nearMeItems.setBackground(Color.BLACK);
        nearMeItems.setCellRenderer(new NearMePaneCellRenderer());

        JScrollPane pane = new JScrollPane(nearMeItems);
        pane.setBorder(BorderFactory.createEmptyBorder());
        nearMeItems.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();
                    nearMeItems.setSelectedIndex(nearMeItems.locationToIndex(e.getPoint()));
                    NearMeItem selectedValue = nearMeItems.getSelectedValue();
                    selectedValue.npc.ifPresent(stringStringTuple -> configureNpcMenu(menu, stringStringTuple));
                    selectedValue.player.ifPresent(stringStringTuple -> configurePlayerMenu(menu, stringStringTuple));
                    selectedValue.item.ifPresent(stringStringTuple -> configureItemMenu(menu, stringStringTuple));
                    menu.show(nearMeItems, e.getPoint().x, e.getPoint().y);
                }
            }
        });
        add(pane);
        setBorder(border);
        setPreferredSize(CreeperClientMainFrame.RIGHT_SIDE_PANEL_DIMENSIONS_BIGGER);
        setFocusable(false);
        setVisible(true);
    }

    private void configureNpcMenu(JPopupMenu menu, Tuple<String, String> npc) {
        JMenuItem attackNpc = new JMenuItem("Attack");
        attackNpc.addActionListener(e1 -> {
            nearMeHandler.attack(npc.getX());
        });
        menu.add(attackNpc);

        JMenuItem lookNpc = new JMenuItem("Look");
        lookNpc.addActionListener(e1 -> {
            nearMeHandler.look(Optional.of(npc.getX()), Optional.empty());
        });
        menu.add(lookNpc);
    }

    private void configurePlayerMenu(JPopupMenu menu, Tuple<String, String> player) {

        JMenuItem lookPlayer = new JMenuItem("Look");
        lookPlayer.addActionListener(e1 -> {
            nearMeHandler.look(Optional.empty(), Optional.of(player.getX()));
        });
        menu.add(lookPlayer);

        JMenuItem comparePlayer = new JMenuItem("Compare");
        comparePlayer.addActionListener(e1 -> {
            nearMeHandler.compare(player.getX());
        });
        menu.add(comparePlayer);
    }

    private void configureItemMenu(JPopupMenu menu, Tuple<String, String> item) {

        JMenuItem pickItem = new JMenuItem("Pick");
        pickItem.addActionListener(e1 -> {
            nearMeHandler.pick(item.getX());
        });
        menu.add(pickItem);

    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        defaultListModel.removeAllElements();
        nearMeItems.revalidate();
        nearMeItems.repaint();
    }

    @Subscribe
    public void updateNearMe(PlayerData playerData) throws IOException {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {

                    Boolean isInFight = playerData.getInFight();
                    if (isInFight) {
                        border.setBorder(BorderFactory.createLineBorder(Color.red));
                        repaint();
                    } else {
                        border.setBorder(BorderFactory.createLineBorder(Color.green));
                        repaint();
                    }


                    java.util.List<NearMeItem> nearMeItemList = Lists.newArrayList();

                    for (String npcID : playerData.getPresentNpcs().keySet()) {
                        String npcName = playerData.getPresentNpcs().get(npcID);
                        NearMeItem nearMeItem = new NearMeItem(Optional.of(new Tuple<String, String>(npcID, npcName)), Optional.empty(), Optional.empty(), Optional.empty());
                        nearMeItemList.add(nearMeItem);
                    }

                    playerData.getPresentItems().forEach(new Consumer<Item>() {
                        @Override
                        public void accept(Item item) {
                            String itemId = item.getItemId();
                            String itemName = item.getItemName();
                            nearMeItemList.add(new NearMeItem(Optional.empty(), Optional.empty(), Optional.of(new Tuple<String, String>(itemId, itemName)), Optional.empty()));
                        }
                    });

                    for (String playerId : playerData.getPresentPlayers().keySet()) {
                        String playerName = playerData.getPresentPlayers().get(playerId);
                        NearMeItem nearMeItem = new NearMeItem(Optional.empty(), Optional.of(new Tuple<>(playerId, playerName)), Optional.empty(), Optional.empty());
                        nearMeItemList.add(nearMeItem);
                    }

                    for (String merchantName: playerData.getPresentMerchants().keySet()) {
                        String merchantType = playerData.getPresentMerchants().get(merchantName);
                        NearMeItem nearMeItem = new NearMeItem(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(new Tuple<>(merchantType, merchantName)));
                        nearMeItemList.add(nearMeItem);
                    }

                    if (!defaultListModel.isEmpty()) {
                        java.util.List<NearMeItem> existingNearMeItems = Lists.newArrayList();
                        Object[] objects = defaultListModel.toArray();
                        for (Object o : objects) {
                            existingNearMeItems.add((NearMeItem) o);
                        }
                        if (existingNearMeItems.equals(nearMeItemList)) {
                            nearMeItems.revalidate();
                            return;
                        }

                    }

                    defaultListModel.removeAllElements();
                    nearMeItemList.forEach(new Consumer<NearMeItem>() {
                        @Override
                        public void accept(NearMeItem nearMeItem) {
                            defaultListModel.addElement(nearMeItem);
                        }
                    });
                    nearMeItems.revalidate();
                    nearMeItems.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class NearMeItem {
        private final Optional<Tuple<String, String>> npc;
        private final Optional<Tuple<String, String>> player;
        private final Optional<Tuple<String, String>> item;
        private final Optional<Tuple<String, String>> merchant;

        public NearMeItem(Optional<Tuple<String, String>> npc, Optional<Tuple<String, String>> player, Optional<Tuple<String, String>> item, Optional<Tuple<String, String>> merchant) {
            this.npc = npc;
            this.player = player;
            this.item = item;
            this.merchant = merchant;
        }

        public Optional<Tuple<String, String>> getNpc() {
            return npc;
        }

        public Optional<Tuple<String, String>> getPlayer() {
            return player;
        }

        public Optional<Tuple<String, String>> getItem() {
            return item;
        }

        public Optional<Tuple<String, String>> getMerchant() {
            return merchant;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NearMeItem that = (NearMeItem) o;
            return Objects.equals(npc, that.npc) &&
                    Objects.equals(player, that.player) &&
                    Objects.equals(item, that.item) &&
                    Objects.equals(merchant, that.merchant);
        }

        @Override
        public int hashCode() {
            return Objects.hash(npc, player, item, merchant);
        }
    }

    public class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        public X getX() {
            return x;
        }

        public Y getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tuple<?, ?> tuple = (Tuple<?, ?>) o;
            return Objects.equals(x, tuple.x) &&
                    Objects.equals(y, tuple.y);
        }

        @Override
        public int hashCode() {

            return Objects.hash(x, y);
        }
    }
}
