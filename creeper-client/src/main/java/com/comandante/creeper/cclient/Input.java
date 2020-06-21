package com.comandante.creeper.cclient;

import com.google.common.base.Strings;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.terminal.ui.UIUtil;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

public class Input extends JPanel {

    private final static int MAX_COMMANDS = 5000;
    private final JTextField field;
    private final EvictingQueue<String> evictingQueue = EvictingQueue.create(MAX_COMMANDS);
    private ListIterator<String> commandScroller;
    private KeyEvent lastUpOrDownEvent;
    private final InputTextConsumer inputTextConsumer;

    public Input(InputTextConsumer inputTextConsumer, MapPanel.MapWindowMovementHandler mapWindowMovementHandler) {
        this.inputTextConsumer = inputTextConsumer;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 26));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        field = new JTextField();

        field.setBackground(Color.black);
        field.setForeground(Color.white);
        field.setCaretColor(Color.white);
        field.setBorder(BorderFactory.createDashedBorder(Color.darkGray));
        field.setFont(getTerminalFont());

        field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                try {
                    area(evt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void area(KeyEvent evt) throws IOException {
                int keyCode = evt.getKeyCode();
                if (keyCode == 38 && !evt.isControlDown()) {
                    if (commandScroller.hasNext()) {
                        if (lastUpOrDownEvent != null && lastUpOrDownEvent.getKeyCode() == 40) {
                            commandScroller.next();
                        }
                        if (commandScroller.hasNext()) {
                            field.setText(commandScroller.next());
                        }
                        lastUpOrDownEvent = evt;
                        evt.consume();
                    }
                } else if (keyCode == 40 && !evt.isControlDown()) {
                    if (commandScroller.hasPrevious()) {
                        if (lastUpOrDownEvent != null && lastUpOrDownEvent.getKeyCode() == 38) {
                            commandScroller.previous();
                        }
                        if (commandScroller.hasPrevious()) {
                            field.setText(commandScroller.previous());
                        }
                        lastUpOrDownEvent = evt;
                        evt.consume();
                    }
                } else if (keyCode == 10) {
                    String linetext = field.getText();
                    inputTextConsumer.consumer(linetext);
                    if (!Strings.isNullOrEmpty(linetext)) {
                        addToHistory(linetext);
                        setListIterator();
                    } else {
                        setListIterator();
                    }
                    field.setText("");
                    field.setCaretPosition(0);
                } else if (evt.isAltDown() && evt.isControlDown() && keyCode == 37) {
                    mapWindowMovementHandler.west();

                } else if (evt.isAltDown() && evt.isControlDown() && keyCode == 38) {
                    mapWindowMovementHandler.north();

                } else if (evt.isAltDown() && evt.isAltDown() && evt.isControlDown() && keyCode == 39) {
                    mapWindowMovementHandler.east();

                } else if (evt.isAltDown() && evt.isControlDown() && keyCode == 40) {
                    mapWindowMovementHandler.south();
                }
            }
        });
        this.add(field);
        setVisible(true);
        setFocusable(true);
    }

    public JTextField getField() {
        return field;
    }

   // public void setTerminalSession(TerminalSession terminalSession) {
   //     this.terminalSession = terminalSession;
   // }

    private void addToHistory(String s) {
        this.evictingQueue.add(s);
    }

    private void setListIterator() {
        List<String> commands = Lists.newArrayList(evictingQueue.toArray(new String[evictingQueue.size()]));
        this.commandScroller = ImmutableList.copyOf(commands).reverse().listIterator();
    }

    private Font getTerminalFont() {
        String fontName;
        if (UIUtil.isWindows) {
            fontName = "Consolas";
        } else if (UIUtil.isMac) {
            fontName = "Menlo";
        } else {
            fontName = "Monospaced";
        }
        return new Font(fontName, Font.PLAIN, 14);
    }

    public interface InputTextConsumer {
        void consumer(String line);
    }
}