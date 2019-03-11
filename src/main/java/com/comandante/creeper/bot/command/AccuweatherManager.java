package com.comandante.creeper.bot.command;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

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
            englishName = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("EnglishName").getAsString();
            administrativeArea = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("AdministrativeArea").getAsJsonObject().get("ID").getAsString();
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

    public String getFiveDayForeCast(String searchString) {
        String locationKey = null;
        String englishName = null;
        String administrativeArea = null;

        if (Character.isDigit(searchString.charAt(0))) {
            JsonElement locationByPostalCode = accuweatherAPI.getLocationByPostalCode(searchString);
            locationKey = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
            englishName = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("EnglishName").getAsString();
            administrativeArea = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("AdministrativeArea").getAsJsonObject().get("ID").getAsString();
        } else {
            JsonElement locationByCity = accuweatherAPI.getLocationByCity(searchString);
            locationKey = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
            englishName = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("EnglishName").getAsString();
            administrativeArea = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("AdministrativeArea").getAsJsonObject().get("ID").getAsString();
        }

        JsonElement fiveDayForecast = accuweatherAPI.getFiveDayForecast(locationKey);

        List<DailyForecastSummary> forecastSummaries = Lists.newArrayList();
        for (JsonElement dailyForecastElement : fiveDayForecast.getAsJsonObject().get("DailyForecasts").getAsJsonArray()) {
            long epochDate = dailyForecastElement.getAsJsonObject().get("EpochDate").getAsLong();
            String dayOfTheWeek = getDayOfTheWeek(epochDate);
            String minTemp = dailyForecastElement.getAsJsonObject().get("Temperature").getAsJsonObject().get("Minimum").getAsJsonObject().get("Value").getAsString() + "F";
            String maxTemp = dailyForecastElement.getAsJsonObject().get("Temperature").getAsJsonObject().get("Maximum").getAsJsonObject().get("Value").getAsString() + "F";
            String dailySummary = dailyForecastElement.getAsJsonObject().get("Day").getAsJsonObject().get("IconPhrase").getAsString();
            forecastSummaries.add(new DailyForecastSummary(dayOfTheWeek, minTemp, maxTemp, dailySummary, epochDate));
        }


        StringBuilder stringBuilder = new StringBuilder();
        for (DailyForecastSummary dailyForecastSummary: forecastSummaries) {
            stringBuilder.append(dailyForecastSummary.toString());
            stringBuilder.append(" | ");
        }

        String s = stringBuilder.toString();

        return englishName + ", " + administrativeArea + ": " + s.substring(0, s.length() - 2);

    }

    private String getDayOfTheWeek(long epochDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        Date dateFormat = new java.util.Date(epochDate* 1000);
        String weekday = sdf.format(dateFormat );
        return weekday;
    }

    public static class DailyForecastSummary {

        private final String dayOfWeek;
        private final String tempLow;
        private final String tempHigh;
        private final String dailySummary;
        private final Long epochDate;

        public DailyForecastSummary(String dayOfWeek, String tempLow, String tempHigh, String dailySummary, Long epochDate) {
            this.dayOfWeek = dayOfWeek;
            this.tempLow = tempLow;
            this.tempHigh = tempHigh;
            this.dailySummary = dailySummary;
            this.epochDate = epochDate;
        }

        @Override
        public String toString() {
            return dayOfWeek + " " + tempLow + "/" + tempHigh + " " + dailySummary;
        }

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
