package terminal.creeper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import terminal.ui.ResetEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class InventoryWindow extends JInternalFrame {

    private final JList<RolledUpInventoryItem> rolledUpInventoryItems;
    private final DefaultListModel<RolledUpInventoryItem> defaultListModel;
    private final ObjectMapper objectMapper;
    private final UseItemIdHandler useItemIdHandler;

    public InventoryWindow(ObjectMapper objectMapper, UseItemIdHandler useItemIdHandler) {
        this.useItemIdHandler = useItemIdHandler;
        setBackground(Color.BLACK);
        setTitle("Inventory");
        this.objectMapper = objectMapper;
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        defaultListModel = new DefaultListModel<>();
        rolledUpInventoryItems = new JList<>(defaultListModel);
        rolledUpInventoryItems.setBackground(Color.BLACK);
        rolledUpInventoryItems.setCellRenderer(new InventoryPaneCellRenderer());
        rolledUpInventoryItems.setVisibleRowCount(Integer.MAX_VALUE);
        JScrollPane pane = new JScrollPane(rolledUpInventoryItems);
        pane.setBorder(BorderFactory.createEmptyBorder());

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


        add(pane);
        setResizable(true);
        setClosable(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setPreferredSize(new Dimension(300, 400));
        putClientProperty("JInternalFrame.frameType", "normal");
        pack();
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
    public void updateInventory(CreeperEvent creeperEvent) throws IOException {
        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.PLAYERDATA)) {
            return;
        }



        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    JsonNode jsonNode = objectMapper.readValue(creeperEvent.getPayload(), JsonNode.class);

                    //itemName -> itemIds
                    Map<String, Set<InventoryItem>> rolledUpInventory = Maps.newHashMap();

                    jsonNode.get("itemMap").forEach(new Consumer<JsonNode>() {
                        @Override
                        public void accept(JsonNode jsonNode) {
                            String itemName = jsonNode.get("itemName").asText();
                            String itemInternalName = jsonNode.get("internalItemName").asText();
                            String itemId = jsonNode.get("itemId").asText();
                            Integer numberOfUses = jsonNode.get("numberOfUses").asInt();
                            Boolean isEquipable = !jsonNode.get("equipment").asText().equals("null");
                            rolledUpInventory.putIfAbsent(itemName, Sets.newHashSet());
                            rolledUpInventory.get(itemName).add(new InventoryItem(itemName, itemId, isEquipable));
                           // inventoryItemList.add(new InventoryItem(itemName, itemId, isEquipable));
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
