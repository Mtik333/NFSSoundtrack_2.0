package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "author_alias")
public class AuthorAlias implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonManagedReference
    @OneToOne(optional = false)
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(name = "alias")
    private String alias;

    public AuthorAlias() {
    }

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public AuthorAlias(Author author, String alias) {
        this.author = author;
        this.alias = alias;
    }

    public AuthorAlias(AuthorAlias authorAlias) {
        this(authorAlias.author, authorAlias.alias);
    }

    @Override
    public String toString() {
        return "AuthorAlias{" +
                "id=" + id +
                ", author=" + author +
                ", alias='" + alias + '\'' +
                '}';
    }

    public String toChangeLogString() {
        return "alias: " + alias + " of author: " + author.toChangeLogString();
    }
}
