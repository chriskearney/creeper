package com.comandante.creeper.bot.command;

import com.comandante.creeper.bot.IrcBotService;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AccuweatherManager extends AbstractScheduledService {

    private final AccuweatherAPI accuweatherAPI;
    private final WeatherGovManager weatherGovManager;
    private final IrcBotService ircBotService;
    private final CreeperConfiguration creeperConfiguration;
    private final ArrayBlockingQueue<String> alertCheckQueue = new ArrayBlockingQueue<>(10);

    public AccuweatherManager(AccuweatherAPI accuweatherAPI, IrcBotService ircBotService, WeatherGovManager weatherGovManager, CreeperConfiguration creeperConfiguration) {
        this.accuweatherAPI = accuweatherAPI;
        this.ircBotService = ircBotService;
        this.weatherGovManager = weatherGovManager;
        this.creeperConfiguration = creeperConfiguration;
        this.startAsync();
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

        alertCheckQueue.add(locationKey);

        return new AccuweatherReport(englishName + ", " + administrativeArea, weatherText, temperature, humidity, feelslike, windDirectionEnglish + " " + windDirectionSpeed + " " + windDirectionUnit);
    }

    public String getCords(String searchString) {
        String locationKey = null;
        if (Character.isDigit(searchString.charAt(0))) {
            JsonElement locationByPostalCode = accuweatherAPI.getLocationByPostalCode(searchString);
            locationKey = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
        } else {
            JsonElement locationByCity = accuweatherAPI.getLocationByCity(searchString);
            locationKey = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
        }

        return getLatLong(locationKey);
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
            String minTemp = dailyForecastElement.getAsJsonObject().get("Temperature").getAsJsonObject().get("Minimum").getAsJsonObject().get("Value").getAsString();
            int minRounded = (int) Double.parseDouble(minTemp);
            String maxTemp = dailyForecastElement.getAsJsonObject().get("Temperature").getAsJsonObject().get("Maximum").getAsJsonObject().get("Value").getAsString();
            int maxRounded = (int) Double.parseDouble(maxTemp);
            String dailySummary = dailyForecastElement.getAsJsonObject().get("Day").getAsJsonObject().get("IconPhrase").getAsString();
            forecastSummaries.add(new DailyForecastSummary(dayOfTheWeek, minRounded + "F", maxRounded + "F", dailySummary, epochDate));
        }


        StringBuilder stringBuilder = new StringBuilder();
        for (DailyForecastSummary dailyForecastSummary : forecastSummaries) {
            stringBuilder.append(dailyForecastSummary.toString());
            stringBuilder.append(" | ");
        }

        String s = stringBuilder.toString();

        return englishName + ", " + administrativeArea + ": " + s.substring(0, s.length() - 2);

    }

    public String getHourlyForecast(String searchString) {
        String locationKey = null;

        if (Character.isDigit(searchString.charAt(0))) {
            JsonElement locationByPostalCode = accuweatherAPI.getLocationByPostalCode(searchString);
            locationKey = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
        } else {
            JsonElement locationByCity = accuweatherAPI.getLocationByCity(searchString);
            locationKey = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
        }

        JsonElement hourlyForecast = accuweatherAPI.getHourlyForecast(locationKey);

        StringBuilder combinedHourlyForecast = new StringBuilder();
        combinedHourlyForecast.append(searchString).append(" : ");
        for (JsonElement element : hourlyForecast.getAsJsonArray()) {
            String rawDateTime = element.getAsJsonObject().get("DateTime").getAsString();
            ZonedDateTime localDateTime = ZonedDateTime.parse(rawDateTime);
            DateTimeFormatter timeFormatter = DateTimeFormatter
                    .ofLocalizedTime(FormatStyle.SHORT)
                    .withLocale(Locale.US);

            String rawTemperatureInteger = element.getAsJsonObject().get("Temperature").getAsJsonObject().get("Value").getAsString();
            Integer temperature = (int) Float.parseFloat(rawTemperatureInteger);
            String temperatureUnit = element.getAsJsonObject().get("Temperature").getAsJsonObject().get("Unit").getAsString();

            String displayTime = timeFormatter.format(localDateTime);
            combinedHourlyForecast.append(displayTime.replaceAll(":00 ", ""));
            combinedHourlyForecast.append(" ")
                    .append(temperature)
                    .append(temperatureUnit)
                    .append("/")
                    .append(element.getAsJsonObject().get("IconPhrase").getAsString())
                    .append(" | ");
        }

        String s = combinedHourlyForecast.toString();

        return s.substring(0, s.length() - 3);
    }

    public String getAQI(String searchString) {
        String locationKey = null;

        if (Character.isDigit(searchString.charAt(0))) {
            JsonElement locationByPostalCode = accuweatherAPI.getLocationByPostalCode(searchString);
            locationKey = locationByPostalCode.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
        } else {
            JsonElement locationByCity = accuweatherAPI.getLocationByCity(searchString);
            locationKey = locationByCity.getAsJsonArray().get(0).getAsJsonObject().get("Key").getAsString();
        }

        JsonElement oneDayForecast = accuweatherAPI.getOneDayForecast(locationKey);
        String aqi = null;

        JsonArray AQI = oneDayForecast.getAsJsonObject().get("DailyForecasts").getAsJsonArray().get(0).getAsJsonObject().get("AirAndPollen").getAsJsonArray();
        for (JsonElement next : AQI) {
            if (next.getAsJsonObject().has("Name") && next.getAsJsonObject().get("Name").getAsString().equals("AirQuality")) {
                aqi = next.getAsJsonObject().get("Value").getAsString();
            }
        }

        return "AQI: " + aqi;
    }

    private String getLatLong(String locationKey) {
        JsonElement locationDetails = accuweatherAPI.getLocationDetails(locationKey);
        String latitude = locationDetails.getAsJsonObject().get("GeoPosition").getAsJsonObject().get("Latitude").getAsString();
        String longitude = locationDetails.getAsJsonObject().get("GeoPosition").getAsJsonObject().get("Longitude").getAsString();
        return latitude + "," + longitude;
    }

    private String getDayOfTheWeek(long epochDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        Date dateFormat = new java.util.Date(epochDate * 1000);
        String weekday = sdf.format(dateFormat);
        return weekday;
    }

    @Override
    protected void runOneIteration() throws Exception {
        try {
            if (alertCheckQueue.isEmpty()) {
                return;
            }
            List<String> locationKeysToCheckForAlerts = Lists.newArrayList();
            alertCheckQueue.drainTo(locationKeysToCheckForAlerts);
            for (String locationKey : locationKeysToCheckForAlerts) {
                JsonElement locationDetails = accuweatherAPI.getLocationDetails(locationKey);
                String latitude = locationDetails.getAsJsonObject().get("GeoPosition").getAsJsonObject().get("Latitude").getAsString();
                String longitude = locationDetails.getAsJsonObject().get("GeoPosition").getAsJsonObject().get("Longitude").getAsString();
                Optional<List<String>> alerts = weatherGovManager.getAlerts(latitude, longitude);
                if (alerts.isPresent()) {
                    for (String alertLine : alerts.get()) {
                        ircBotService.getBot().getUserChannelDao().getChannel(creeperConfiguration.getIrcChannel()).send().message(alertLine);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 1, TimeUnit.SECONDS);
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
            return dayOfWeek + " " + tempHigh + "/" + tempLow + " " + dailySummary;
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
