package com.nfssoundtrack.NFSSoundtrack_20.others;

public class AuthorToDiscoGSObj {

    private long artistId;
    private DiscoGSObj discoGSObj;

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public DiscoGSObj getDiscoGSObj() {
        return discoGSObj;
    }

    public void setDiscoGSObj(DiscoGSObj discoGSObj) {
        this.discoGSObj = discoGSObj;
    }

    public AuthorToDiscoGSObj(long artistId, DiscoGSObj discoGSObj) {
        this.artistId = artistId;
        this.discoGSObj = discoGSObj;
    }
}