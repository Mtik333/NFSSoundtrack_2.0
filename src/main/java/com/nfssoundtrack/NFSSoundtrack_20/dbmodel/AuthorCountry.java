package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="author_country")
public class AuthorCountry implements Serializable {

    @Id
    @Column(name="id",nullable=false)
    private Long id;

    @OneToOne(optional=false)
    @JoinColumn(name="author_id")
    private Author author;

    @OneToOne(optional=false)
    @JoinColumn(name="country_id")
    private Country country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
