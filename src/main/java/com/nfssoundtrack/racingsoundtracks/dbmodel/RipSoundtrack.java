package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "rip_soundtrack")
public class RipSoundtrack implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonBackReference
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_platform_id")
    private GamePlatform gamePlatform;

    @Column(name = "link")
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(name = "rip_source")
    private RipSource ripSource;

    @Column(name = "alt_title")
    private String altTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GamePlatform getGamePlatform() {
        return gamePlatform;
    }

    public void setGamePlatform(GamePlatform gamePlatform) {
        this.gamePlatform = gamePlatform;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public RipSource getRipSource() {
        return ripSource;
    }

    public void setRipSource(RipSource ripSource) {
        this.ripSource = ripSource;
    }

    public String getAltTitle() {
        return altTitle;
    }

    public void setAltTitle(String altTitle) {
        this.altTitle = altTitle;
    }

    @Override
    public String toString() {
        return "RipSoundtrack{" +
                "id=" + id +
                ", gamePlatform=" + gamePlatform +
                ", link='" + link + '\'' +
                ", gameStatus=" + ripSource +
                ", alt_title='" + altTitle + '\'' +
                '}';
    }
}
