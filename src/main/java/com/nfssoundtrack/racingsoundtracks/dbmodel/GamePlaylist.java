package com.nfssoundtrack.racingsoundtracks.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "game_playlist")
public class GamePlaylist implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private PlaylistPlatform platform;

    @Column(name = "playlist_url")
    private String playlistUrl;

    @Column(name = "label")
    private String label;

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

    public PlaylistPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(PlaylistPlatform platform) {
        this.platform = platform;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
