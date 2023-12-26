package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="song_genre")
public class SongGenre implements Serializable{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="id",nullable=false)
        private Long id;

        @JsonBackReference
        @OneToOne(optional=false)
        @JoinColumn(name="song_id")
        private Song song;

        @JsonBackReference
        @OneToOne(optional=false)
        @JoinColumn(name="genre_id")
        private Genre genre;

    }
