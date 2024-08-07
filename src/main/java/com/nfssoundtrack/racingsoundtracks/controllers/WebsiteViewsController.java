package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.DiscoGSObj;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.SongSerializer;
import com.nfssoundtrack.racingsoundtracks.serializers.SongSubgroupFilenameSerializer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.text.WordUtils;
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
    public static final String APP_NAME = "appName";
    public static final String MIN_INDEX = "min/index";
    public static final String DESC_BREAK = "\n====================\n\n";
    public static final String SERIES = "series";

    @Autowired
    SongSerializer songSerializer;

    @Autowired
    SongSubgroupFilenameSerializer songSubgroupFilenameSerializer;

    @Value("${spring.application.name}")
    String appName;

    @Value("${bot.token}")
    private String botSecret;

    @Value("${textchannel.id}")
    private String channelId;

    static JDA jda;

    public static JDA getJda() {
        return jda;
    }

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
        model.addAttribute(APP_NAME, appName);
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        return "manage";
    }

    /**
     * @param model view model
     * @return endpoint of login
     */
    @GetMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute(APP_NAME, appName);
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
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
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        model.addAttribute("htmlToInject",
                contentService.findByContentShort(value).getContentData());
        boolean isHome = value.contains("home");
        model.addAttribute("home", isHome);
        model.addAttribute("todayssong", todaysSongService.getTodaysSong());
        model.addAttribute(APP_NAME, value + " - " + appName);
        return MIN_INDEX;
    }

    /**
     * @param model     view model
     * @param gameshort short name from 'game' table
     * @return whole display of game's soundtrack
     */
    @GetMapping(value = "/game/{gameshort}")
    public String game(Model model, @PathVariable("gameshort") String gameshort/*, HttpSession httpSession*/) {
        Game game = gameService.findByGameShort(gameshort);
        model.addAttribute("endpoint", "/game/" + gameshort);
        model.addAttribute("game", game);
        model.addAttribute(APP_NAME, game.getDisplayTitle() +
                " " + getLocalizedMessage("soundtrackAt") + " " + appName);
        Optional<CustomTheme> customTheme = customThemeService.findByGame(game);
        customTheme.ifPresent(theme -> model.addAttribute("customTheme", theme));
        model.addAttribute("songSubgroups", songSubgroupService.hasGameAnySongs(game));
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        return MIN_INDEX;
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
        model.addAttribute(APP_NAME, "Custom playlist - " + appName);
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        model.addAttribute("customPlaylist", songSubgroupList);
        return MIN_INDEX;
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
            logger.debug("result {}", result);
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
        model.addAttribute(APP_NAME, song.getOfficialDisplayBand()
                + " - " + song.getOfficialDisplayTitle() + " - " + appName);
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        model.addAttribute("songToCheck", song);
        model.addAttribute("songUsages", songSubgroupService.findBySong(song));
        return MIN_INDEX;
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
        model.addAttribute(APP_NAME, author.getName() + " - " + appName);
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        return MIN_INDEX;
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
        model.addAttribute(APP_NAME, genre.getGenreName() + " - " + appName);
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        return MIN_INDEX;
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
        model.addAttribute(APP_NAME, "Full list of genre " + genre.getGenreName() + " - " + appName);
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        return MIN_INDEX;
    }

    @GetMapping(value = "/songhistory")
    public String getTodaysSongHistory(Model model) {
        List<TodaysSong> todays30Songs = todaysSongService.findAllFromLast30Days();
        model.addAttribute("todays30Songs", todays30Songs);
        model.addAttribute(APP_NAME, "Archive of today's songs - " + appName);
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        return MIN_INDEX;
    }

    @PostMapping(value = "/correction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String submitCorrection(@RequestBody String formData)
            throws JsonProcessingException, InterruptedException {
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
                if (songSubgroup.isPresent()){
                    correction = new Correction(songSubgroup.get(), pageUrl, ProblemType.valueOf(problemType), correctValue, discordUserName);
                } else {
                    throw new ResourceNotFoundException("no songSubgroup found with id " + songSubgroupId);
                }
            } else {
                songSubgroup = Optional.empty();
                correction = new Correction(null, pageUrl, ProblemType.valueOf(problemType), correctValue, discordUserName);
            }
            correction.setCorrectionStatus(CorrectionStatus.PENDING);
            correction = correctionService.save(correction);
            rebuildJda(botSecret);
            List<Member> foundUsers;
            if (!discordUserName.isEmpty()) {
                discordUserName = discordUserName.replace("@", "");
                foundUsers = jda.getGuilds().get(0).retrieveMembersByPrefix(discordUserName, 1).get();
            } else {
                foundUsers = new ArrayList<>();
            }
            TextChannel textChannel = jda.getTextChannelById(channelId);
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
            }
        } catch (LoginException | ResourceNotFoundException | InterruptedException e) {
            throw new InterruptedException(e.getMessage());
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/langdisplay/{lang}")
    public @ResponseBody String getLanguageDisplay(@PathVariable("lang") String lang) {
        Locale loc = new Locale(lang);
        return loc.getDisplayLanguage();
    }

    @GetMapping(value = "/customtheme/{gameId}")
    public @ResponseBody String getCustomTheme(@PathVariable("gameId") Integer gameId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<Game> game = gameService.findById(gameId);
        if (game.isPresent()) {
            Optional<CustomTheme> customTheme = customThemeService.findByGame(game.get());
            if (customTheme.isPresent()) {
                return objectMapper.writeValueAsString(customTheme.get());
            }
        }
        return objectMapper.writeValueAsString(null);
    }

    @GetMapping(value = "/dynamictheme/{bgId}")
    public @ResponseBody String getDynamicTheme(@PathVariable("bgId") Integer bgId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<CustomTheme> customTheme = customThemeService.findById(bgId);
        if (customTheme.isPresent()) {
            return objectMapper.writeValueAsString(customTheme.get());
        }
        return objectMapper.writeValueAsString(null);
    }

    @GetMapping(value = "/filename/{songFilename}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getSongInfo(@PathVariable("songFilename") String songFilename) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(SongSubgroup.class, songSubgroupFilenameSerializer);
        objectMapper.registerModule(simpleModule);
        songFilename = URLDecoder.decode(songFilename, StandardCharsets.UTF_8);
        List<SongSubgroup> songsByFilename = songSubgroupService.findByFilenameStartsWith(songFilename);
        StringBuilder resultingText = new StringBuilder();
        if (songsByFilename.size() == 1) {
            SongSubgroup songSubgroup = songsByFilename.get(0);
            String youtubePlaylistId = songSubgroup.getSubgroup().getMainGroup().getGame().getYoutubeId();
            //if playlist exists, make this first line
            if (youtubePlaylistId != null) {
                String thingToAppend = "https://www.youtube.com/watch?v=" + songSubgroup.getSrcId()
                        + "&list=" + youtubePlaylistId;
                resultingText.append("Full Soundtrack Playlist: ").append(thingToAppend);
            }
            Song realSong = songSubgroup.getSong();
            List<SongSubgroup> songSubgroupSameGameList = songSubgroupService.findBySong(realSong);
            songSubgroupSameGameList = songSubgroupSameGameList.stream().filter(songSubgroup1 ->
                            songSubgroup1.getSubgroup().getMainGroup().getGame().equals(songSubgroup.getSubgroup().getMainGroup().getGame()))
                    .filter(songSubgroup1 -> songSubgroup1.getFilename() != null && !songSubgroup1.getFilename().equals(songSubgroup.getFilename())).toList();
            if (!songSubgroupSameGameList.isEmpty()) {
                resultingText.append("\n");
            }
            for (SongSubgroup songSubgroup1 : songSubgroupSameGameList) {
                resultingText.append("\n").append(songSubgroup1.getSubgroup().getSubgroupName()).append(": ")
                        .append("https://www.youtube.com/watch?v=").append(songSubgroup1.getSrcId());
            }
            resultingText.append(DESC_BREAK);
            //now second line with song info
            resultingText.append("Track: ");
//            if (songSubgroup.getIngameDisplayTitle()!=null){
//                resultingText.append(songSubgroup.getIngameDisplayTitle());
//            } else {
//                resultingText.append(songSubgroup.getSong().getOfficialDisplayTitle());
//            }
            resultingText.append(songSubgroup.getSong().getOfficialDisplayTitle());
            resultingText.append("\n");
            List<AuthorSong> authors = songSubgroup.getSong().getAuthorSongList();
            if (authors.size() > 1) {
                for (int i = 0; i < authors.size(); i++) {
                    resultingText.append(WordUtils.capitalizeFully("Artist ")).append((i + 1))
                            .append(": ").append(authors.get(i).getAuthorAlias().getAlias()).append("\n");
                }
            } else {
                resultingText.append("Artist: ");
                if (songSubgroup.getIngameDisplayBand() != null) {
                    resultingText.append(songSubgroup.getIngameDisplayBand()).append("\n");
                } else {
                    resultingText.append(songSubgroup.getSong().getOfficialDisplayBand()).append("\n");
                }
            }
            resultingText.append(songSubgroup.getSubgroup().getMainGroup().getGame().getDisplayTitle());
            //now line with filename
            resultingText.append(DESC_BREAK);
            resultingText.append(songSubgroup.getFilename());
            //now line with promo
            resultingText.append(DESC_BREAK);
            resultingText.append("Check out more Racing Game Soundtracks: https://RacingSoundtracks.com");
            resultingText.append("\nIf you like what you hear, you can support me by making a small donation: https://www.paypal.me/SergiuAntoniuA");
            //and line with tags
            resultingText.append(DESC_BREAK);
            resultingText.append("#GameRip, #RacingSoundtracks.com");
        }
        return resultingText.toString();
    }

    public static void rebuildJda(String botSecret) throws LoginException, InterruptedException {
        if (jda == null) {
            jda = JDABuilder.createDefault(botSecret).build();
            jda.awaitReady();
        }
    }
}
