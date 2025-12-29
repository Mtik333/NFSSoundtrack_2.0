package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "song")
@NamedEntityGraph(name = "Song.authorSongList", attributeNodes = @NamedAttributeNode("authorSongList"))
public class Song implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "officialdisplayband")
    private String officialDisplayBand;

    @Column(name = "officialdisplaytitle")
    private String officialDisplayTitle;

    @Column(name = "src_id")
    private String srcId;

    @Enumerated(EnumType.STRING)
    @Column(name = "multi_concat")
    private MultiConcat multiConcat;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "lyrics")
    private String lyrics;

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

    @JsonManagedReference
    @OneToMany(mappedBy = "song", fetch = FetchType.LAZY)
    @OrderBy("role ASC")
    private List<AuthorSong> authorSongList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "song")
    private List<SongGenre> songGenreList = new ArrayList<>();

    /**
     * possibly used to link remixes to 'normal' version of song
     */
    //@JsonBackReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "basesong_id")
    private Song baseSong;

    /**
     * sometimes we need to display feat author next to main author name
     */
    @Column(name = "feat_next_to_band", columnDefinition = "BIT")
    private Boolean featNextToBand;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOfficialDisplayBand() {
        return officialDisplayBand;
    }

    public void setOfficialDisplayBand(String officialDisplayBand) {
        this.officialDisplayBand = officialDisplayBand;
    }

    public String getOfficialDisplayTitle() {
        return officialDisplayTitle;
    }

    public void setOfficialDisplayTitle(String officialDisplayTitle) {
        this.officialDisplayTitle = officialDisplayTitle;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public MultiConcat getMultiConcat() {
        return multiConcat;
    }

    public void setMultiConcat(MultiConcat multiConcat) {
        this.multiConcat = multiConcat;
    }

    public List<AuthorSong> getAuthorSongList() {
        return authorSongList;
    }

    public void setAuthorSongList(List<AuthorSong> authorSongList) {
        this.authorSongList = authorSongList;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public List<SongGenre> getSongGenreList() {
        return songGenreList;
    }

    public void setSongGenreList(List<SongGenre> songGenreList) {
        this.songGenreList = songGenreList;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
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

    public Song getBaseSong() {
        return baseSong;
    }

    public void setBaseSong(Song baseSong) {
        this.baseSong = baseSong;
    }

    public Boolean getFeatNextToBand() {
        return featNextToBand;
    }

    public void setFeatNextToBand(Boolean featNextToBand) {
        this.featNextToBand = featNextToBand;
    }

    public Song() {
    }

    public Song(String officialDisplayBand, String officialDisplayTitle, String srcId, String lyrics) {
        this.officialDisplayBand = officialDisplayBand;
        this.officialDisplayTitle = officialDisplayTitle;
        this.srcId = srcId;
        this.lyrics = lyrics;
    }

    public void setLinks(String spotifyId, String deezerId, String itunesLink, String tidalLink, String soundcloudLink) {
        this.spotifyId = spotifyId;
        this.deezerId = deezerId;
        this.itunesLink = itunesLink;
        this.tidalLink = tidalLink;
        this.soundcloudLink = soundcloudLink;
    }

    public void setLinks(SongSubgroup songSubgroup) {
        this.spotifyId = songSubgroup.getSpotifyId();
        this.deezerId = songSubgroup.getDeezerId();
        this.itunesLink = songSubgroup.getItunesLink();
        this.tidalLink = songSubgroup.getTidalLink();
        this.soundcloudLink = songSubgroup.getSoundcloudLink();
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", officialDisplayBand='" + officialDisplayBand + '\'' +
                ", officialDisplayTitle='" + officialDisplayTitle + '\'' +
                ", srcId='" + srcId + '\'' +
                ", spotifyId='" + spotifyId + '\'' +
                ", deezerId='" + deezerId + '\'' +
                ", itunesLink='" + itunesLink + '\'' +
                ", tidalLink='" + tidalLink + '\'' +
                ", soundcloudLink='" + soundcloudLink + '\'' +
                '}';
    }

    public String toAnotherChangeLogString() {
        return officialDisplayBand + " - " + officialDisplayTitle;
    }

    //TODO use it in some cases for lyrics?
    public String toTitleWithoutFeat(){
        return officialDisplayTitle.substring(officialDisplayTitle.indexOf("feat."), officialDisplayTitle.lastIndexOf(")"));
    }

    public String toChangeLogString() {
        return officialDisplayBand + " - " + officialDisplayTitle + ", srcId: " + srcId + ", (id: " + id + ")";
    }
}
