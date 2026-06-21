package com.nfssoundtrack.racingsoundtracks.dbmodel;

public enum SubgroupType {
    TRAILERS("Trailers only"),
    UNUSED("Found in game files but not used"),
    ALBUM("Included in music album"),
    BETA("Beta soundtrack");

    private final String displayLabel;

    SubgroupType(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }
}
