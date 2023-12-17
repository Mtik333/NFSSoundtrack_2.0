package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="game")
public class Game implements Serializable {

    @Id
    @Column(name="id")
    private Long id;

    @OneToOne(optional=false)
    @JoinColumn(name="series_id")
    private Serie serie;

    @Column(name="position")
    private Long position;

    @Column(name="gametitle")
    private String gameTitle;

    @Column(name="displaytitle")
    private String displayTitle;

    @Column(name="gameshort")
    private String gameShort;

    @Column(name="prefix")
    private String prefix;

    @Column(name="spotify_id")
    private String spotify_id;

    @Column(name="deezer_id")
    private String deezer_id;

    @Column(name="tidal_id")
    private String tidal_id;

    @Column(name="youtube_id")
    private String youtube_id;

    @Column(name="soundcloud_id")
    private String soundcloud_id;

    @Enumerated(EnumType.STRING)
    @Column(name="game_status")
    private GameStatus gameStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
        this.displayTitle = displayTitle;
    }

    public String getGameShort() {
        return gameShort;
    }

    public void setGameShort(String gameShort) {
        this.gameShort = gameShort;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSpotify_id() {
        return spotify_id;
    }

    public void setSpotify_id(String spotify_id) {
        this.spotify_id = spotify_id;
    }

    public String getDeezer_id() {
        return deezer_id;
    }

    public void setDeezer_id(String deezer_id) {
        this.deezer_id = deezer_id;
    }

    public String getTidal_id() {
        return tidal_id;
    }

    public void setTidal_id(String tidal_id) {
        this.tidal_id = tidal_id;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getSoundcloud_id() {
        return soundcloud_id;
    }

    public void setSoundcloud_id(String soundcloud_id) {
        this.soundcloud_id = soundcloud_id;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}
