package com.comandante.creeper.cclient;

import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.comandante.creeper.events.PlayerData;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CreeperApiHttpClient extends AbstractScheduledService {

    private final Client client;
    private final EventBus eventBus;
    private final static String url = "/api/events";
    private final String hostname;
    private final Integer port;
    private final BasicAuthStringSupplier basicAuthSupplier;
    private final ObjectMapper objectMapper;

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

    public static class LoggingFilter implements ClientRequestFilter {
        private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
//            System.out.println(requestContext.getEntity().toString());
        }
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
            client.register(new LoggingFilter());

            target = client.target("http://" + hostname + ":" + port + url);
            eventSource = EventSource.target(target)
                    .reconnectingEvery(500, TimeUnit.MILLISECONDS)
                    .usePersistentConnections()
                    .build();
            eventSource.register(inboundSseEvent -> {
                try {
                    if (inboundSseEvent != null) {
                        if (inboundSseEvent.getName().equals("ping")) {
                            System.out.println("ping received from server.");
                            return;
                        }
                        CreeperEvent creeperEvent = objectMapper.readValue(inboundSseEvent.readData(), CreeperEvent.class);
                        if (creeperEvent.getCreeperEventType().equals(CreeperEventType.PLAYERDATA)) {
                            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
                            PlayerData playerData = objectMapper.readValue(creeperEvent.getPayload(), PlayerData.class);
                            eventBus.post(playerData);
                        }
                        eventBus.post(creeperEvent);
                    }
                } catch (Exception e) {
                    System.out.println("Unable to run event loop.");
                    e.printStackTrace();
                }
            });
            eventSource.open();
        } catch (Throwable e) {
            System.out.println("Unable to run event loop.");
            e.printStackTrace();
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

    public void callApi(NameValuePair basicNameValuePair, String apiMethod) {
        this.callApi(Lists.newArrayList(basicNameValuePair), apiMethod);
    }

    public void callApi(List<NameValuePair> basicNameValuePairs, String apiMethod) {
        if (!basicAuthSupplier.get().isPresent()) {
            return;
        }
        HttpPost httpPost = new HttpPost("http://" + hostname + ":" + port + "/api/" + apiMethod);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(basicNameValuePairs, Consts.UTF_8);
        httpPost.setHeader("Authorization", "Basic " + basicAuthSupplier.get().get());
        httpPost.setEntity(entity);
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
            EntityUtils.consume(response.getEntity());
        } catch (Exception e) {
            System.out.println("unable to post api message for: " + apiMethod);
            e.printStackTrace();
        }
    }



}
