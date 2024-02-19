package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Role;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.TodaysSong;
import com.nfssoundtrack.NFSSoundtrack_20.others.DiscoGSObj;
import com.nfssoundtrack.NFSSoundtrack_20.others.JustSomeHelper;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.SongSerializer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebsiteViewsController extends BaseControllerWithErrorHandling {

	private static final Logger logger = LoggerFactory.getLogger(WebsiteViewsController.class);

	@Autowired
	SongSerializer songSerializer;

	@Value("${spring.application.name}")
	String appName;

	@Value("${bot.token}")
	private String botSecret;

	@Value("${textchannel.id}")
	private String channelId;

	/**
	 * @return the main 'endpoint' of the website
	 */
	@GetMapping(value = "/")
	public String mainPage() {
		return "redirect:/content/home";
	}

	/**
	 * @param model view model
	 * @return endpoint of admin panel
	 */
	@GetMapping(value = "/manage/manage")
	public String manage(Model model) {
		model.addAttribute("appName", appName);
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		return "manage";
	}

	/**
	 * @param model view model
	 * @return endpoint of login
	 */
	@GetMapping(value = "/login")
	public String login(Model model) {
		model.addAttribute("appName", appName);
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		model.addAttribute("login", true);
		return "login";
	}

	/**
	 * @param model view model
	 * @param value content_short from 'content' table
	 * @return html for content_short entry
	 */
	@GetMapping(value = "/content/{value}")
	public String topMenuEntry(Model model, @PathVariable("value") String value) throws ResourceNotFoundException {
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		model.addAttribute("htmlToInject",
				contentService.findByContentShort(value).getContentData());
		boolean isHome = value.contains("home");
		model.addAttribute("home", isHome);
		model.addAttribute("todayssong", todaysSongService.getTodaysSong());
		model.addAttribute("appName", value + " - " + appName);
		return "index";
	}

	/**
	 * @param model     view model
	 * @param gameshort short name from 'game' table
	 * @return whole display of game's soundtrack
	 */
	@GetMapping(value = "/game/{gameshort}")
	public String game(Model model, @PathVariable("gameshort") String gameshort) {
		Game game = gameService.findByGameShort(gameshort);
		model.addAttribute("endpoint", "/game/" + gameshort);
		model.addAttribute("game", game);
		model.addAttribute("appName", game.getDisplayTitle() + " soundtrack at " + appName);
		model.addAttribute("gamegroups", game.getMainGroups());
		model.addAttribute("songSubgroups", songSubgroupService.fetchFromGame(game));
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		return "index";
	}

	/**
	 * @param model          view model
	 * @param customPlaylist array of song ids, example ["350","357","312","315","410","425","528"]
	 * @return display of custom playlist songs table
	 * @throws ResourceNotFoundException exception when song subgroup not found
	 * @throws JsonProcessingException   exception due to objectmapper
	 */
	@PostMapping(value = "/custom/playlist")
	public String getCustomPlaylist(Model model, @RequestBody String customPlaylist)
			throws ResourceNotFoundException, JsonProcessingException {
		List<SongSubgroup> songSubgroupList = new ArrayList<>();
		if (customPlaylist == null || customPlaylist.isEmpty()) {
			logger.error("do something");
		} else {
			String result = URLDecoder.decode(customPlaylist, StandardCharsets.UTF_8);
			String basicallyArray = result.replace("customPlaylist=", "");
			List<String> finalList = new ObjectMapper().readValue(basicallyArray,
					TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
			for (String songSubgroupId : finalList) {
				songSubgroupList.add(songSubgroupService.findById(Integer.valueOf(
						songSubgroupId)).orElseThrow(() -> new ResourceNotFoundException("No songsubgroup found with " +
						"id " + songSubgroupId)));
			}
		}
		model.addAttribute("appName", "Custom playlist - " + appName);
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		model.addAttribute("customPlaylist", songSubgroupList);
		return "index";
	}

	/**
	 * @param songId id of song to fetch info about
	 * @return whole information about the song
	 * @throws JsonProcessingException   exception due to objectmapper
	 * @throws ResourceNotFoundException when song not found by id
	 */
	@GetMapping(value = "/songInfo/{songId}")
	public @ResponseBody
	String provideSongModalInfo(@PathVariable("songId") int songId)
			throws JsonProcessingException, ResourceNotFoundException {
		Song song = songService.findById(songId).orElseThrow(
				() -> new ResourceNotFoundException("No song with id found " + songId));
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Song.class, songSerializer);
		objectMapper.registerModule(simpleModule);
		String result = objectMapper.writeValueAsString(song);
		if (logger.isDebugEnabled()) {
			logger.debug("result " + result);
		}
		return result;
	}

	/**
	 * @param model  view model
	 * @param songId id of song to fetch usages of it
	 * @return table with usages of the song across all games
	 * @throws ResourceNotFoundException exception when song is not found
	 */
	@GetMapping(value = "/song/{songId}")
	public String provideSongInfo(Model model, @PathVariable("songId") String songId) throws ResourceNotFoundException {
		Song song = songService.findById(Integer.valueOf(songId)).orElseThrow(
				() -> new ResourceNotFoundException("No song found with id " + songId));
		model.addAttribute("appName", song.getOfficialDisplayBand()
				+ " - " + song.getOfficialDisplayTitle() + " - " + appName);
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		model.addAttribute("songToCheck", song);
		model.addAttribute("songUsages", songSubgroupService.findBySong(song));
		return "index";
	}

	/**
	 * @param model    view model
	 * @param authorId id of author to fetch info about
	 * @return table with all songs where author is being part of
	 * @throws ResourceNotFoundException author not found by id
	 * @throws JsonProcessingException   exception due to objectmapper
	 */
	@GetMapping(value = "/author/{authorId}")
	public String readAuthor(Model model, @PathVariable("authorId") int authorId)
			throws ResourceNotFoundException, JsonProcessingException {
		Author author =
				authorService.findById(authorId).orElseThrow(
						() -> new ResourceNotFoundException("No author found with id " + authorId));
		DiscoGSObj discoGSObj = authorService.fetchInfoFromMap(author);
		List<AuthorAlias> allAliases = authorAliasService.findByAuthor(author);
//        authorSongRepository.findByAuthorAlias(allAliases.get(0));
		Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsComposer = new HashMap<>();
		Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsSubcomposer = new HashMap<>();
		Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsFeat = new HashMap<>();
		Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsRemixed = new HashMap<>();
		for (AuthorAlias authorAlias : allAliases) {
			List<AuthorSong> allAuthorSongs = authorSongService.findByAuthorAlias(authorAlias);
			for (AuthorSong authorSong : allAuthorSongs) {
				List<SongSubgroup> songSubgroupList =
						songSubgroupService.findBySong(authorSong.getSong());
				JustSomeHelper.fillMapForArtistDisplay(authorAlias, authorSong, Role.COMPOSER, songsAsComposer,
						songSubgroupList);
				JustSomeHelper.fillMapForArtistDisplay(authorAlias, authorSong, Role.SUBCOMPOSER, songsAsSubcomposer,
						songSubgroupList);
				JustSomeHelper.fillMapForArtistDisplay(authorAlias, authorSong, Role.FEAT, songsAsFeat,
						songSubgroupList);
				JustSomeHelper.fillMapForArtistDisplay(authorAlias, authorSong, Role.REMIX, songsRemixed,
						songSubgroupList);
			}
		}
		model.addAttribute("discoGSObj", discoGSObj);
		model.addAttribute("author", author);
		model.addAttribute("songsAsComposer", songsAsComposer);
		model.addAttribute("songsAsSubcomposer", songsAsSubcomposer);
		model.addAttribute("songsAsFeat", songsAsFeat);
		model.addAttribute("songsRemixed", songsRemixed);
		model.addAttribute("allAliases", allAliases);
		model.addAttribute("appName", author.getName() + " - " + appName);
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		return "index";
	}

	/**
	 * @param model   view model
	 * @param genreId id of genre
	 * @return table with songs associated with genre (limited to 50 entries)
	 * @throws ResourceNotFoundException when genre with this id not found
	 */
	@GetMapping(value = "/genre/{genreId}")
	public String readGenreInfo(Model model, @PathVariable("genreId") int genreId) throws ResourceNotFoundException {
		Genre genre =
				genreService.findById(genreId).orElseThrow(
						() -> new ResourceNotFoundException("No genre found with id " + genreId));
		List<SongGenre> songGenreList = songGenreService.findByGenre(genre, 50);
		List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
		List<SongSubgroup> songSubgroupList = songSubgroupService.findBySongInSortedByIdAsc(songs);
		model.addAttribute("songSubgroupList", songSubgroupList);
		model.addAttribute("genre", genre);
		model.addAttribute("readFull", true);
		model.addAttribute("appName", genre.getGenreName() + " - " + appName);
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		return "index";
	}

	/**
	 * @param model   view model
	 * @param genreId id of genre
	 * @return table with songs associated with genre
	 * @throws ResourceNotFoundException when genre with this id not found
	 */
	@GetMapping(value = "/genre/readfull/{genreId}")
	public String readGenreInfoFull(Model model, @PathVariable("genreId") int genreId)
			throws ResourceNotFoundException {
		Genre genre =
				genreService.findById(genreId).orElseThrow(
						() -> new ResourceNotFoundException("No genre found with id " + genreId));
		List<SongGenre> songGenreList = songGenreService.findByGenre(genre);
		List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
		List<SongSubgroup> songSubgroupList = songSubgroupService.findBySongInSortedByIdAsc(songs);
		model.addAttribute("songSubgroupList", songSubgroupList);
		model.addAttribute("genre", genre);
		model.addAttribute("appName", appName);
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		return "index";
	}

	@GetMapping(value = "/songhistory")
	public String getTodaysSongHistory(Model model) {
		List<TodaysSong> todays30Songs = todaysSongService.findAllFromLast30Days();
		model.addAttribute("todays30Songs", todays30Songs);
		model.addAttribute("appName", appName);
		model.addAttribute("series", serieService.findAllSortedByPositionAsc());
		return "index";
	}

	@PostMapping(value = "/correction", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String submitCorrection(@RequestBody String formData)
			throws JsonProcessingException {
		Map<?, ?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
		int songSubgroupId = (int) objectMapper.get("affectedSongsubgroup");
		String problemType = (String) objectMapper.get("problemType");
		String correctValue = (String) objectMapper.get("rightValue");
		String discordId = (String) objectMapper.get("discordId");
		try {
			JDA jda = JDABuilder.createDefault(botSecret).build();
			jda.awaitReady();
			TextChannel textChannel = jda.getTextChannelById(channelId);
			if (textChannel!=null && textChannel.canTalk()) {
				textChannel.sendMessage("Correction: " + songSubgroupId + ", " + problemType + ", " + correctValue
						+ ", " + discordId).queue();
			}
			if (discordId != null && !discordId.isEmpty()) {
				EmbedBuilder eb = new EmbedBuilder();
				eb.setTitle("Correction", "https://racingsoundtracks.com/content/home");
				eb.setColor(Color.red);
				eb.setDescription("Submitted correction for: " + songSubgroupId);
				eb.addField("Problem type", problemType, false);
				eb.addBlankField(false);
				eb.addField("Correct value", correctValue, false);
				eb.addBlankField(false);
				eb.setAuthor("name", null,
						"https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/zekroBot_Logo_-_round_small.png");
				eb.setFooter("Text",
						"https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/zekroBot_Logo_-_round_small.png");
				eb.setImage("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png");
				eb.setThumbnail("https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/logo%20-%20title.png");
				jda.openPrivateChannelById(discordId).queue(privateChannel -> privateChannel
						.sendMessageEmbeds(eb.build()).queue());
			}

		} catch (LoginException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		return new ObjectMapper().writeValueAsString("OK");
	}
}
