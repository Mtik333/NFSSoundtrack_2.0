package com.nfssoundtrack.racingsoundtracks.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.sql.Date;

@Entity(name = "todayssong")
public class TodaysSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "songsubgroup_id")
    private SongSubgroup songSubgroup;

    /**
     * we need to control when today's song was 'drawn'
     */
    @Column(name = "todaydate")
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SongSubgroup getSongSubgroup() {
        return songSubgroup;
    }

    public void setSongSubgroup(SongSubgroup songSubgroup) {
        this.songSubgroup = songSubgroup;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
