package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

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

    @JsonManagedReference
    @OneToMany(mappedBy = "song", fetch = FetchType.LAZY)
    private List<AuthorSong> authorSongList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "song", fetch = FetchType.LAZY)
    private List<SongGenre> songGenreList = new ArrayList<>();

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

}
