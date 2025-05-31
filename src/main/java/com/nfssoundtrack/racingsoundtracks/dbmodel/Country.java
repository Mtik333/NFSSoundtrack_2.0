package com.nfssoundtrack.racingsoundtracks.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "country")
public class Country implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "countryname")
    private String countryName;

    /**
     * country link is always ending with PNG format
     */
    @Column(name = "countrylink")
    private String countryLink;

    @Column(name = "locallink")
    private String localLink;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryLink() {
        return countryLink;
    }

    public void setCountryLink(String countryLink) {
        this.countryLink = countryLink;
    }

    public String getLocalLink() {
        return localLink;
    }

    public void setLocalLink(String localLink) {
        this.localLink = localLink;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", countryName='" + countryName + '\'' +
                ", countryLink='" + countryLink + '\'' +
                '}';
    }

    public String toChangeLogString() {
        return countryName + " (id: " + id + ")";
    }
}
