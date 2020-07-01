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

    private String graphiteHost = "localhost";

    private Integer graphitePort = 2004;

    private String ircServer = "Chicago.IL.US.Undernet.org";

    private String ircUsername = "creeper";

    private String ircNickname = "creeper1";

    private String ircChannel = "#creeper";

    private Integer ircBridgeroomId = 376;

    private Boolean ircEnabled = false;

    @NotNull
    private Integer defaultMapSize = 14;

    @NotNull
    private Double forageRateLimitPerSecond = 1.5;

    private String weatherUndergroundApiKey = "dead!";
    private String twitterConsumerKey = "";
    private String twitterConsumerSecret = "";
    private String twitterAccessToken = "";
    private String twitterAccessTokenSecret = "";

    @NotEmpty
    private String accuweatherApiKey = "";

    @NotNull
    private Integer maxGossipCacheSize = 10000;

    private Boolean isGraphite = false;

    private String clientPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpAIBAAKCAQEAng//Dz5Ae/+b3I6JrClN2PGtKmXkYN5SPFby4UNniOyaOdPZ\n" +
            "VoiM5TgjIT4YSc/xBftBhFnkNfwkIMQSfUSzK3ayt9JLUEiXA4bS0/K2Z9401JHv\n" +
            "x24ggg5X/wsD3CYQdkvaNJPyYSvEUrw54K3PO0ncAVAz51o0vOeYZ9QcmQ35h2rV\n" +
            "Frt7druqiIqZXBZroe26g8C5p4wQPw9B7NB0F9TaKzpnu06SROq+ylqlLk2lRsyd\n" +
            "NdmMKT482AUcLQI+vdtKckiMMYpeYcWCUthp+INAhLjvKhU+ze65DUGqjte7z2vA\n" +
            "CvosPjkBZW2plYe37TUD/z4cChVixmYK0tGBLQIDAQABAoIBAF0ldhyohd3M8y/V\n" +
            "u+H98UdgnLi4lQ5U1ceDQdxoGXKCZByh8JljHQzTuTgU3MFazmHFAIYbZzY9IZlB\n" +
            "ktfJjYCWkZlsTz+/l2bXpBSNGts5Ao8mdVRkXLnn9Alzl1G8CMV9y9hiO1ryLN7I\n" +
            "157FLrN0wjbv6bZz3VJZdOHzCOtUeJ1PvKkmh42QwqpgfQ/e3JmUYA9NJFcf1ehz\n" +
            "0OpqAHsPPGE0AL65iKkpeNUsmNRyQTb69495j13YiwpyRNZR48ScaV0LHdAsOnVw\n" +
            "2StjIpZsDfSnAQoxBs4wKhqX6xgPpuSVAkWhGDZ1WhXODh5ZQ6WWY/146V7NXiFF\n" +
            "7/Uuwq0CgYEA0m6AYpBMpGSjNVrUgVADF5s1xahPeInihhw5jEaDj8AK78n6dR04\n" +
            "WWm9hSFbxWtzsM/A2TMr2L+cewkbTP0t6ENVfezogYDbnZIT0GzwZvoqWrcr7mCi\n" +
            "5zx1czLZ5Qfbz19igQkpA3Ib57us9o79cBE88P15GvSZHS9ifjBcOqMCgYEAwEpb\n" +
            "CG4IOdrYQk3EC0c8S8VUEt+Or/Z63IG7AejAdfv8UpBjp+jIH1GI25+qmw5nFcB7\n" +
            "a2Jd9EUdr0FOZSxMWmmhypNpQ1KC0K6iuvUrdQAXyjQnZYTp+TyEJfi5W4LKN3Up\n" +
            "HfP8zXWUzRHMtfB9vVSxqYxQF2rUT/B8Mzp1Ye8CgYAPqnhWXiXGi1N3MmblpZ5F\n" +
            "UKHFME2STLmXgFxsbAd5WTO3PFMwCtfaGDwqwBwD64b2X9EcmmmmPkWZB0mIBsU3\n" +
            "KGQh9tQsZ/pxlaFx/9o54F/s1vwnR/x4uJCJ3fxIx7f+jTxZHOR3xDP9oYQz6ttF\n" +
            "T5M44bX1YsZPXOq5OEJ1fwKBgQC8hencSOx9tGbEErQ7Dns6GlwEKPQe5nusRvCO\n" +
            "vaA7zHKki/V4gMv7kJeqI09DuAovFEisjoNo4n5o/ZEbtiOhnODH2GCiZXnlmOHo\n" +
            "hEg37IBmeV2KtZYjCkbRZ5pq8r7JQm+uczCOS1I4/9OBKShOAIQyo2M+ojlHqpJK\n" +
            "M200NwKBgQCpyUuRGQi3ck7FK2jlSBiHbhkMlLYxyGalIYenbW35+wWPtHEOmGDg\n" +
            "c8WXDhR6hQEBT4E23Kx1plDEcLAr8Z8Z5s+OcZTsvfP5+qcCoQjAryV+5Z4hMlb8\n" +
            "E1TWC/o4d36fC+3fPCx3krJ/JHy0XkOXgq7HjWtuu9VXKvPwIqeQ1g==\n" +
            "-----END RSA PRIVATE KEY-----";

    private String clientPublicKey = "com.ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCeD/8PPkB7/5vcjomsKU3Y8a0qZeR" +
            "g3lI8VvLhQ2eI7Jo509lWiIzlOCMhPhhJz/EF+0GEWeQ1/CQgxBJ9RLMrdrK30ktQSJcDhtLT8" +
            "rZn3jTUke/HbiCCDlf/CwPcJhB2S9o0k/JhK8RSvDngrc87SdwBUDPnWjS855hn1ByZDfmHatUW" +
            "u3t2u6qIiplcFmuh7bqDwLmnjBA/D0Hs0HQX1NorOme7TpJE6r7KWqUuTaVGzJ012YwpPjzYBR" +
            "wtAj6920pySIwxil5hxYJS2Gn4g0CEuO8qFT7N7rkNQaqO17vPa8AK+iw+OQFlbamVh7ftNQP/P" +
            "KFWLGZgrS0YEt bridge@creeper";

    private String sshHostname = "creeper.ktwit.net";
    private int sshPort = 30000;
    private String sshUser = "bridge";
    private String sshPass = "";

    private String clientPassPhrase = "";

    private String clientConnectHostname = "creeper.ktwit.net";

    @JsonProperty
    public String getSshHostname() {
        return sshHostname;
    }

    @JsonProperty
    public void setSshHostname(String sshHostname) {
        this.sshHostname = sshHostname;
    }

    @JsonProperty
    public int getSshPort() {
        return sshPort;
    }

    @JsonProperty
    public void setSshPort(int sshPort) {
        this.sshPort = sshPort;
    }

    @JsonProperty
    public String getSshUser() {
        return sshUser;
    }

    @JsonProperty
    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    @JsonProperty
    public String getSshPass() {
        return sshPass;
    }
    @JsonProperty
    public void setSshPass(String sshPass) {
        this.sshPass = sshPass;
    }

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

    @JsonProperty
    public String getTwitterConsumerKey() {
        return twitterConsumerKey;
    }

    public void setTwitterConsumerKey(String twitterConsumerKey) {
        this.twitterConsumerKey = twitterConsumerKey;
    }

    @JsonProperty
    public String getTwitterConsumerSecret() {
        return twitterConsumerSecret;
    }

    public void setTwitterConsumerSecret(String twitterConsumerSecret) {
        this.twitterConsumerSecret = twitterConsumerSecret;
    }

    @JsonProperty
    public String getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public void setTwitterAccessToken(String twitterAccessToken) {
        this.twitterAccessToken = twitterAccessToken;
    }

    @JsonProperty
    public String getTwitterAccessTokenSecret() {
        return twitterAccessTokenSecret;
    }

    public void setTwitterAccessTokenSecret(String twitterAccessTokenSecret) {
        this.twitterAccessTokenSecret = twitterAccessTokenSecret;
    }
    @JsonProperty
    public String getClientPrivateKey() {
        return clientPrivateKey;
    }

    @JsonProperty
    public String getClientPublicKey() {
        return clientPublicKey;
    }

    @JsonProperty
    public String getClientPassPhrase() {
        return clientPassPhrase;
    }

    @JsonProperty
    public String getClientConnectHostname() {
        return clientConnectHostname;
    }

    @JsonProperty
    public void setClientPrivateKey(String clientPrivateKey) {
        this.clientPrivateKey = clientPrivateKey;
    }

    @JsonProperty
    public void setClientPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    @JsonProperty
    public void setClientPassPhrase(String clientPassPhrase) {
        this.clientPassPhrase = clientPassPhrase;
    }

    @JsonProperty
    public void setClientConnectHostname(String clientConnectHostname) {
        this.clientConnectHostname = clientConnectHostname;
    }
}
