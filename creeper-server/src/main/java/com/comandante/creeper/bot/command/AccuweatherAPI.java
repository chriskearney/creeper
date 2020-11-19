package com.comandante.creeper.bot.command;

import com.google.gson.JsonElement;

public interface AccuweatherAPI {
    JsonElement getOneDayForecast(String locationKey);

    JsonElement getLocationByPostalCode(String searchString);

    JsonElement getLocationByCity(String searchString);

    JsonElement getCurrentConditions(String locationKey);

    JsonElement getFiveDayForecast(String locationKey);

    JsonElement getHourlyForecast(String locationKey);

    JsonElement getLocationDetails(String locationKey);
}
