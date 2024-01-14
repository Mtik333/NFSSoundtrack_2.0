package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "subgroup")
@NamedEntityGraph(name = "Subgroup.songSubgroupList", attributeNodes = @NamedAttributeNode("songSubgroupList"))
public class Subgroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subgroup_name")
    private String subgroupName;

    @Column(name = "position")
    private Integer position;

    @JsonBackReference
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private MainGroup mainGroup;

    @JsonManagedReference
    @OneToMany(mappedBy = "subgroup", fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<SongSubgroup> songSubgroupList = new ArrayList<>();

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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<SongSubgroup> getSongSubgroupList() {
        return songSubgroupList;
    }

    public void setSongSubgroupList(List<SongSubgroup> songSubgroupList) {
        this.songSubgroupList = songSubgroupList;
    }

    public Subgroup() {
    }

    public Subgroup(String subgroupName, Integer position, MainGroup mainGroup) {
        this.subgroupName = subgroupName;
        this.position = position;
        this.mainGroup = mainGroup;
    }

    public Subgroup(Subgroup subgroup) {
        this(subgroup.subgroupName, subgroup.position, subgroup.mainGroup);
    }
}
