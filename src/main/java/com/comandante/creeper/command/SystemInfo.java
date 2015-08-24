package com.comandante.creeper.command;

import com.comandante.creeper.Main;
import com.comandante.creeper.command.Command;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.comandante.creeper.server.Color;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SystemInfo extends Command {
    final static List<String> validTriggers = Arrays.asList("sysinfo", "systeminfo", "sys");
    final static String description = "Display System information.";
    final static String correctUsage = "systeminfo";

    public SystemInfo(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            String os_name = System.getProperty("os.name", "OS_NAME");
            String os_version = System.getProperty("os.version", "OS_VERSION");
            String java_version = System.getProperty("java.version", "JAVA_VERSION");
            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            long uptime = bean.getUptime();
            String upTime = getDurationBreakdown(uptime);
            String maxHeap = FileUtils.byteCountToDisplaySize(Runtime.getRuntime().maxMemory());
            write(new StringBuilder()
                    .append(Color.MAGENTA)
                    .append("os_name:")
                    .append(Color.RESET)
                    .append(os_name)
                    .append("\r\n")
                    .append(Color.MAGENTA)
                    .append("os_version:")
                    .append(Color.RESET)
                    .append(os_version)
                    .append("\r\n")
                    .append(Color.MAGENTA)
                    .append("java_version:")
                    .append(Color.RESET)
                    .append(java_version)
                    .append("\r\n")
                    .append(Color.MAGENTA)
                    .append("max_heap:")
                    .append(Color.RESET)
                    .append(maxHeap)
                    .append("\r\n")
                    .append(Color.MAGENTA)
                    .append("uptime:")
                    .append(Color.RESET)
                    .append(upTime)
                    .append("\r\n")
                    .append(Color.MAGENTA)
                    .append("build:")
                    .append(Color.RESET)
                    .append(Main.getCreeperVersion())
                    .append("\r\n").toString());
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return (sb.toString());
    }
}
