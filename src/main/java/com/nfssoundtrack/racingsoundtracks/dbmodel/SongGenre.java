package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "song_genre")
public class SongGenre implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonBackReference
    @OneToOne(optional = false)
    @JoinColumn(name = "song_id")
    private Song song;

    @JsonManagedReference
    @OneToOne(optional = false)
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

    public SongGenre() {
    }

    public SongGenre(Song song, Genre genre) {
        this.song = song;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "SongGenre{" +
                "id=" + id +
                ", song=" + song +
                ", genre=" + genre +
                '}';
    }

    public String toChangeLogString() {
        return "genre: " + genre.toChangeLogString() + " of song: " + song.toChangeLogString();
    }
}
