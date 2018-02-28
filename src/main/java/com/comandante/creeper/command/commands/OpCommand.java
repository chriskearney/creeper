package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class OpCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("op");
    final static String description = "Give a user in the IRC channel ops.";
    final static String correctUsage = "op fibs";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.ADMIN);

    public OpCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    public OpCommand(GameManager gameManager, List<String> validTriggers, String description, String correctUsage) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            //ghetto and will only work for one channel bots.
            if (originalMessageParts.size() <= 1) {
                return;
            }
            originalMessageParts.remove(0);
            String desiredNickNameToOp = Joiner.on(" ").join(originalMessageParts);
            UserChannelDao<User, Channel> userChannelDao = gameManager.getIrcBotService().getBot().getUserChannelDao();
            User user = userChannelDao.getUser(desiredNickNameToOp);
            if (user == null) {
                write("No such nick name exists in " + gameManager.getCreeperConfiguration().getIrcChannel());
                return;
            }
            ImmutableSortedSet<Channel> channels = user.getChannels();
            for (Channel channel : channels) {
                channel.send().op(user);
                write("Ops given in channel " + channel.getName());
            }
        });
    }
}
