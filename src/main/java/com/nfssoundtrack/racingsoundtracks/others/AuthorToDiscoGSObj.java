package com.nfssoundtrack.racingsoundtracks.others;

/**
 * kind of overlay layer to point between author id from database
 * and the non-DTO class to keep a lot of info about artists
 */
public class AuthorToDiscoGSObj {

    private long artistId;
    private boolean ignoredByDiscogs;
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

    public boolean isIgnoredByDiscogs() {
        return ignoredByDiscogs;
    }

    public void setIgnoredByDiscogs(boolean ignoredByDiscogs) {
        this.ignoredByDiscogs = ignoredByDiscogs;
    }

    public AuthorToDiscoGSObj(long artistId, DiscoGSObj discoGSObj) {
        this.artistId = artistId;
        this.discoGSObj = discoGSObj;
    }
}
