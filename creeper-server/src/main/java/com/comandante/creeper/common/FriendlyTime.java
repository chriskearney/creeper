package com.comandante.creeper.common;

public class FriendlyTime {

    private final long originalSeconds;

    private final long seconds;
    private final long minutes;
    private final long hours;
    private final long days;
    private final long months;
    private final long years;

    private final String friendlyFormatted;
    private final String friendlyFormattedShort;

    public FriendlyTime(long originalSeconds) {
        this.originalSeconds = originalSeconds;
        this.seconds = (originalSeconds >= 60 ? originalSeconds % 60 : originalSeconds);
        this.minutes = (originalSeconds = (originalSeconds / 60)) >= 60 ? originalSeconds % 60 : originalSeconds;
        this.hours = (originalSeconds = (originalSeconds / 60)) >= 24 ? originalSeconds % 24 : originalSeconds;
        this.days = (originalSeconds = (originalSeconds / 24)) >= 30 ? originalSeconds % 30 : originalSeconds;
        this.months = (originalSeconds = (originalSeconds / 30)) >= 12 ? originalSeconds % 12 : originalSeconds;
        this.years = (originalSeconds = (originalSeconds / 12));
        this.friendlyFormatted = buildFriendlyFormatted();
        this.friendlyFormattedShort = buildFriendlyFormattedShort();
    }

    public long getOriginalSeconds() {
        return originalSeconds;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getHours() {
        return hours;
    }

    public long getDays() {
        return days;
    }

    public long getMonths() {
        return months;
    }

    public long getYears() {
        return years;
    }

    public String getFriendlyFormatted() {
        return friendlyFormatted;
    }

    public String getFriendlyFormattedShort() {
        return friendlyFormattedShort;
    }

    private String buildFriendlyFormattedShort() {
        StringBuilder sb = new StringBuilder();
        if (years > 0) {
            sb.append(years).append("y");
            if (years <= 6 && months > 0) {
                sb.append(months).append("mo");
            }
        } else if (months > 0) {
            sb.append(months).append("mo");
            if (months <= 6 && days > 0) {
                sb.append(days).append("d");
            }
        } else if (days > 0) {
            sb.append(days).append("d");
            if (days <= 3 && hours > 0) {
                sb.append(hours).append("h");
            }
        } else if (hours > 0) {
            sb.append(hours).append("h");
            if (minutes > 1) {
                sb.append(minutes).append("m");
            }
        } else if (minutes > 0) {
            sb.append(minutes).append("m");
            if (seconds > 1) {
                sb.append(seconds).append("s");
            }
        } else {
            sb.append(seconds).append("s");
        }
        return sb.toString();
    }

    private String buildFriendlyFormatted() {
        StringBuilder sb = new StringBuilder();
        if (years > 0) {
            if (years == 1) {
                sb.append("a year");
            } else {
                sb.append(years).append(" years");
            }
            if (years <= 6 && months > 0) {
                if (months == 1) {
                    sb.append(" and a month");
                } else {
                    sb.append(" and ").append(months).append(" months");
                }
            }
        } else if (months > 0) {
            if (months == 1) {
                sb.append("a month");
            } else {
                sb.append(months).append(" months");
            }
            if (months <= 6 && days > 0) {
                if (days == 1) {
                    sb.append(" and a day");
                } else {
                    sb.append(" and ").append(days).append(" days");
                }
            }
        } else if (days > 0) {
            if (days == 1) {
                sb.append("a day");
            } else {
                sb.append(days).append(" days");
            }
            if (days <= 3 && hours > 0) {
                if (hours == 1) {
                    sb.append(" and an hour");
                } else {
                    sb.append(" and ").append(hours).append(" hours");
                }
            }
        } else if (hours > 0) {
            if (hours == 1) {
                sb.append("an hour");
            } else {
                sb.append(hours).append(" hours");
            }
            if (minutes > 1) {
                sb.append(" and ").append(minutes).append(" minutes");
            }
        } else if (minutes > 0) {
            if (minutes == 1) {
                sb.append("a minute");
            } else {
                sb.append(minutes).append(" minutes");
            }
            if (seconds > 1) {
                sb.append(" and ").append(seconds).append(" seconds");
            }
        } else {
            if (seconds <= 1) {
                sb.append("about a second");
            } else {
                sb.append("about ").append(seconds).append(" seconds");
            }
        }
        return sb.toString();
    }

}
