package com.comandante.creeper.cclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.google.common.eventbus.Subscribe;
import com.terminal.Questioner;
import com.terminal.TtyConnector;
import com.terminal.ui.GetDataListener;
import com.terminal.ui.ResetEvent;

import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

import static javax.swing.BorderFactory.createEmptyBorder;

public class GossipWindow extends JInternalFrame implements GetDataListener {

    private final ObjectMapper objectMapper;

    private final JTextPane textPane;
    private final JScrollPane scrollPane;
    private final Input input;

    public GossipWindow(Input input, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.input = input;
        input.setRequestFocusEnabled(true);
        input.setFocusable(true);
        textPane = new JTextPane();
        textPane.setFont(CreeperClientMainFrame.getTerminalFont());
        textPane.setBackground(Color.BLACK);
        textPane.setForeground(Color.WHITE);
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        this.scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(createEmptyBorder());
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(scrollPane);
        putClientProperty("JInternalFrame.frameType", "normal");
        setResizable(true);
        setIconifiable(true);
        setTitle("Gossip");
        input.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        DefaultCaret caret = (DefaultCaret) textPane.getCaret(); // ←
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane.setViewportView(textPane);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        getContentPane().add(input);
        addInternalFrameListener(new InternalFrameAdapter() {


            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                input.getField().requestFocus();
                super.internalFrameActivated(e);
            }
        });
        textPane.addFocusListener(new FocusAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void focusGained(FocusEvent aE) {
                input.getField().requestFocus();

            }
        });
        setClosable(true);
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

    @Override
    public void process(JsonNode jsonNode) {
        //
    }

    public static class GossipTtyConnector implements TtyConnector {

        @Override
        public boolean init(Questioner q) {
            return true;
        }

        @Override
        public void close() {

        }

        @Override
        public void resize(Dimension termSize, Dimension pixelSize) {

        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public int read(char[] buf, int offset, int length) throws IOException {
            return 0;
        }

        @Override
        public void write(byte[] bytes) throws IOException {

        }

        @Override
        public boolean isConnected() {
            return false;
        }

        @Override
        public void write(String string) throws IOException {

        }

        @Override
        public int waitFor() throws InterruptedException {
            return 0;
        }
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
