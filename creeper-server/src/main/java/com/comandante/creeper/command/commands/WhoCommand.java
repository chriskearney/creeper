package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.stats.Levels;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class WhoCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("who");
    final static String description = "Display who is currently logged into the mud.";
    final static String correctUsage = "who";

    public WhoCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            StringBuilder output = new StringBuilder();
            // output.append(Color.MAGENTA + "Who--------------------------------" + Color.RESET).append("\r\n");
            Table t = new Table(4, BorderStyle.BLANKS,
                    ShownBorders.NONE);
            t.setColumnWidth(0, 14, 24);
            t.setColumnWidth(1, 7, 7);
            t.setColumnWidth(2, 16, 16);
            t.addCell("Player");
            t.addCell("Level");
            t.addCell("XP");
            t.addCell("Location");
            Set<Player> allPlayers = gameManager.getAllPlayers();
            for (Player allPlayer : allPlayers) {
                t.addCell(allPlayer.getPlayerName());
                Optional<PlayerMetadata> playerMetadataOptional = playerManager.getPlayerMetadata(allPlayer.getPlayerId());
                if (!playerMetadataOptional.isPresent()){
                    continue;
                }
                PlayerMetadata playerMetadata = playerMetadataOptional.get();
                t.addCell(Long.toString(Levels.getLevel(playerMetadata.getStats().getExperience())));
                t.addCell(NumberFormat.getNumberInstance(Locale.US).format((playerMetadata.getStats().getExperience())));
                t.addCell(roomManager.getPlayerCurrentRoom(allPlayer).get().getRoomTitle());
            }
            output.append(t.render());
            write(output.toString());
        });
    }
}
