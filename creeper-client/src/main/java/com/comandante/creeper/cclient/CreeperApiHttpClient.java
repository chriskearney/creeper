package com.comandante.creeper.cclient;

import com.comandante.creeper.api.ClientConnectionInfo;
import com.comandante.creeper.chat.Gossip;
import com.comandante.creeper.chat.Users;
import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.comandante.creeper.events.DrawMapEvent;
import com.comandante.creeper.events.KillNpcEvent;
import com.comandante.creeper.events.PlayerData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.imageio.ImageIO;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CreeperApiHttpClient extends AbstractScheduledService {

    private final Client client;
    private final EventBus eventBus;
    private final static String url = "/api/events";
    private final String hostname;
    private final Integer port;
    private final BasicAuthStringSupplier basicAuthSupplier;
    private final ObjectMapper objectMapper;

    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreeperApiHttpClient.class);


    private WebTarget target;
    private EventSource eventSource;

    private final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

    public CreeperApiHttpClient(String creeperHostName,
                                Integer port,
                                EventBus eventBus,
                                BasicAuthStringSupplier basicAuthSupplier,
                                ObjectMapper objectMapper) {
        this.eventBus = eventBus;
        this.hostname = creeperHostName;
        this.port = port;
        this.basicAuthSupplier = basicAuthSupplier;
        this.objectMapper = objectMapper;
        this.client = ClientBuilder.newBuilder()
                .register(new SseFeature())
                .build();
    }

    @Override
    protected void startUp() throws Exception {

    }

    @Override
    protected void runOneIteration() throws Exception {
        try {
            if (!basicAuthSupplier.get().isPresent()) {
                if (eventSource != null && eventSource.isOpen()) {
                    eventSource.close();
                    eventSource = null;
                }
                return;
            }

            if (eventSource != null && eventSource.isOpen()) {
                return;
            }

            String credentials = new String(Base64.getDecoder().decode(basicAuthSupplier.get().get()), Charset.forName("UTF-8"));
            final String[] values = credentials.split(":", 2);
            Feature feature = HttpAuthenticationFeature.basic(values[0], values[1]);
            client.register(feature);

            target = client.target("http://" + hostname + ":" + port + url);
            eventSource = EventSource.target(target)
                    .reconnectingEvery(500, TimeUnit.MILLISECONDS)
                    .usePersistentConnections()
                    .build();
            eventSource.register(inboundSseEvent -> {
                try {
                    if (inboundSseEvent != null) {
                        if (inboundSseEvent.getName().equals("ping")) {
                            LOG.debug("Ping received from server.");
                            return;
                        }
                        CreeperEvent creeperEvent = objectMapper.readValue(inboundSseEvent.readData(), CreeperEvent.class);
                        if (creeperEvent.getCreeperEventType().equals(CreeperEventType.PLAYERDATA)) {
                            PlayerData playerData = objectMapper.readValue(creeperEvent.getPayload(), PlayerData.class);
                            eventBus.post(playerData);
                        } else if (creeperEvent.getCreeperEventType().equals(CreeperEventType.GOSSIP)) {
                            Gossip gossip = objectMapper.readValue(creeperEvent.getPayload(), Gossip.class);
                            eventBus.post(gossip);
                        } else if (creeperEvent.getCreeperEventType().equals(CreeperEventType.USERS)) {
                            Users users = objectMapper.readValue(creeperEvent.getPayload(), Users.class);
                            eventBus.post(users);
                        }
                        else if (creeperEvent.getCreeperEventType().equals(CreeperEventType.KILL_NPC)) {
                            KillNpcEvent killNpcEvent = objectMapper.readValue(creeperEvent.getPayload(), KillNpcEvent.class);
                            eventBus.post(killNpcEvent);
                        }
                        else if (creeperEvent.getCreeperEventType().equals(CreeperEventType.DRAW_MAP)) {
                            DrawMapEvent drawMapEvent = objectMapper.readValue(creeperEvent.getPayload(), DrawMapEvent.class);
                            eventBus.post(drawMapEvent);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Unable to run event loop.", e);
                }
            });
            eventSource.open();
            seedEvents();
        } catch (Throwable e) {
            LOG.error("Unable to run event loop.", e);
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 50, TimeUnit.MILLISECONDS);
    }

    public void gossip(String msg) {
        callApi(new BasicNameValuePair("message", msg), "gossip");
    }

    public void useItem(String itemId) {
        callApi(new BasicNameValuePair("itemId", itemId), "use");
    }

    public void dropItem(String itemId) {
        callApi(new BasicNameValuePair("itemId", itemId), "drop");
    }

    public void equip(String itemId) {
        callApi(new BasicNameValuePair("itemId", itemId), "equip");
    }

    public void show(String itemId) {
        callApi(new BasicNameValuePair("itemId", itemId), "show");
    }

    public void seedEvents() {
        callApi("seed");
    }

    public void look(Optional<String> npcId, Optional<String> playerId) {
        List<NameValuePair> form = new ArrayList<>();
        npcId.ifPresent(s -> form.add(new BasicNameValuePair("npcId", npcId.get())));
        playerId.ifPresent(s -> form.add(new BasicNameValuePair("playerId", playerId.get())));
        callApi(form, "look");
    }

    public void move(String direction) {
        callApi(new BasicNameValuePair("direction", direction), "move");
    }

    public void attackNpc(String npcId) {
        callApi(new BasicNameValuePair("npcId", npcId), "attack");
    }

    public void pick(String itemId) {
        callApi(new BasicNameValuePair("itemId", itemId), "pick");
    }

    public void compare(String playerId) {
        callApi(new BasicNameValuePair("playerId", playerId), "compare");
    }

    public void talk(String target) {
        callApi(new BasicNameValuePair("target", target), "talk");
    }

    public Optional<BufferedImage> getNpcArt(String npcId) {
        Optional<byte[]> bytes = callApiWithEntity(Collections.singletonList(new BasicNameValuePair("npcId", npcId)), "npcArt");
        try {
            if (bytes.isPresent()) {
                return Optional.ofNullable(ImageIO.read(new ByteArrayInputStream(bytes.get())));
            }
        } catch (IOException e) {
        }
        return Optional.empty();
    }

    public void callApi(String apiMethod) {
        this.callApi(Lists.newArrayList(), apiMethod);
    }

    public void callApi(NameValuePair basicNameValuePair, String apiMethod) {
        callApi(Lists.newArrayList(basicNameValuePair), apiMethod);
    }

    public void callApi(List<NameValuePair> basicNameValuePairs, String apiMethod) {
        Optional<byte[]> bytes = callApiWithEntity(basicNameValuePairs, apiMethod);
    }

    public Optional<byte[]> callApiWithEntity(List<NameValuePair> basicNameValuePairs, String apiMethod) {
        if (!basicAuthSupplier.get().isPresent()) {
            return Optional.empty();
        }
        HttpPost httpPost = new HttpPost("http://" + hostname + ":" + port + "/api/" + apiMethod);
        if (!basicNameValuePairs.isEmpty()) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(basicNameValuePairs, Consts.UTF_8);
            httpPost.setEntity(entity);
        }
        httpPost.setHeader("Authorization", "Basic " + basicAuthSupplier.get().get());
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
            if (response.getEntity() == null) {
                return Optional.empty();
            }
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            return Optional.of(bytes);
        } catch (Exception e) {
            LOG.error("Unable to post api message for: " + apiMethod, e);
        }
        return Optional.empty();
    }

    public ClientConnectionInfo getClientConnectionInfo() throws IOException {
        HttpGet httpGet = new HttpGet("http://" + hostname + ":" + port + "/api/server_info");
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
            String rawJson = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(rawJson, ClientConnectionInfo.class);
        }
    }


}
