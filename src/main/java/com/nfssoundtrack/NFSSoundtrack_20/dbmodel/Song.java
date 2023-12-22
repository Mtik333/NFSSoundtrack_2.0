package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity(name="song")
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

    @OneToMany(mappedBy = "song")
    private List<AuthorSong> authorSongList;
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
}
