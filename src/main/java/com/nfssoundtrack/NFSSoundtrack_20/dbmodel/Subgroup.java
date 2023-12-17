package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name="subgroup")
public class Subgroup implements Serializable {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="subgroup_name")
    private String subgroup_name;

    @OneToOne(optional=false)
    @JoinColumn(name="group_id")
    private MainGroup group;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubgroup_name() {
        return subgroup_name;
    }

    public void setSubgroup_name(String subgroup_name) {
        this.subgroup_name = subgroup_name;
    }

    public MainGroup getGroup() {
        return group;
    }

    public void setGroup(MainGroup group) {
        this.group = group;
    }
}
