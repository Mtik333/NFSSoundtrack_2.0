package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum ProblemType {

    WRONG_YOUTUBE_LINK("WRONG_YOUTUBE_LINK"),
    WRONG_MUSIC_LINK("WRONG_MUSIC_LINK"),
    WRONG_FLAG("WRONG_FLAG"),
    WRONG_GENRE("WRONG_GENRE"),
    WRONG_LYRICS("WRONG_LYRICS"),
    WRONG_ADDITIONAL_INFO("WRONG_ADDITIONAL_INFO"),
    OTHER_PROBLEM("OTHER_PROBLEM"),
    WRONG_GROUPS("WRONG_GROUPS"),
    DISPLAY_ISSUES("DISPLAY_ISSUES");

    public String value() {
        return value;
    }

    private final String value;

    ProblemType(String value) {
        this.value = value;
    }
}
