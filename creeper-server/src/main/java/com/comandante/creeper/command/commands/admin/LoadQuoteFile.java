package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.bot.command.QuoteManager;
import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.testng.collections.Lists;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LoadQuoteFile extends Command {

    final static List<String> validTriggers = Arrays.asList("loadquotes");
    final static String description = "Load a quote file over http";
    final static String correctUsage = "loadquotes quotefile";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);

    public LoadQuoteFile(GameManager gameManager) {
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

            String quoteFileUrl = originalMessageParts.get(0);
            if (!isValidURL(quoteFileUrl)) {
                write("Invalid HTTP address." + "\r\n");
                return;
            }

            HttpGet httpGet = new HttpGet(quoteFileUrl);

            HttpClient httpclient = gameManager.getHttpclient();

            HttpResponse httpResponse = httpclient.execute(httpGet);

            HttpEntity entity = httpResponse.getEntity();

            if (entity == null) {
                write("Error retrieving JSON url." + "\r\n");
                return;
            }

            gameManager.getMapDBCreeperStorage().getIrcQuotes().clear();
            String quoteFileContents = EntityUtils.toString(entity);
            BufferedReader bufReader = new BufferedReader(new StringReader(quoteFileContents));
            String line = null;
            int saveCount = 0;
            while ((line = bufReader.readLine()) != null) {
                List<String> lineParts = Lists.newArrayList(Arrays.asList(line.split(" ")));
                String nickName = lineParts.remove(0);
                String keyword = lineParts.remove(0);
                String quote = Joiner.on(" ").join(lineParts);
                gameManager.getQuoteManager().save(new QuoteManager.IrcQuote(nickName, keyword, quote));
                saveCount++;
            }
            write("Saved " + saveCount + " quotes.");
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
