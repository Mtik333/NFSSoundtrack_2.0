package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "maingroup")
@NamedEntityGraph(name = "MainGroup.subgroups", attributeNodes = @NamedAttributeNode("subgroups"))
public class MainGroup implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "groupname")
	private String groupName;

	@JsonBackReference
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@JsonManagedReference
	@OrderBy("position ASC")
	@OneToMany(mappedBy = "mainGroup", fetch = FetchType.LAZY)
	private List<Subgroup> subgroups = new ArrayList<>();

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

	public MainGroup() {

	}

	public MainGroup(String groupName, Game game) {
		this.groupName = groupName;
		this.game = game;
	}

	public MainGroup(MainGroup mainGroup) {
		this(mainGroup.groupName, mainGroup.game);
	}
}
