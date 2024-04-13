package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.DiscoGSObj;
import com.nfssoundtrack.NFSSoundtrack_20.others.JustSomeHelper;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.SongSerializer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

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

    public static JDA JDA;

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
    public String topMenuEntry(Model model, @PathVariable("value") String value) throws ResourceNotFoundException, LoginException, InterruptedException {
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
        model.addAttribute("appName", game.getDisplayTitle() +
                " " + getLocalizedMessage("soundtrackAt") + " " + appName);
        model.addAttribute("gamegroups", game.getMainGroups());
        Optional<CustomTheme> customTheme = customThemeService.findByGame(game);
        customTheme.ifPresent(theme -> model.addAttribute("customTheme", theme));
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
            throws ResourceNotFoundException, JsonProcessingException, LoginException, InterruptedException {
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
        model.addAttribute("appName", "Full list of genre " + genre.getGenreName() + " - " + appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        return "index";
    }

    @GetMapping(value = "/songhistory")
    public String getTodaysSongHistory(Model model) {
        List<TodaysSong> todays30Songs = todaysSongService.findAllFromLast30Days();
        model.addAttribute("todays30Songs", todays30Songs);
        model.addAttribute("appName", "Archive of today's songs - " + appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        return "index";
    }

    @PostMapping(value = "/correction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String submitCorrection(@RequestBody String formData)
            throws JsonProcessingException {
        Map<?, ?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
        int songSubgroupId = (int) objectMapper.get("affectedSongsubgroup");
        String pageUrl = (String) objectMapper.get("sourceUrl");
        String problemType = (String) objectMapper.get("problemType");
        String correctValue = (String) objectMapper.get("rightValue");
        String discordUserName = (String) objectMapper.get("discordUsername");
        try {
            Optional<SongSubgroup> songSubgroup;
            Correction correction;
            if (songSubgroupId != -1) {
                songSubgroup = songSubgroupService.findById(songSubgroupId);
                correction = new Correction(songSubgroup.get(), pageUrl, ProblemType.valueOf(problemType), correctValue, discordUserName);
            } else {
                songSubgroup = Optional.empty();
                correction = new Correction(null, pageUrl, ProblemType.valueOf(problemType), correctValue, discordUserName);
            }
            correction.setCorrectionStatus(CorrectionStatus.PENDING);
            correction = correctionService.save(correction);
            if (JDA == null) {
                JDA = JDABuilder.createDefault(botSecret).build();
                JDA.awaitReady();
            }
            List<Member> foundUsers;
            if (!discordUserName.isEmpty()) {
                discordUserName = discordUserName.replace("@", "");
                foundUsers = JDA.getGuilds().get(0).retrieveMembersByPrefix(discordUserName, 1).get();
            } else {
                foundUsers = new ArrayList<>();
            }
            TextChannel textChannel = JDA.getTextChannelById(channelId);
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Correction", pageUrl);
            eb.setThumbnail("https://racingsoundtracks.com/images/racingsoundtracks.png");
            eb.setColor(Color.red);
            if (songSubgroup.isPresent()) {
                eb.setDescription("Submitted correction for:\n" + songSubgroup.get().toCorrectionString());
            } else {
                eb.setDescription("Submitted general correction on:\n" + pageUrl);
            }
            eb.addField("Problem Type", problemType, false);
            eb.addField("Correct Value", correctValue, false);
            if (!foundUsers.isEmpty()) {
                eb.addField("Reported by", foundUsers.get(0).getEffectiveName(), false);
            } else {
                eb.addField("Reported by un-identified user: ", discordUserName, false);
            }
            eb.setFooter("Correction ID: " + correction.getId());
            MessageEmbed embed = eb.build();
            if (textChannel != null && textChannel.canTalk()) {
                textChannel.sendMessageEmbeds(embed).queue();
            }
            if (!foundUsers.isEmpty()) {
                foundUsers.get(0).getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Thanks for submitting correction to RacingSoundtracks.com\nI will try to handle this fast\nHere are the details of your correction").queue());
                foundUsers.get(0).getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessageEmbeds(embed).queue());
//				jda.openPrivateChannelById(discordUserName).queue(privateChannel -> privateChannel
//						.sendMessageEmbeds(eb.build()).queue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/langdisplay/{lang}")
    public @ResponseBody String getLanguageDisplay(Model model, @PathVariable("lang") String lang) {
        Locale loc = new Locale(lang);
        String displayLoc = loc.getDisplayLanguage();
        return displayLoc;
    }

    @GetMapping(value = "/customtheme/{game_id}")
    public @ResponseBody String getCustomTheme(@PathVariable("game_id") Integer game_id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<Game> game = gameService.findById(game_id);
        if (game.isPresent()) {
            Optional<CustomTheme> customTheme = customThemeService.findByGame(game.get());
            if (customTheme.isPresent()) {
                return objectMapper.writeValueAsString(customTheme.get());
            }
        }
        return objectMapper.writeValueAsString(null);
    }

    @GetMapping(value = "/dynamictheme/{bg_id}")
    public @ResponseBody String getDynamicTheme(@PathVariable("bg_id") Integer bg_id) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<CustomTheme> customTheme = customThemeService.findById(bg_id);
        if (customTheme.isPresent()) {
            return objectMapper.writeValueAsString(customTheme.get());
        }
        return objectMapper.writeValueAsString(null);
    }
}
