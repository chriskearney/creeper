package com.comandante.creeper.bot.command;

import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.List;

public class AccuweatherClient implements AccuweatherAPI {

    private final static String API_BASE_URL = "http://dataservice.accuweather.com";
    private final static String LOCATIONS_BY_CITY_ENDPOINT = "/locations/v1/cities/search";
    private final static String LOCATIONS_BY_POSTAL_CODE_ENDPOINT = "/locations/v1/postalcodes/search";
    private final static String ONE_DAY_FORECAST_ENDPOINT = "/forecasts/v1/daily/1day/";
    private final static String CURRENT_CONDITIONS_ENDPOINT = "/currentconditions/v1/";
    private final static String FIVE_DAY_FORECAST_ENDPOINT = "/forecasts/v1/daily/5day/";
    private final static String HOURLY_FORECAST_ENDPOINT = "/forecasts/v1/hourly/12hour/";

    private final HttpClient httpClient = HttpClients.custom().build();
    private final JsonParser jsonParser = new JsonParser();
    private final CreeperConfiguration creeperConfiguration;

    public AccuweatherClient(CreeperConfiguration creeperConfiguration) {
        this.creeperConfiguration = creeperConfiguration;
    }

    @Override
    public JsonElement getOneDayForecast(String locationKey) {
        HttpGet request = getAuthWiredRequest(ONE_DAY_FORECAST_ENDPOINT + "/" + locationKey, Lists.newArrayList());
        return getJsonElementFromRequest(request);
    }

    @Override
    public JsonElement getLocationByPostalCode(String searchString) {
        HttpGet request = getAuthWiredRequest(LOCATIONS_BY_POSTAL_CODE_ENDPOINT, Lists.newArrayList(new Tuple<>("q", searchString)));
        return getJsonElementFromRequest(request);
    }

    @Override
    public JsonElement getLocationByCity(String searchString) {
        HttpGet request = getAuthWiredRequest(LOCATIONS_BY_CITY_ENDPOINT, Lists.newArrayList(new Tuple<>("q", searchString)));
        return getJsonElementFromRequest(request);
    }

    @Override
    public JsonElement getCurrentConditions(String locationKey) {
        HttpGet request = getAuthWiredRequest(CURRENT_CONDITIONS_ENDPOINT + "/" + locationKey, Lists.newArrayList(new Tuple<>("details", "true")));
        return getJsonElementFromRequest(request);
    }

    @Override
    public JsonElement getFiveDayForecast(String locationKey) {
        HttpGet request = getAuthWiredRequest(FIVE_DAY_FORECAST_ENDPOINT + "/" + locationKey, Lists.newArrayList(new Tuple<>("details", "false")));
        return getJsonElementFromRequest(request);
    }

    @Override
    public JsonElement getHourlyForecast(String locationKey) {
        HttpGet request = getAuthWiredRequest(HOURLY_FORECAST_ENDPOINT + "/" + locationKey, Lists.newArrayList(new Tuple<>("details", "false")));
        return getJsonElementFromRequest(request);
    }

    private JsonElement getJsonElementFromRequest(HttpGet request) {
        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return jsonParser.parse(responseString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpGet getAuthWiredRequest(String endPoint, List<Tuple<String, String>> params) {
        try {
            final String url = API_BASE_URL + endPoint;
            URIBuilder builder = new URIBuilder(url);
            builder.addParameter("apikey", creeperConfiguration.getAccuweatherApiKey());
            for (Tuple<String, String> param : params) {
                builder.addParameter(param.getX(), param.getY());
            }
            return new HttpGet(builder.build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public class Tuple<X, Y> {
        public final X x;
        public final Y y;

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }

        public X getX() {
            return x;
        }

        public Y getY() {
            return y;
        }
    }
}
