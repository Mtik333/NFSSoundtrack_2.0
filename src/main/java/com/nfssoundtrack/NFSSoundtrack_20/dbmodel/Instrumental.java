package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

public enum Instrumental {
    YES("YES"), NO("NO");

    public String value() {
        return value;
    }

    private final String value;

    Instrumental(String value) {
        this.value = value;
    }

    public static Instrumental fromBoolean(Boolean value) {
        if (value) {
            return YES;
        } else {
            return NO;
        }
    }
}
