package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.io.Serializable;


@Entity(name = "author_song")
public class AuthorSong implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@JsonManagedReference
	@OneToOne(optional = false)
	@JoinColumn(name = "alias_id")
	private AuthorAlias authorAlias;

	@JsonBackReference
	@OneToOne(optional = false)
	@JoinColumn(name = "song_id")
	private Song song;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private Role role;

	@Column(name = "remix_concat")
	private String remixConcat;

	@Column(name = "feat_concat")
	private String featConcat;

	@Column(name = "subcomposer_concat")
	private String subcomposerConcat;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AuthorAlias getAuthorAlias() {
		return authorAlias;
	}

	public void setAuthorAlias(AuthorAlias authorAlias) {
		this.authorAlias = authorAlias;
	}

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getRemixConcat() {
		return remixConcat;
	}

	public void setRemixConcat(String remixConcat) {
		this.remixConcat = remixConcat;
	}

	public String getFeatConcat() {
		return featConcat;
	}

	public void setFeatConcat(String featConcat) {
		this.featConcat = featConcat;
	}

	public String getSubcomposerConcat() {
		return subcomposerConcat;
	}

	public void setSubcomposerConcat(String subcomposerConcat) {
		this.subcomposerConcat = subcomposerConcat;
	}

	public AuthorSong() {
	}

	public AuthorSong(AuthorAlias authorAlias, Song song, Role role) {
		this.authorAlias = authorAlias;
		this.song = song;
		this.role = role;
	}
}
