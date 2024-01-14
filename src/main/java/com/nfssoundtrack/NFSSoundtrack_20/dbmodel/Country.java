package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

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

    @Column(name = "countrylink")
    private String countryLink;

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
}
