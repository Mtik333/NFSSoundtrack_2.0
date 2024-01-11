package com.nfssoundtrack.NFSSoundtrack_20.dbmodel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "game")
@NamedEntityGraph(name = "Game.mainGroups", attributeNodes = @NamedAttributeNode(value = "mainGroups"))
public class Game implements Serializable, Comparable<Game> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	private Serie serie;

	@Column(name = "position")
	private Long position;

	@Column(name = "gametitle")
	private String gameTitle;

	@Column(name = "displaytitle")
	private String displayTitle;

	@Column(name = "gameshort")
	private String gameShort;

	@Column(name = "prefix")
	private String prefix;

	@Column(name = "spotify_id")
	private String spotifyId;

	@Column(name = "deezer_id")
	private String deezerId;

	@Column(name = "tidal_id")
	private String tidalId;

	@Column(name = "youtube_id")
	private String youtubeId;

	@Column(name = "soundcloud_id")
	private String soundcloudId;

	@Enumerated(EnumType.STRING)
	@Column(name = "game_status")
	private GameStatus gameStatus;

	@Column(name = "disqus_link")
	private String disqusLink;

	@JsonManagedReference
	@OneToMany(mappedBy = "game", fetch = FetchType.LAZY)
	private List<MainGroup> mainGroups = new ArrayList<>();

	public Game() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Serie getSerie() {
		return serie;
	}

	public void setSerie(Serie serie) {
		this.serie = serie;
	}

	public Long getPosition() {
		return position;
	}

	public void setPosition(Long position) {
		this.position = position;
	}

	public String getGameTitle() {
		return gameTitle;
	}

	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}

	public String getDisplayTitle() {
		return displayTitle;
	}

	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}

	public String getGameShort() {
		return gameShort;
	}

	public void setGameShort(String gameShort) {
		this.gameShort = gameShort;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public String getDeezerId() {
		return deezerId;
	}

	public void setDeezerId(String deezerId) {
		this.deezerId = deezerId;
	}

	public String getTidalId() {
		return tidalId;
	}

	public void setTidalId(String tidalId) {
		this.tidalId = tidalId;
	}

	public String getYoutubeId() {
		return youtubeId;
	}

	public void setYoutubeId(String youtubeId) {
		this.youtubeId = youtubeId;
	}

	public String getSoundcloudId() {
		return soundcloudId;
	}

	public void setSoundcloudId(String soundcloudId) {
		this.soundcloudId = soundcloudId;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	public List<MainGroup> getMainGroups() {
		return mainGroups;
	}

	public void setMainGroups(List<MainGroup> mainGroups) {
		this.mainGroups = mainGroups;
	}

	public String getDisqusLink() {
		return disqusLink;
	}

	public void setDisqusLink(String disqusLink) {
		this.disqusLink = disqusLink;
	}

	@Override
	public int compareTo(Game o) {
		int i = getPosition().compareTo(o.getPosition());
		if (i != 0) {
			return -i;
		}
		return i;
	}

	public Game(Long position, String gameTitle, String displayTitle, String gameShort, String prefix, String spotifyId, String deezerId, String tidalId, String youtubeId, String soundcloudId, GameStatus gameStatus) {
		this.position = position;
		this.gameTitle = gameTitle;
		this.displayTitle = displayTitle;
		this.gameShort = gameShort;
		this.prefix = prefix;
		this.spotifyId = spotifyId;
		this.deezerId = deezerId;
		this.tidalId = tidalId;
		this.youtubeId = youtubeId;
		this.soundcloudId = soundcloudId;
		this.gameStatus = gameStatus;
	}

	public Game(Game game) {
		this(game.position, game.gameTitle, game.displayTitle, game.gameShort, game.prefix, game.spotifyId,
				game.deezerId,
				game.tidalId, game.youtubeId, game.soundcloudId, GameStatus.RELEASED);
	}
}
