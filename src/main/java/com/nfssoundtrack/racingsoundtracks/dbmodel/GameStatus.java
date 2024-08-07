package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum GameStatus {
    RELEASED("RELEASED"),
    UNRELEASED("UNRELEASED"),
    UNPLAYABLE("UNPLAYABLE"),
    CANCELED("CANCELED");

    public String value() {
        return value;
    }

    private final String value;

    GameStatus(String value) {
        this.value = value;
    }
}
