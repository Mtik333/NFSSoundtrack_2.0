package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

public enum MultiConcat {
    MINUS('-'), X('X'), AND('&');

    public char asChar() {
        return asChar;
    }

    private final char asChar;

    MultiConcat(char asChar) {
        this.asChar = asChar;
    }
}
