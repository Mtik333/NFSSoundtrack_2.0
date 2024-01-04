package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "song_genre")
public class SongGenre implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonBackReference
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    private Song song;

    @JsonBackReference
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
}
