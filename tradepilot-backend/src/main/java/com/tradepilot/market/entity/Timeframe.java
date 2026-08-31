package com.tradepilot.market.entity;

public enum Timeframe {

    ONE_MINUTE("1m"),
    FIVE_MINUTES("5m"),
    FIFTEEN_MINUTES("15m"),
    THIRTY_MINUTES("30m"),
    ONE_HOUR("1h"),
    ONE_DAY("1d");

    private final String value;

    Timeframe(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}