package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "serie")
@NamedEntityGraph(name = "Serie.games", attributeNodes = @NamedAttributeNode("games"))
public class Serie implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "position")
    private Long position;

    @Column(name = "name")
    private String name;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "is_recently_updated")
    private Boolean hasRecentUpdates = false;

    @Column(name = "has_upcoming_games")
    private Boolean hasUpcomingGames = false;

    @JsonManagedReference
    @OneToMany(mappedBy = "serie", fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private Set<Game> games = new HashSet<>();

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

    // Getters and setters
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getHasRecentUpdates() {
        return hasRecentUpdates;
    }

    public void setHasRecentUpdates(Boolean hasRecentUpdates) {
        this.hasRecentUpdates = hasRecentUpdates;
    }

    public Boolean getHasUpcomingGames() {
        return hasUpcomingGames;
    }

    public void setHasUpcomingGames(Boolean hasUpcomingGames) {
        this.hasUpcomingGames = hasUpcomingGames;
    }

    public Serie() {
    }

    public Serie(Long position, String name) {
        this.position = position;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Serie{" +
                "id=" + id +
                ", position=" + position +
                ", name='" + name + '\'' +
                '}';
    }

    public String toChangeLogString() {
        return name + ", position: " + position + ", (id: " + id + ")";
    }
}
