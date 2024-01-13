package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;

public class DiscoGSObj {

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
}
