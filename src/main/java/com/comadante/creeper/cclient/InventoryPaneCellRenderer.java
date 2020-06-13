package com.comadante.creeper.cclient;

import com.terminal.ui.ColorPane;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

/*from  w  ww .j a  va  2  s  . c o  m*/

public class InventoryPaneCellRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        InventoryPanel.RolledUpInventoryItem rolledUpInventoryItem = (InventoryPanel.RolledUpInventoryItem) value;
        ColorPane colorPane = new ColorPane();
        colorPane.setOpaque(true);
        colorPane.appendANSI("(" + rolledUpInventoryItem.getInventoryItems().size() + ") " + rolledUpInventoryItem.getItemName());
        if (isSelected) {
            colorPane.setBackground(Color.darkGray);
        } else {
            colorPane.setBackground(Color.BLACK);
        }
        return colorPane;
    }
}
