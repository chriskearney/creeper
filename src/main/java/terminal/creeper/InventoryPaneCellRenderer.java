package terminal.creeper;

import terminal.ui.ColorPane;

import java.awt.Color;
import java.awt.Component;
/*from  w  ww .j a  va  2  s  . c o  m*/
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class InventoryPaneCellRenderer implements ListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        InventoryWindow.RolledUpInventoryItem rolledUpInventoryItem = (InventoryWindow.RolledUpInventoryItem) value;
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
