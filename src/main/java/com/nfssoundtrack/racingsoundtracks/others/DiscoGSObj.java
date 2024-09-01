package com.nfssoundtrack.racingsoundtracks.others;

/**
 * DTO that consists of id of author in discogs, all his social media
 * and the boolean that allows us to ignore artist for purpose of looking up
 * information on discogs which might be just not present
 */
public class DiscoGSObj {

    private boolean notInDiscogs;

    private Integer discogsId;
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
        this.discogsId = artistId;
        this.uri = uri;
        this.profile = profile;
    }

    public void setSocialLink(String twitter, String facebook, String instagram, String soundcloud, String myspace, String wikipedia){
        this.twitter = twitter;
        this.facebook = facebook;
        this.instagram = instagram;
        this.soundcloud = soundcloud;
        this.myspace = myspace;
        this.wikipedia = wikipedia;
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

    public Integer getDiscogsId() {
        return discogsId;
    }

    public void setDiscogsId(Integer artistId) {
        this.discogsId = artistId;
    }

    @Override
    public String toString() {
        return "DiscoGSObj{" +
                "notInDiscogs=" + notInDiscogs +
                ", discogsId=" + discogsId +
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
