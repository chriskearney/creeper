package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
import com.comandante.creeper.items.EquipmentSlotType;
import com.comandante.creeper.items.Item;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ColorPane;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EquipmentPanel extends JPanel {

    private final JList<EquipmentSlot> rolledUpInventoryItems;
    private final DefaultListModel<EquipmentSlot> defaultListModel;

    public EquipmentPanel() {
        defaultListModel = new DefaultListModel<>();
        rolledUpInventoryItems = new JList<>(defaultListModel);
        rolledUpInventoryItems.setBackground(Color.BLACK);
        rolledUpInventoryItems.setCellRenderer(new EquipmentSlotRenderer());
        JScrollPane pane = new JScrollPane(rolledUpInventoryItems);
        pane.setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setPreferredSize(CreeperClientMainFrame.RIGHT_SIDE_PANEL_DIMENSIONS_BIGGER);
        add(pane);
        setFocusable(false);
        setVisible(true);
    }

    public static class EquipmentSlot {

        private Item item;

        public EquipmentSlot(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return item;
        }
    }

    public class EquipmentSlotRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            EquipmentSlot equipmentSlot = (EquipmentSlot) value;
            ColorPane colorPane = new ColorPane();
            colorPane.setOpaque(true);
            colorPane.appendANSI(equipmentSlot.getItem().getEquipment().getEquipmentSlotType().getName() + "\t| " + equipmentSlot.getItem().getItemName());
            if (isSelected) {
                colorPane.setBackground(Color.darkGray);
            } else {
                colorPane.setBackground(Color.BLACK);
            }
            return colorPane;
        }
    }

    @Subscribe
    public void creeperEvent(PlayerData playerData) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                defaultListModel.removeAllElements();

                Map<EquipmentSlotType, Item> slotToItemMap = Maps.newHashMap();
                for (Item equipment: playerData.getEquipmentMap().values()) {
                    slotToItemMap.put(equipment.getEquipment().getEquipmentSlotType(), equipment);
                }
                defaultListModel.addElement(new EquipmentSlot(slotToItemMap.get(EquipmentSlotType.HEAD)));
                defaultListModel.addElement(new EquipmentSlot(slotToItemMap.get(EquipmentSlotType.HAND)));
                defaultListModel.addElement(new EquipmentSlot(slotToItemMap.get(EquipmentSlotType.FEET)));
                defaultListModel.addElement(new EquipmentSlot(slotToItemMap.get(EquipmentSlotType.LEGS)));
                defaultListModel.addElement(new EquipmentSlot(slotToItemMap.get(EquipmentSlotType.WRISTS)));
                defaultListModel.addElement(new EquipmentSlot(slotToItemMap.get(EquipmentSlotType.CHEST)));
                defaultListModel.addElement(new EquipmentSlot(slotToItemMap.get(EquipmentSlotType.BAG)));

                rolledUpInventoryItems.revalidate();
                rolledUpInventoryItems.repaint();
            }
        });
    }


}
