package com.comandante.creeper.bot.command;

public class WeatherAlertReceivedEvent {
    private final String weatherAlert;

    public WeatherAlertReceivedEvent(String weatherAlert) {
        this.weatherAlert = weatherAlert;
    }

    public String getWeatherAlert() {
        return weatherAlert;
    }
}
