package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name="song")
@NamedEntityGraph(name = "Song.authorSongList", attributeNodes = @NamedAttributeNode("authorSongList"))
public class Song implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="officialdisplayband")
    private String officialDisplayBand;

    @Column(name="officialdisplaytitle")
    private String officialDisplayTitle;

    @Column(name="src_id")
    private String srcId;

    @Column(name="info")
    private String info;

    @Enumerated(EnumType.STRING)
    @Column(name="multi_concat")
    private MultiConcat multiConcat;

    @Column(name="lyrics")
    private String lyrics;

    @JsonManagedReference
    @OneToMany(mappedBy = "song",fetch = FetchType.LAZY)
    private List<AuthorSong> authorSongList=new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "song",fetch = FetchType.LAZY)
    private List<SongGenre> songGenreList=new ArrayList<>();

    @Transient
    private Set<Country> countrySet = new HashSet<>();
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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

    public Set<Country> getCountrySet() {
        Set<Country> filteredCountries = new HashSet<>();
        for (AuthorSong authorSong : getAuthorSongList()){
            for (AuthorCountry authorCountry : authorSong.getAuthorAlias().getAuthor().getAuthorCountries()){
                filteredCountries.add(authorCountry.getCountry());
            }
        }
        return countrySet;
    }

    public void setCountrySet(Set<Country> countrySet) {
        this.countrySet = countrySet;
    }
}
