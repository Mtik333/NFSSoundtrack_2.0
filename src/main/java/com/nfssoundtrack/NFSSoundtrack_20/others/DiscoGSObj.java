package com.nfssoundtrack.NFSSoundtrack_20.others;

public class DiscoGSObj {

    private boolean notInDiscogs;

    private Integer artistId;
    private String uri;
    private String profile;

    private String twitter;

    private String facebook;

    private String instagram;
    private String soundcloud;

    private String myspace;

    private String wikipedia;

    public DiscoGSObj() {
    }

    public DiscoGSObj(String uri, String profile) {
        this.uri = uri;
        this.profile = profile;
    }

    public DiscoGSObj(boolean notInDiscogs, Integer artistId, String uri, String profile) {
        this.notInDiscogs = notInDiscogs;
        this.artistId = artistId;
        this.uri = uri;
        this.profile = profile;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getSoundcloud() {
        return soundcloud;
    }

    public void setSoundcloud(String soundcloud) {
        this.soundcloud = soundcloud;
    }

    public String getMyspace() {
        return myspace;
    }

    public void setMyspace(String myspace) {
        this.myspace = myspace;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public boolean isNotInDiscogs() {
        return notInDiscogs;
    }

    public void setNotInDiscogs(boolean notInDiscogs) {
        this.notInDiscogs = notInDiscogs;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    @Override
    public String toString() {
        return "DiscoGSObj{" +
                "notInDiscogs=" + notInDiscogs +
                ", artistId=" + artistId +
                ", uri='" + uri + '\'' +
                ", profile='" + profile + '\'' +
                ", twitter='" + twitter + '\'' +
                ", facebook='" + facebook + '\'' +
                ", instagram='" + instagram + '\'' +
                ", soundcloud='" + soundcloud + '\'' +
                ", myspace='" + myspace + '\'' +
                ", wikipedia='" + wikipedia + '\'' +
                '}';
    }
}
