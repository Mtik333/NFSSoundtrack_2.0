package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="song_subgroup")
public class SongSubgroup implements Serializable {

    @Id
    @Column(name="id")
    private Long id;

    @OneToOne(optional=false)
    @JoinColumn(name="song_id")
    private Song song;

    @OneToOne(optional=false)
    @JoinColumn(name="subgroup_id")
    private Subgroup subgroup;

    @Enumerated(EnumType.STRING)
    @Column(name="instrumental")
    private Instrumental instrumental;

    @Enumerated(EnumType.STRING)
    @Column(name="remix")
    private Remix remix;

    @Column(name="src_id")
    private String src_id;

    @Column(name="spotify_id")
    private String spotify_id;

    @Column(name="ingame_display_band")
    private String ingame_display_band;

    @Column(name="ingame_display_title")
    private String ingame_display_title;

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

    public Subgroup getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(Subgroup subgroup) {
        this.subgroup = subgroup;
    }

    public Instrumental getInstrumental() {
        return instrumental;
    }

    public void setInstrumental(Instrumental instrumental) {
        this.instrumental = instrumental;
    }

    public Remix getRemix() {
        return remix;
    }

    public void setRemix(Remix remix) {
        this.remix = remix;
    }

    public String getSrc_id() {
        return src_id;
    }

    public void setSrc_id(String src_id) {
        this.src_id = src_id;
    }

    public String getSpotify_id() {
        return spotify_id;
    }

    public void setSpotify_id(String spotify_id) {
        this.spotify_id = spotify_id;
    }

    public String getIngame_display_band() {
        return ingame_display_band;
    }

    public void setIngame_display_band(String ingame_display_band) {
        this.ingame_display_band = ingame_display_band;
    }

    public String getIngame_display_title() {
        return ingame_display_title;
    }

    public void setIngame_display_title(String ingame_display_title) {
        this.ingame_display_title = ingame_display_title;
    }
}
