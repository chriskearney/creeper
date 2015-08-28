package com.comandante.creeper.command;

import com.codahale.metrics.Meter;
import com.comandante.creeper.Main;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.ExperienceManager;
import com.comandante.creeper.player.Levels;
import com.comandante.creeper.player.PlayerMetadata;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class XpCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("xp");
    final static String description = "Display experience to next level.";
    final static String correctUsage = "xp";

    public XpCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            PlayerMetadata playerMetadata = playerManager.getPlayerMetadata(player.getPlayerId());
            long nextLevel = Levels.getLevel(playerMetadata.getStats().getExperience()) + 1;
            long expToNextLevel = ExperienceManager.getXpToLevel(playerMetadata.getStats().getExperience());
            Meter meter = Main.metrics.meter("experience-" + player.getPlayerName());
            StringBuilder sb = new StringBuilder();
            sb.append(NumberFormat.getNumberInstance(Locale.US).format(expToNextLevel)).append(" experience to level ").append(nextLevel).append(".\r\n");

            Table t = new Table(2, BorderStyle.CLASSIC_COMPATIBLE,
                    ShownBorders.NONE);

            t.setColumnWidth(0, 8, 20);
            t.setColumnWidth(1, 10, 20);


            t.addCell("Window");
            t.addCell("XP/sec");
            t.addCell(" 1 min");
            t.addCell(String.valueOf(round(meter.getOneMinuteRate())));
            t.addCell(" 5 min");
            t.addCell(String.valueOf(round(meter.getFiveMinuteRate())));
            t.addCell("15 min");
            t.addCell(String.valueOf(round(meter.getFifteenMinuteRate())));
            write(sb.toString() + t.render());
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    public static double round(double value) {
        int places = 2;
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}