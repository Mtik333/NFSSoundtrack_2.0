package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

public enum Remix {

    Y('Y'),N('N');

    public char asChar(){
        return asChar;
    }

    private final char asChar;

    Remix(char asChar){
        this.asChar=asChar;
    }
}
