package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum EntityType {

    AUTHOR_ALIAS("Author-Alias"),
    AUTHOR("Author"),
    AUTHOR_COUNTRY("Author-Country"),
    AUTHOR_MEMBER("Author-Member"),
    AUTHOR_SONG("Author-SOng"),
    COUNTRY("Country"),
    GAME("Game"),
    GENRE("Genre"),
    MAIN_GROUP("Main-Group"),
    SERIE("Serie"),
    SONG("Song"),
    SONG_GENRE("Song-Genre"),
    SONG_SUBGROUP("Song-Subgroup"),
    SUBGROUP("Subgroup");

    public String value() {
        return value;
    }

    private final String value;

    EntityType(String value) {
        this.value = value;
    }
}
