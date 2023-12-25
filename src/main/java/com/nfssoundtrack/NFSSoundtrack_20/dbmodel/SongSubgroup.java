package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="song_subgroup")
public class SongSubgroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    //@JsonBackReference
    @OneToOne(optional=false)
    @JoinColumn(name="song_id")
    private Song song;

    @JsonBackReference
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
    private String srcId;

    @Column(name="spotify_id")
    private String spotify_id;

    @Column(name="ingame_display_band")
    private String ingameDisplayBand;

    @Column(name="ingame_display_title")
    private String ingameDisplayTitle;

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

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getSpotify_id() {
        return spotify_id;
    }

    public void setSpotify_id(String spotify_id) {
        this.spotify_id = spotify_id;
    }

    public String getIngameDisplayBand() {
        return ingameDisplayBand;
    }

    public void setIngameDisplayBand(String ingameDisplayBand) {
        this.ingameDisplayBand = ingameDisplayBand;
    }

    public String getIngameDisplayTitle() {
        return ingameDisplayTitle;
    }

    public void setIngameDisplayTitle(String ingameDisplayTitle) {
        this.ingameDisplayTitle = ingameDisplayTitle;
    }
}
