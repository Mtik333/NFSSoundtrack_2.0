package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity(name="maingroup")
public class MainGroup implements Serializable {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="groupname")
    private String groupName;

    @OneToOne(optional=false)
    @JoinColumn(name="game_id", referencedColumnName = "id")
    private Game game;

    @OneToMany(mappedBy="mainGroup")
    private List<Subgroup> subgroups;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Subgroup> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(List<Subgroup> subgroups) {
        this.subgroups = subgroups;
    }
}
