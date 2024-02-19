package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

public enum CorrectionStatus {

    PENDING("PENDING"),
    DONE("DONE");

    public String value() {
        return value;
    }

    private final String value;

    CorrectionStatus(String value) {
        this.value = value;
    }
}
