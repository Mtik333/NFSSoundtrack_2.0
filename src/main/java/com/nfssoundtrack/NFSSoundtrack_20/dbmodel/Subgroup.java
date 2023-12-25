package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity(name="subgroup")
public class Subgroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="subgroup_name")
    private String subgroupName;

    @JsonBackReference
    @OneToOne(optional=false)
    @JoinColumn(name="group_id")
    private MainGroup mainGroup;

    @JsonManagedReference
    @OneToMany(mappedBy = "subgroup")
    private List<SongSubgroup> songSubgroupList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubgroupName() {
        return subgroupName;
    }

    public void setSubgroupName(String subgroupName) {
        this.subgroupName = subgroupName;
    }

    public MainGroup getMainGroup() {
        return mainGroup;
    }

    public void setMainGroup(MainGroup mainGroup) {
        this.mainGroup = mainGroup;
    }

    public List<SongSubgroup> getSongSubgroupList() {
        return songSubgroupList;
    }

    public void setSongSubgroupList(List<SongSubgroup> songSubgroupList) {
        this.songSubgroupList = songSubgroupList;
    }
}
