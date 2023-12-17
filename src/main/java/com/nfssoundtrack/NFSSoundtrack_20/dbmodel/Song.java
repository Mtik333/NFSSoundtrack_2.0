package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="song")
public class Song implements Serializable {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="officialdisplayband")
    private String officialDisplayBand;

    @Column(name="officialdisplaytitle")
    private String officialDisplayTitle;

    @Column(name="src_id")
    private String src_id;

    @Column(name="info")
    private String info;

    @Enumerated(EnumType.STRING)
    @Column(name="multi_concat")
    private MultiConcat multiConcat;

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

    public String getSrc_id() {
        return src_id;
    }

    public void setSrc_id(String src_id) {
        this.src_id = src_id;
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
}
