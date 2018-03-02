package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.comandante.creeper.storage.NpcMetadata;
import com.google.common.collect.Sets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LoadNpcCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("loadnpc");
    final static String description = "Load a NPC using JSON over http";
    final static String correctUsage = "loadnpc <http url with json for npc>";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);

    public LoadNpcCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {

            if (originalMessageParts.size() <= 1) {
                write("Please specify a http url." + "\r\n");
                return;
            }

            originalMessageParts.remove(0);


            String npcJsonHttpUrl = originalMessageParts.get(0);
            if (!isValidURL(npcJsonHttpUrl)) {
                write("Invalid HTTP address." + "\r\n");
                return;
            }

            HttpGet httpGet = new HttpGet(npcJsonHttpUrl);

            HttpClient httpclient = gameManager.getHttpclient();

            HttpResponse httpResponse = httpclient.execute(httpGet);

            HttpEntity entity = httpResponse.getEntity();

            if (entity == null) {
                write("Error retrieving JSON url." + "\r\n");
                return;
            }

            String npcJson = EntityUtils.toString(entity);

            NpcMetadata npcMetadata = null;
            try {
                npcMetadata = gameManager.getGson().fromJson(npcJson, NpcMetadata.class);
            } catch (Exception ex) {
                write("Retrieved JSON file is malformed. " + ex.getLocalizedMessage() + "\r\n");
                return;
            }
            httpGet.reset();

            gameManager.getNpcStorage().saveNpcMetadata(npcMetadata);
            write("NPC Saved. - " + npcMetadata.getName() + "\r\n");

        });
    }

    public boolean isValidURL(String url) {
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

}



