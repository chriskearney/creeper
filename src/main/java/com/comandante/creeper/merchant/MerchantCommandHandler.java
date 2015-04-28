package com.comandante.creeper.merchant;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.CreeperSession;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.UUID;

public class MerchantCommandHandler extends SimpleChannelUpstreamHandler {

    private final GameManager gameManager;
    private final Merchant merchant;
    private final MerchantManager merchantManager;

    public MerchantCommandHandler(GameManager gameManager, Merchant merchant) {
        this.gameManager = gameManager;
        this.merchant = merchant;
        this.merchantManager = new MerchantManager(gameManager);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        CreeperSession creeperSession = (CreeperSession) e.getChannel().getAttachment();
        Player playerByUsername = gameManager.getPlayerManager().getPlayerByUsername(creeperSession.getUsername().get());
        try {
            String message = (String) e.getMessage();
            String cmd = message;
            if (message.contains(" ")) {
                String[] split = message.split(" ");
                cmd = split[0];
                Integer desiredItem = Integer.parseInt(split[1]);
                if (cmd.equalsIgnoreCase("buy")) {
                    merchantManager.purchaseItem(merchant, desiredItem, playerByUsername);
                }
            } else if (cmd.equalsIgnoreCase("done")) {
                gameManager.getChannelUtils().write(playerByUsername.getPlayerId(), "Thanks, COME AGAIN." + "\r\n"+ "\r\n"+ "\r\n", true);
                e.getChannel().getPipeline().addLast(UUID.randomUUID().toString(), creeperSession.getGrabMerchant().get().getValue());
                return;
            }
            gameManager.getChannelUtils().write(playerByUsername.getPlayerId(), merchant.getMenu() + "\r\n");
            gameManager.getChannelUtils().write(playerByUsername.getPlayerId(), "\r\n[" + merchant.getName() + " (done to exit, buy <itemNo>)] ");
        } finally {
            e.getChannel().getPipeline().remove(ctx.getHandler());
            super.messageReceived(ctx, e);
        }
    }
}
