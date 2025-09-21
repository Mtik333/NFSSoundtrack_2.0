package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum CorrectionStatus {

    PENDING("PENDING"),
    DONE("DONE"),
    NOTIFIED("NOTIFIED"),
    REJECTED("REJECTED"),
    REJECTED_NOTIFIED("REJECTED_NOTIFIED"),
    CLARIFY("CLARIFY"),
    CLARIFY_WAITING("CLARIFY_WAITING");

    public String value() {
        return value;
    }

    private final String value;

    CorrectionStatus(String value) {
        this.value = value;
    }
}
