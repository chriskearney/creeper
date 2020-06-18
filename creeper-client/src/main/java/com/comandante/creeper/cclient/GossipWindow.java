package com.comandante.creeper.cclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ResetEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

import static javax.swing.BorderFactory.createEmptyBorder;

public class GossipWindow extends JFrame {

    private final ObjectMapper objectMapper;

    private final JTextPane textPane;
    private final JScrollPane scrollPane;

    public GossipWindow(Input input, ObjectMapper objectMapper) {
        setTitle("Gossip");
        Border lineBorder = BorderFactory.createLineBorder(Color.green);

        this.objectMapper = objectMapper;
        this.textPane = new JTextPane();
        this.textPane.setFont(CreeperClientMainFrame.getTerminalFont());
        this.textPane.setBackground(Color.BLACK);
        this.textPane.setForeground(Color.WHITE);
        this.textPane.setContentType("text/html");
        this.textPane.setEditable(false);

        this.scrollPane = new JScrollPane(textPane);
        this.scrollPane.setViewportView(textPane);
        this.scrollPane.setBorder(lineBorder);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(scrollPane);

        input.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        input.setBorder(lineBorder);
        input.setRequestFocusEnabled(true);
        input.setFocusable(true);

        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        getContentPane().add(input);

        textPane.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent aE) {
                input.getField().requestFocus();
            }
        });

        setPreferredSize(CreeperClientMainFrame.MAIN_FRAME_HALF);

        pack();
    }

    public void appendChatMessage(Long timestamp, String name, String message) {

        HTMLDocument doc = (HTMLDocument) textPane.getStyledDocument();
        try {
            doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()), buildHtmlChatMessage(name, message));
            textPane.setCaretPosition(textPane.getDocument().getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.GOSSIP)) {
            return;
        }
        JsonNode jsonNode = objectMapper.readValue(creeperEvent.getPayload(), JsonNode.class);
        String name = jsonNode.get("name").asText();
        String message = jsonNode.get("message").asText();
        Long timestamp = jsonNode.get("name").asLong();
        appendChatMessage(timestamp, name, message);
    }

    private String buildHtmlChatMessage(String name, String message) {
        return "<font style='font-family: " + getFont().getFamily() + "' color='white'>[</font><font style='font-family: " + getFont().getFamily() + "' color='#FF00FF'>" + name + "</font><font style='font-family: " + getFont().getFamily() + "' color='white'>]</font>&nbsp;<font style='font-family: " + getFont().getFamily() + "' color='#00FFFF'>" + message + "</font><br>";
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        textPane.setText("");
    }
}
