package com.comandante.creeper.cclient;

import com.terminal.ui.ColorPane;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class NearMePaneCellRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        NearPanel.NearMeItem nearMeItem = (NearPanel.NearMeItem) value;
        ColorPane colorPane = new ColorPane();
        colorPane.setOpaque(true);
        String displayName = null;
        if (nearMeItem.getNpc().isPresent()) {
            displayName = nearMeItem.getNpc().get().getY();
        } else if (nearMeItem.getPlayer().isPresent()) {
            displayName = nearMeItem.getPlayer().get().getY();
        } else if (nearMeItem.getItem().isPresent()) {
            displayName = nearMeItem.getItem().get().getY();
        } else if (nearMeItem.getMerchant().isPresent()) {
            displayName = nearMeItem.getMerchant().get().getY();
        }
        if (displayName != null) {
            colorPane.appendANSI(displayName);
        }
        if (isSelected) {
            colorPane.setBackground(Color.darkGray);
        } else {
            colorPane.setBackground(Color.BLACK);
        }
        return colorPane;
    }
}