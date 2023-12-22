package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="content")
public class Content implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="method")
    private Long position;

    @Column(name="pos")
    private Long pos;

    @Column(name="visibility")
    private Long visibility;

    @Column(name="content_title")
    private String contentTitle;

    @Column(name="content_short")
    private String contentShort;

    @Column(name="content_data")
    private String contentData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public Long getPos() {
        return pos;
    }

    public void setPos(Long pos) {
        this.pos = pos;
    }

    public Long getVisibility() {
        return visibility;
    }

    public void setVisibility(Long visibility) {
        this.visibility = visibility;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentShort() {
        return contentShort;
    }

    public void setContentShort(String contentShort) {
        this.contentShort = contentShort;
    }

    public String getContentData() {
        return contentData;
    }

    public void setContentData(String contentData) {
        this.contentData = contentData;
    }
}
