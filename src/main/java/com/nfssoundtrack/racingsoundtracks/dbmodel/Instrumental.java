package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum Instrumental {
    YES("YES"),
    NO("NO");

    public String value() {
        return value;
    }

    private final String value;

    Instrumental(String value) {
        this.value = value;
    }

    public static Instrumental fromBoolean(Boolean value) {
        if (Boolean.TRUE.equals(value)) {
            return YES;
        } else {
            return NO;
        }
    }
}
