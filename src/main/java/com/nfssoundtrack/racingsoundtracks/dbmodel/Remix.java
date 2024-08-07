package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum Remix {

    YES("YES"),
    NO("NO");

    public String value() {
        return value;
    }

    private final String value;

    Remix(String value) {
        this.value = value;
    }

    public static Remix fromBoolean(Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            return YES;
        } else {
            return NO;
        }
    }
}
