package com.comandante.creeper.bot.command;

import com.google.gson.JsonElement;

public class AccuweatherManager {

    private final AccuweatherAPI accuweatherAPI;

    public AccuweatherManager(AccuweatherAPI accuweatherAPI) {
        this.accuweatherAPI = accuweatherAPI;
    }

    public AccuweatherReport getCurrentConditions(String searchString) {
        String locationKey = null;
        String englishName = null;
        String administrativeArea = null;

        //    "EnglishName": "Lake Oswego",
        if (Character.isDigit(searchString.charAt(0))) {
            JsonElement locationByPostalCode = accuweatherAPI.getLocationByPostalCode(searchString);
            locationKey = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
            englishName = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("EnglishName").getAsString();
            administrativeArea = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("AdministrativeArea").getAsJsonObject().get("ID").getAsString();

        } else {
            JsonElement locationByCity = accuweatherAPI.getLocationByCity(searchString);
            locationKey = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
        }

        JsonElement currentConditions = accuweatherAPI.getCurrentConditions(locationKey);
        String weatherText = currentConditions.getAsJsonArray().get(0).getAsJsonObject().get("WeatherText").getAsString();
        String humidity = currentConditions.getAsJsonArray().get(0).getAsJsonObject().get("RelativeHumidity").getAsString() + "%";
        String temperature = currentConditions.getAsJsonArray().get(0).getAsJsonObject().get("Temperature").getAsJsonObject().get("Imperial").getAsJsonObject().get("Value").getAsString() + "F";
        String feelslike = currentConditions.getAsJsonArray().get(0).getAsJsonObject().get("RealFeelTemperature").getAsJsonObject().get("Imperial").getAsJsonObject().get("Value").getAsString() + "F";
        String windDirectionEnglish = currentConditions.getAsJsonArray().get(0).getAsJsonObject().get("Wind").getAsJsonObject().get("Direction").getAsJsonObject().get("English").getAsString();
        String windDirectionSpeed = currentConditions.getAsJsonArray().get(0).getAsJsonObject().get("Wind").getAsJsonObject().get("Speed").getAsJsonObject().get("Imperial").getAsJsonObject().get("Value").getAsString();
        String windDirectionUnit = currentConditions.getAsJsonArray().get(0).getAsJsonObject().get("Wind").getAsJsonObject().get("Speed").getAsJsonObject().get("Imperial").getAsJsonObject().get("Unit").getAsString();
        return new AccuweatherReport(englishName + ", " + administrativeArea, weatherText, temperature, humidity, feelslike, windDirectionEnglish + " " + windDirectionSpeed + " " + windDirectionUnit);
    }

    public static class AccuweatherReport {

        private final String properLocationName;
        private final String weatherDescription;
        private final String temperature;
        private final String humidity;
        private final String feelsLike;
        private final String wind;

        public AccuweatherReport(String properLocationName, String weatherDescription, String temperature, String humidity, String feelsLike, String wind) {
            this.properLocationName = properLocationName;
            this.weatherDescription = weatherDescription;
            this.temperature = temperature;
            this.humidity = humidity;
            this.feelsLike = feelsLike;
            this.wind = wind;
        }

        @Override
        public String toString() {
            return properLocationName + ": " + weatherDescription + " | Temperature: " + temperature + " | Humidity: " + humidity + " | Feels Like: " + feelsLike + " | Wind: " + wind;
        }
    }
}
