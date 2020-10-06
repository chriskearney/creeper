package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
import com.comandante.creeper.items.EquipmentSlotType;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.player.Quest;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ColorPane;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;
import java.util.Map;

public class QuestsPanel extends JPanel {

    private final JList<QuestSlot> rolledUpInventoryItems;
    private final DefaultListModel<QuestSlot> defaultListModel;

    public QuestsPanel() {
        defaultListModel = new DefaultListModel<>();
        rolledUpInventoryItems = new JList<>(defaultListModel);
        rolledUpInventoryItems.setBackground(Color.BLACK);
        rolledUpInventoryItems.setCellRenderer(new EquipmentSlotRenderer());
        JScrollPane pane = new JScrollPane(rolledUpInventoryItems);
        pane.setBorder(BorderFactory.createEmptyBorder());
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        add(pane);
        setFocusable(false);
        setVisible(true);
    }

    public static class QuestSlot {

        private Quest quest;

        public QuestSlot(Quest quest) {
            this.quest = quest;
        }

        public Quest getQuest() {
            return quest;
        }
    }

    public class EquipmentSlotRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            QuestSlot questSlot = (QuestSlot) value;
            ColorPane colorPane = new ColorPane();
            colorPane.setOpaque(true);
            colorPane.appendANSI(questSlot.getQuest().getQuestName());
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

                Map<String, Quest> acceptedQuests = playerData.getPlayerMetadata().getAcceptedQuests();
                for (Map.Entry<String, Quest> next : acceptedQuests.entrySet()) {
                    defaultListModel.addElement(new QuestSlot(next.getValue()));
                }

                rolledUpInventoryItems.revalidate();
                rolledUpInventoryItems.repaint();
            }
        });
    }


}