package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum EntityUrl {

    AUTHOR("author"),
    GENRE("genre"),
    SONG("song"),
    COUNTRYINFO("countryInfo"),
    GAME("game");

    public String value() {
        return value;
    }

    private final String value;

    EntityUrl(String value) {
        this.value = value;
    }
}
