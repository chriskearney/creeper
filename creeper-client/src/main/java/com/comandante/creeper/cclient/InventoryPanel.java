package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
import com.comandante.creeper.items.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class InventoryPanel extends JPanel {

    private final JList<RolledUpInventoryItem> rolledUpInventoryItems;
    private final DefaultListModel<RolledUpInventoryItem> defaultListModel;
    private final ObjectMapper objectMapper;
    private final UseItemIdHandler useItemIdHandler;
    private final TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Inventory");

    public InventoryPanel(ObjectMapper objectMapper, UseItemIdHandler useItemIdHandler) {
        this.useItemIdHandler = useItemIdHandler;
        this.objectMapper = objectMapper;

        defaultListModel = new DefaultListModel<>();
        rolledUpInventoryItems = new JList<>(defaultListModel);
        rolledUpInventoryItems.setBackground(Color.BLACK);
        rolledUpInventoryItems.setCellRenderer(new InventoryPaneCellRenderer());

        JScrollPane pane = new JScrollPane(rolledUpInventoryItems);
        pane.setBorder(BorderFactory.createEmptyBorder());

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(border);
        setPreferredSize(CreeperClientMainFrame.RIGHT_SIDE_PANEL_DIMENSIONS_BIGGER);
        add(pane);
        setFocusable(false);
        setVisible(true);


        rolledUpInventoryItems.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    rolledUpInventoryItems.setSelectedIndex(rolledUpInventoryItems.locationToIndex(e.getPoint()));
                    RolledUpInventoryItem selectedValue = rolledUpInventoryItems.getSelectedValue();


                    JPopupMenu menu = new JPopupMenu();
                    if (selectedValue.getInventoryItems().iterator().next().isEquipable) {
                        JMenuItem itemEquip = new JMenuItem("Equip");
                        itemEquip.addActionListener(e12 -> {
                            useItemIdHandler.equip(selectedValue.getInventoryItems().iterator().next().getItemId());
                        });
                        menu.add(itemEquip);
                    } else {
                        JMenuItem itemUse = new JMenuItem("Use");
                        itemUse.addActionListener(e12 -> {
                            useItemIdHandler.use(selectedValue.getInventoryItems().iterator().next().getItemId());
                        });
                        menu.add(itemUse);
                    }

                    JMenuItem itemDrop = new JMenuItem("Drop");
                    itemDrop.addActionListener(e1 -> {
                        useItemIdHandler.drop(selectedValue.getInventoryItems().iterator().next().getItemId());
                    });
                    menu.add(itemDrop);

                    JMenuItem itemUse = new JMenuItem("Show");
                    itemUse.addActionListener(e12 -> {
                        useItemIdHandler.show(selectedValue.getInventoryItems().iterator().next().getItemId());
                    });
                    menu.add(itemUse);

                    menu.show(rolledUpInventoryItems, e.getPoint().x, e.getPoint().y);
                }
            }
        });
    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        defaultListModel.removeAllElements();
        rolledUpInventoryItems.revalidate();
        rolledUpInventoryItems.repaint();
    }

    public static class RolledUpInventoryItem {

        private String itemName;
        private Set<InventoryItem> inventoryItems;

        public RolledUpInventoryItem(String itemName, Set<InventoryItem> inventoryItems) {
            this.itemName = itemName;
            this.inventoryItems = inventoryItems;
        }

        public String getItemName() {
            return itemName;
        }

        public Set<InventoryItem> getInventoryItems() {
            return inventoryItems;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RolledUpInventoryItem that = (RolledUpInventoryItem) o;
            return Objects.equals(itemName, that.itemName) &&
                    Objects.equals(inventoryItems, that.inventoryItems);
        }

        @Override
        public int hashCode() {

            return Objects.hash(itemName, inventoryItems);
        }
    }

    public static class InventoryItem {

        private final String itemName;
        private final String itemId;
        private final Boolean isEquipable;

        public InventoryItem(String itemName, String itemId, Boolean isEquipable) {
            this.itemName = itemName;
            this.itemId = itemId;
            this.isEquipable = isEquipable;
        }

        public String getItemName() {
            return itemName;
        }

        public String getItemId() {
            return itemId;
        }

        public Boolean getIsEquipable() {
            return isEquipable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InventoryItem that = (InventoryItem) o;
            return Objects.equals(itemName, that.itemName) &&
                    Objects.equals(itemId, that.itemId) &&
                    Objects.equals(isEquipable, that.isEquipable);
        }

        @Override
        public int hashCode() {

            return Objects.hash(itemName, itemId, isEquipable);
        }
    }

    @Subscribe
    public void updateInventory(PlayerData playerData) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //itemName -> itemIds
                    Map<String, Set<InventoryItem>> rolledUpInventory = Maps.newHashMap();

                    playerData.getItemMap().keySet().forEach(new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            Item item = playerData.getItemMap().get(s);
                            String itemName = item.getItemName();
                            String itemInternalName = item.getInternalItemName();
                            String itemId = item.getItemId();
                            Integer numberOfUses = item.getNumberOfUses();
                            Boolean isEquipable = true;
                            if (item.getEquipment() == null) {
                                isEquipable = false;
                            }
                            rolledUpInventory.putIfAbsent(itemName, Sets.newHashSet());
                            rolledUpInventory.get(itemName).add(new InventoryItem(itemName, itemId, isEquipable));
                        }
                    });

                    List<RolledUpInventoryItem> rolledUpInventoryItemsList = Lists.newArrayList();

                    for (Map.Entry<String, Set<InventoryItem>> next : rolledUpInventory.entrySet()) {
                        rolledUpInventoryItemsList.add(new RolledUpInventoryItem(next.getKey(), next.getValue()));
                    }

                    if (!defaultListModel.isEmpty()) {
                        java.util.List<RolledUpInventoryItem> currentItems = Lists.newArrayList();
                        Object[] objects = defaultListModel.toArray();
                        for (Object o : objects) {
                            currentItems.add((RolledUpInventoryItem) o);
                        }
                        if (currentItems.equals(rolledUpInventoryItemsList)) {
                            rolledUpInventoryItems.revalidate();
                            return;
                        }

                    }

                    defaultListModel.removeAllElements();
                    rolledUpInventoryItemsList.forEach(defaultListModel::addElement);
                    rolledUpInventoryItems.revalidate();
                    rolledUpInventoryItems.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface UseItemIdHandler {
        void use(String itemId);

        void drop(String itemId);

        void equip(String itemId);

        void show(String itemId);

    }
}
