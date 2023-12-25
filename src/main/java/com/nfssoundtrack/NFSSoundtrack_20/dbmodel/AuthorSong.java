package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;


@Entity(name="author_song")
public class AuthorSong implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @JsonBackReference
    @OneToOne(optional=false)
    @JoinColumn(name="alias_id")
    private AuthorAlias authorAlias;

    @JsonBackReference
    @OneToOne(optional=false)
    @JoinColumn(name="song_id")
    private Song song;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthorAlias getAuthorAlias() {
        return authorAlias;
    }

    public void setAuthorAlias(AuthorAlias authorAlias) {
        this.authorAlias = authorAlias;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
