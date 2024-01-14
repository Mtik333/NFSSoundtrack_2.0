package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "song_subgroup")
public class SongSubgroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //@JsonBackReference
    @OneToOne(optional = false)
    @JoinColumn(name = "song_id")
    private Song song;

    @JsonBackReference
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "subgroup_id")
    private Subgroup subgroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrumental")
    private Instrumental instrumental;

    @Enumerated(EnumType.STRING)
    @Column(name = "remix")
    private Remix remix;

    @Column(name = "src_id")
    private String srcId;

    @Column(name = "spotify_id")
    private String spotifyId;

    @Column(name = "deezer_id")
    private String deezerId;

    @Column(name = "itunes_link")
    private String itunesLink;

    @Column(name = "tidal_link")
    private String tidalLink;

    @Column(name = "soundcloud_link")
    private String soundcloudLink;

    @Column(name = "ingame_display_band")
    private String ingameDisplayBand;

    @Column(name = "ingame_display_title")
    private String ingameDisplayTitle;

    @Column(name = "position")
    private Long position;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "lyrics")
    private String lyrics;

    @Column(name = "info")
    private String info;

    @Column(name = "feat_title")
    private Long featTitle;

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

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
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

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getDeezerId() {
        return deezerId;
    }

    public void setDeezerId(String deezerId) {
        this.deezerId = deezerId;
    }

    public String getItunesLink() {
        return itunesLink;
    }

    public void setItunesLink(String itunesLink) {
        this.itunesLink = itunesLink;
    }

    public String getTidalLink() {
        return tidalLink;
    }

    public void setTidalLink(String tidalLink) {
        this.tidalLink = tidalLink;
    }

    public String getSoundcloudLink() {
        return soundcloudLink;
    }

    public void setSoundcloudLink(String soundcloudLink) {
        this.soundcloudLink = soundcloudLink;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getFeatTitle() {
        return featTitle;
    }

    public void setFeatTitle(Long featTitle) {
        this.featTitle = featTitle;
    }

    public SongSubgroup(Instrumental instrumental, Remix remix, String srcId, String spotifyId, String deezerId, String itunesLink, String tidalLink, String soundcloudLink, String ingameDisplayBand, String ingameDisplayTitle, Long position, String lyrics, String info) {
        this.instrumental = instrumental;
        this.remix = remix;
        this.srcId = srcId;
        this.spotifyId = spotifyId;
        this.deezerId = deezerId;
        this.itunesLink = itunesLink;
        this.tidalLink = tidalLink;
        this.soundcloudLink = soundcloudLink;
        this.ingameDisplayBand = ingameDisplayBand;
        this.ingameDisplayTitle = ingameDisplayTitle;
        this.position = position;
        this.lyrics = lyrics;
        this.info = info;
    }

    public SongSubgroup() {
    }

    public SongSubgroup(SongSubgroup songSubgroup) {
        this(songSubgroup.instrumental, songSubgroup.remix, songSubgroup.srcId, songSubgroup.spotifyId,
                songSubgroup.deezerId, songSubgroup.itunesLink, songSubgroup.tidalLink, songSubgroup.soundcloudLink,
                songSubgroup.ingameDisplayBand, songSubgroup.ingameDisplayTitle, songSubgroup.position,
                songSubgroup.lyrics, songSubgroup.info);
    }

}
