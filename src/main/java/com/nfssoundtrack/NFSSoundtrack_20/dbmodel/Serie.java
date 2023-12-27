package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name="serie")
@NamedEntityGraph(name = "Serie.games", attributeNodes = @NamedAttributeNode("games"))
public class Serie implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="position")
    private Long position;

    @Column(name="name")
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "serie",fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private Set<Game> games= new HashSet<>();
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

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }
}
