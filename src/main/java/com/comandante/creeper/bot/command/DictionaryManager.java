package com.comandante.creeper.bot.command;


import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DictionaryManager {

    private final CreeperConfiguration creeperConfiguration;
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final JsonFactory JSON_FACTORY = new JacksonFactory();
    private final HttpRequestFactory requestFactory;

    private static final Logger log = Logger.getLogger(DictionaryManager.class);


    public DictionaryManager(CreeperConfiguration creeperConfiguration) {
        this.creeperConfiguration = creeperConfiguration;
        this.requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                        request.setHeaders(new HttpHeaders().set("X-Mashape-Key", "HfQbRj3cESmshQnVUNJhUU71ak9Kp1bO2rmjsnGPcgNXS4FiVD"));
                    }
                });
    }

    public List<String> getDefinitionForWord(String word) throws IOException {
        List<String> resp = Lists.newArrayList();
        GenericUrl url = new GenericUrl("https://montanaflynn-dictionary.p.mashape.com/define?word=" + word);
        HttpRequest httpRequest = requestFactory.buildGetRequest(url);
        GenericJson content = httpRequest.execute().parseAs(GenericJson.class);
        resp.addAll(parseDefinition(content));
        return resp;
    }


    private List<String> parseDefinition(GenericJson content) {
        List<String> response = Lists.newArrayList();
        try {
            ArrayList definitions = (ArrayList) content.get("definitions");
            ArrayMap definition = (ArrayMap) definitions.get(0);
            Object text = definition.get("text");
            response.add(text.toString());
        } catch (Exception e) {
            log.error("Error obtaining definition!", e);
        }
        return response;
    }
}