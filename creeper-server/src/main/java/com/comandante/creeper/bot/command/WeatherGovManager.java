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

    public String getCurrentWeather(String latitude, String longitude) {
        JsonElement stations = weatherGovApi.getStations(latitude, longitude);
        String firstObservationStation = stations.getAsJsonObject().get("observationStations").getAsJsonArray().get(0).getAsString();
        JsonElement latestObservation = weatherGovApi.getLatestObservations(extractStationIdentifier(firstObservationStation));

        String temperatureCelsius = latestObservation.getAsJsonObject().get("properties").getAsJsonObject().get("temperature").getAsJsonObject().get("value").getAsString();
        int temperatureFahrenheit = (int) Math.round(convertFahrenheit(Double.parseDouble(temperatureCelsius)));

        String windChillTemperatureCelsius = latestObservation.getAsJsonObject().get("properties").getAsJsonObject().get("windChill").getAsJsonObject().get("value").getAsString();
        int windChillTemperatureFahrenheit = (int) Math.round(convertFahrenheit(Double.parseDouble(windChillTemperatureCelsius)));

        String textDescription = latestObservation.getAsJsonObject().get("properties").getAsJsonObject().get("textDescription").getAsString();

        String winDirectionDegrees = latestObservation.getAsJsonObject().get("properties").getAsJsonObject().get("windDirection").getAsJsonObject().get("value").getAsString();
        String windSpeedKmh = latestObservation.getAsJsonObject().get("properties").getAsJsonObject().get("windSpeed").getAsJsonObject().get("value").getAsString();
        int windSpeedMilesPerHour = (int) Math.round(kmphTomph(Double.parseDouble(windSpeedKmh)));

        String visibilityMeters = latestObservation.getAsJsonObject().get("properties").getAsJsonObject().get("visibility").getAsJsonObject().get("value").getAsString();
        int visibilityMiles = (int) Math.round(metersToMiles(Double.parseDouble(visibilityMeters)));

        String humidityPercent = latestObservation.getAsJsonObject().get("properties").getAsJsonObject().get("relativeHumidity").getAsJsonObject().get("value").getAsString();
        int humidityPercentRound = (int) Math.round(Double.parseDouble(humidityPercent));

        String currentConditions = textDescription + " | " + "Temperature: " + temperatureFahrenheit + "F | Wind Chill: " + windChillTemperatureFahrenheit + "F | Humidity: " + humidityPercentRound + "% | Visibility: " + visibilityMiles + "mi | Wind Degrees: " + winDirectionDegrees + " | Wind Speed: " + windSpeedMilesPerHour + "mph";

        return currentConditions;
    }

    public static double convertFahrenheit(double c) {
        return  ((9*c)/5)+32;
    }

    public static double kmphTomph(double kmph) {
        return 0.6214 * kmph;
    }

    public static double metersToMiles(double meters) {
        return meters / 1609.3440057765;
    }

    private String extractStationIdentifier(String nwsStationUrl) {
        List<String> stationParts = Arrays.asList(nwsStationUrl.split("/"));
        return stationParts.get(stationParts.size() - 1);
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
