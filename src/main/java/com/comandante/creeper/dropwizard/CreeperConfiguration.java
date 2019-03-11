package com.comandante.creeper.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.NotNull;

public class CreeperConfiguration extends Configuration {

    @NotEmpty
    private String template = "template";

    @NotEmpty
    private String defaultName = "creeper";

    @NotNull
    private Boolean isProduction = false;

    @NotNull
    private Integer telnetPort = 8080;

    @NotEmpty
    private String databaseFileName = "creeper.mapdb";

    @NotEmpty
    private String graphiteHost = "localhost";

    @NotNull
    private Integer graphitePort = 2004;

    @NotEmpty
    private String ircServer = "Chicago.IL.US.Undernet.org";

    @NotEmpty
    private String ircUsername = "creeper";

    @NotEmpty
    private String ircNickname = "creeper1";

    @NotEmpty
    private String ircChannel = "#creeper";

    @NotNull
    private Integer ircBridgeroomId = 376;

    @NotNull
    private Boolean ircEnabled = false;

    @NotNull
    private Integer defaultMapSize = 14;

    @NotNull
    private Double forageRateLimitPerSecond = 1.5;

    @NotEmpty
    private String weatherUndergroundApiKey = "dead!";

    @NotEmpty
    private String accuweatherApiKey = "";

    @NotNull
    private Integer maxGossipCacheSize = 10000;

    private Boolean isGraphite = false;

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }

    @JsonProperty
    public Boolean isGraphite() {
        return isGraphite;
    }

    public void setGraphite(Boolean graphite) {
        this.isGraphite = graphite;
    }


    @JsonProperty
    public Boolean isProduction() {
        return isProduction;
    }
    @JsonProperty
    public void setProduction(Boolean production) {
        isProduction = production;
    }
    @JsonProperty
    public Integer getTelnetPort() {
        return telnetPort;
    }
    @JsonProperty
    public void setTelnetPort(Integer telnetPort) {
        this.telnetPort = telnetPort;
    }
    @JsonProperty
    public String getDatabaseFileName() {
        return databaseFileName;
    }
    @JsonProperty
    public void setDatabaseFileName(String databaseFileName) {
        this.databaseFileName = databaseFileName;
    }
    @JsonProperty
    public String getGraphiteHost() {
        return graphiteHost;
    }
    @JsonProperty
    public void setGraphiteHost(String graphiteHost) {
        this.graphiteHost = graphiteHost;
    }
    @JsonProperty
    public Integer getGraphitePort() {
        return graphitePort;
    }
    @JsonProperty
    public void setGraphitePort(Integer graphitePort) {
        this.graphitePort = graphitePort;
    }
    @JsonProperty
    public String getIrcServer() {
        return ircServer;
    }
    @JsonProperty
    public void setIrcServer(String ircServer) {
        this.ircServer = ircServer;
    }
    @JsonProperty
    public String getIrcUsername() {
        return ircUsername;
    }
    @JsonProperty
    public void setIrcUsername(String ircUsername) {
        this.ircUsername = ircUsername;
    }
    @JsonProperty
    public String getIrcNickname() {
        return ircNickname;
    }
    @JsonProperty
    public void setIrcNickname(String ircNickname) {
        this.ircNickname = ircNickname;
    }
    @JsonProperty
    public String getIrcChannel() {
        return ircChannel;
    }
    @JsonProperty
    public void setIrcChannel(String ircChannel) {
        this.ircChannel = ircChannel;
    }
    @JsonProperty
    public Integer getIrcBridgeroomId() {
        return ircBridgeroomId;
    }
    @JsonProperty
    public void setIrcBridgeroomId(Integer ircBridgeroomId) {
        this.ircBridgeroomId = ircBridgeroomId;
    }
    @JsonProperty
    public Boolean isIrcEnabled() {
        return ircEnabled;
    }
    @JsonProperty
    public void setIrcEnabled(Boolean ircEnabled) {
        this.ircEnabled = ircEnabled;
    }
    @JsonProperty
    public Integer getDefaultMapSize() {
        return defaultMapSize;
    }
    @JsonProperty
    public void setDefaultMapSize(Integer defaultMapSize) {
        this.defaultMapSize = defaultMapSize;
    }
    @JsonProperty
    public Double getForageRateLimitPerSecond() {
        return forageRateLimitPerSecond;
    }
    @JsonProperty
    public void setForageRateLimitPerSecond(Double forageRateLimitPerSecond) {
        this.forageRateLimitPerSecond = forageRateLimitPerSecond;
    }
    @JsonProperty
    public String getWeatherUndergroundApiKey() {
        return weatherUndergroundApiKey;
    }
    @JsonProperty
    public void setWeatherUndergroundApiKey(String weatherUndergroundApiKey) {
        this.weatherUndergroundApiKey = weatherUndergroundApiKey;
    }

    public String getAccuweatherApiKey() {
        return accuweatherApiKey;
    }

    @JsonProperty
    public Integer getMaxGossipCacheSize() {
        return maxGossipCacheSize;
    }
    @JsonProperty
    public void setMaxGossipCacheSize(Integer maxGossipCacheSize) {
        this.maxGossipCacheSize = maxGossipCacheSize;
    }
}
