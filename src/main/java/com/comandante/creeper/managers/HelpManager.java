package com.comandante.creeper.managers;


import com.comandante.creeper.command.DefaultCommandType;
import com.comandante.creeper.model.Player;
import org.fusesource.jansi.Ansi;

public class HelpManager {

    public void printHelp(Player player, String originalMessage) {
        StringBuilder sb = new StringBuilder();
        DefaultCommandType[] values = DefaultCommandType.values();
        sb.append(new Ansi().fg(Ansi.Color.RED).toString());
        for (DefaultCommandType defaultCommandType : values) {
            if (defaultCommandType.equals(DefaultCommandType.UNKNOWN)) {
                continue;
            }
            sb.append(defaultCommandType.getValidCommandTriggers()).append(" ").append(defaultCommandType.getDescription()).append("\r\n");
        }
        sb.append(new Ansi().reset().toString()).append("\r\n");
        player.getChannel().write(sb.toString());
    }
}
