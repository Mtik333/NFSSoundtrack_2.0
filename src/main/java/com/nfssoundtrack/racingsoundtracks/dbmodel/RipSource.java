package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum RipSource {

    HCS64("HCS64"),
    KHINSIDER("KHINSIDER"),
    ZOPHAR("ZOPHAR"),
    CUSTOM("CUSTOM");

    public String value() {
        return value;
    }

    private final String value;

    RipSource(String value) {
        this.value = value;
    }
}
