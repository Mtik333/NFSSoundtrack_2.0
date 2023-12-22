package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity(name="serie")
public class Serie implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="position")
    private Long position;

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy="serie")
    private List<Game> games;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Game> getGames() {
        return games;
    }
}
