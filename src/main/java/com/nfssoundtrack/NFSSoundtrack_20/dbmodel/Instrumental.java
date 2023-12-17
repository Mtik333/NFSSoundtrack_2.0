package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

public enum Instrumental {
    Y('Y'),N('N');

    public char asChar(){
        return asChar;
    }

    private final char asChar;

    Instrumental(char asChar){
        this.asChar=asChar;
    }
}
