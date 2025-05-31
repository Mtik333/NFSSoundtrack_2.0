package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity(name = "author_member")
public class AuthorMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonBackReference
    @OneToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Author member;

    @JsonBackReference
    @OneToOne(optional = false)
    @JoinColumn(name = "author_id")
    private Author author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Author getMember() {
        return member;
    }

    public void setMember(Author member) {
        this.member = member;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public AuthorMember() {
    }

    public AuthorMember(Author member, Author author) {
        this.member = member;
        this.author = author;
    }

    @Override
    public String toString() {
        return "AuthorMember{" +
                "id=" + id +
                ", member=" + member +
                ", author=" + author +
                '}';
    }

    public String toChangeLogString() {
        return "member: " + member.toChangeLogString() + " of author: " + author.toChangeLogString();
    }
}
