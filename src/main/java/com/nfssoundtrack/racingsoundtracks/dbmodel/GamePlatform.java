package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "game_platform")
public class GamePlatform implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @JsonBackReference
    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "platform_id")
    private Platform platform;

    @Column(name = "retroachievements_url")
    private String retroachievementsUrl;

    @Column(name = "has_set", columnDefinition = "BIT")
    private Boolean hasSet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getRetroachievementsUrl() {
        return retroachievementsUrl;
    }

    public void setRetroachievementsUrl(String retroachievementsUrl) {
        this.retroachievementsUrl = retroachievementsUrl;
    }

    public Boolean getHasSet() {
        return hasSet;
    }

    public void setHasSet(Boolean hasSet) {
        this.hasSet = hasSet;
    }

    public GamePlatform() {
    }

    public GamePlatform(Platform platform, Game game) {
        this.platform = platform;
        this.game = game;
    }

    public GamePlatform(Platform platform, Game game, String retroachievementsUrl, Boolean hasSet) {
        this.platform = platform;
        this.game = game;
        this.retroachievementsUrl=retroachievementsUrl;
        this.hasSet=hasSet;
    }

    @Override
    public String toString() {
        return "GamePlatform{" +
                "id=" + id +
                ", game=" + game +
                ", platform=" + platform +
                '}';
    }
}
