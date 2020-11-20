package com.comandante.creeper.bot.command;

import com.google.gson.JsonElement;
import org.testng.collections.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class WeatherGovManager {

    private final WeatherGovApi weatherGovApi;

    public WeatherGovManager(WeatherGovApi weatherGovApi) {
        this.weatherGovApi = weatherGovApi;
    }

    public Optional<List<String>> getAlerts(String latitude, String longitude) {
        try {
            JsonElement alertData = weatherGovApi.getAlertData(latitude, longitude);
            List<String> alertStrings = Lists.newArrayList();
            String alertHeadline = alertData.getAsJsonObject().get("features").getAsJsonArray().get(0).getAsJsonObject().get("properties").getAsJsonObject().get("headline").getAsString();
            String alertDescription = alertData.getAsJsonObject().get("features").getAsJsonArray().get(0).getAsJsonObject().get("properties").getAsJsonObject().get("description").getAsString();
            alertStrings.add(alertHeadline);
            alertStrings.addAll(reformatAlertDescription(alertDescription));
            return Optional.of(alertStrings);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public AccuweatherManager.AccuweatherReport getCurrentWeather() {

    }

    protected List<String> reformatAlertDescription(String rawAlert) {
        List<String> formattedFinal = Lists.newArrayList();
        List<String> strings = Arrays.asList(rawAlert.split("\n\n"));
        strings.forEach(s -> {
            String replace = s.replaceAll("\n", " ");
            formattedFinal.add(replace);
        });
        return formattedFinal;

    }
}
