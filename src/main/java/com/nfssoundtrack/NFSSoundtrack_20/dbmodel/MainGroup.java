package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name="maingroup")
@NamedEntityGraph(name = "MainGroup.subgroups", attributeNodes = @NamedAttributeNode("subgroups"))
public class MainGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="groupname")
    private String groupName;

    @JsonBackReference
    @OneToOne(optional=false,fetch = FetchType.LAZY)
    @JoinColumn(name="game_id")
    private Game game;

    @JsonManagedReference
    @OrderBy("position ASC")
    @OneToMany(mappedBy = "mainGroup",fetch = FetchType.LAZY)
    private List<Subgroup> subgroups=new ArrayList<>();
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
