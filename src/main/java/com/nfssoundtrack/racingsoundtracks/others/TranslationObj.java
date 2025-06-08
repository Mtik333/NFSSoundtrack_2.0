package com.nfssoundtrack.racingsoundtracks.others;

import java.io.Serializable;

/**
 * used to display these country flags to trigger localization of the website
 */
public class TranslationObj implements Serializable {

    private String value;
    private String countryName;
    private String imageLink;
    private String translationKey;

    public TranslationObj(String value, String countryName, String imageLink, String translationKey) {
        this.value = value;
        this.countryName = countryName;
        this.imageLink = imageLink;
        this.translationKey = translationKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
