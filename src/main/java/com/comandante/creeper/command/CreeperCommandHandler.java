package com.comandante.creeper.command;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.comandante.creeper.Creeper;
import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.command.commands.CommandAuditLog;
import com.comandante.creeper.command.commands.GossipCommand;
import com.comandante.creeper.command.commands.UnknownCommand;
import com.comandante.creeper.configuration.ConfigureCommands;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SentryManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.merchant.MerchantCommandHandler;
import com.comandante.creeper.merchant.bank.commands.BankCommandHandler;
import com.comandante.creeper.merchant.lockers.LockerCommandHandler;
import com.comandante.creeper.merchant.playerclass_selector.PlayerClassCommandHandler;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.model.CreeperSession;
import com.comandante.creeper.server.multiline.MultiLineInputHandler;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;

public class CreeperCommandHandler extends SimpleChannelUpstreamHandler {

    private final GameManager gameManager;
    private final Meter commandMeter = Creeper.metrics.meter(MetricRegistry.name(CreeperCommandHandler.class, "commands"));
    private static final Logger log = Logger.getLogger(CreeperCommandHandler.class);

    public CreeperCommandHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        CreeperSession session = (CreeperSession) e.getChannel().getAttachment();
        Player player = gameManager.getPlayerManager().getPlayerByUsername(session.getUsername().get());
        session.setLastActivity(System.currentTimeMillis());
        if (session.getGrabMultiLineInput().isPresent()) {
            addLastHandler(e, new MultiLineInputHandler(gameManager));
            super.messageReceived(ctx, e);
            return;
        }

        if (session.getGrabMerchant().isPresent()) {
            Merchant merchant = session.getGrabMerchant().get().getKey();
            if (merchant.getMerchantType() == Merchant.MerchantType.BANK) {
                addLastHandler(e, new BankCommandHandler(gameManager, merchant));
            } else if (merchant.getMerchantType() == Merchant.MerchantType.LOCKER) {
                addLastHandler(e, new LockerCommandHandler(gameManager, merchant));
            } else if (merchant.getMerchantType() == Merchant.MerchantType.PLAYERCLASS_SELECTOR) {
                addLastHandler(e, new PlayerClassCommandHandler(gameManager, merchant));
            } else {
                addLastHandler(e, new MerchantCommandHandler(gameManager, merchant));
            }
            super.messageReceived(ctx, e);
            return;
        }

        Command commandByTrigger = null;

        String rootCommand = getRootCommand(e);
        if (player.isChatModeOn()) {
            if (rootCommand.startsWith("/")) {
                commandByTrigger = ConfigureCommands.creeperCommandRegistry.getCommandByTrigger(rootCommand.substring(1));
            } else {
                commandByTrigger = new GossipCommand(gameManager);
            }
        } else {
            commandByTrigger = ConfigureCommands.creeperCommandRegistry.getCommandByTrigger(rootCommand);
        }

        if ((commandByTrigger.roles != null) && commandByTrigger.roles.size() > 0) {
            boolean roleMatch = gameManager.getPlayerManager().hasAnyOfRoles(player, commandByTrigger.roles);
            if (!roleMatch) {
                addLastHandler(e, new UnknownCommand(gameManager));
                super.messageReceived(ctx, e);
                return;
            }
        }

        if (commandByTrigger.getDescription() != null) {
            Creeper.metrics.counter(MetricRegistry.name(CreeperCommandHandler.class, rootCommand)).inc();
            CommandAuditLog.logCommand((String) e.getMessage(), session.getUsername().get());
        }

        commandMeter.mark();
        // Always create a copy of the command.
        addLastHandler(e, commandByTrigger.copy());
        super.messageReceived(ctx, e);
    }

    private void addLastHandler(MessageEvent e, ChannelHandler handler) {
        e.getChannel().getPipeline().addLast("executed_command", handler);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        SentryManager.logSentry(this.getClass(), e.getCause(), "Exception caught in command handler!");
        CreeperSession creeperSession = (CreeperSession) e.getChannel().getAttachment();
        log.error("Error in the Command Handler!, last message: \"" + creeperSession.getLastMessage() + "\" - from username:" + creeperSession.getUsername().get(), e.getCause());
        gameManager.getPlayerManager().removePlayer(creeperSession.getUsername().get());
        e.getChannel().close();
    }


    private String getRootCommand(MessageEvent e) {
        String origMessage = (String) e.getMessage();
        String[] split = origMessage.split(" ");
        if (split.length > 0) {
            return split[0].toLowerCase();
        } else {
            return origMessage;
        }
    }
}
