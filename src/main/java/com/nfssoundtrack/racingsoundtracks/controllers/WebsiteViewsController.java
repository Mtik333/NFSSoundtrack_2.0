package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.*;
import com.nfssoundtrack.racingsoundtracks.serializers.SongSerializer;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/**
 * main controller with all the basic endpoint for non-authenticated users
 */
@Controller
public class WebsiteViewsController  {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteViewsController.class);
    public static final String APP_NAME = "appName";
    public static final String MIN_INDEX = "index";
    public static final String DESC_BREAK = "\n====================\n\n";
    public static final String SERIES = "series";
    public static final String GAMES_ALPHA = "gamesAlpha";
    static List<TranslationObj> translationObjs = new ArrayList<>();

    private final SongSerializer songSerializer;
    private final BaseControllerWithErrorHandling baseController;
    private final WebClient webClient;

    public WebsiteViewsController(SongSerializer songSerializer, BaseControllerWithErrorHandling baseController) {
        this.songSerializer = songSerializer;
        this.baseController = baseController;
        // Configure WebClient with larger buffer size for big pages
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024)) // 5MB
                .build();
                
        this.webClient = WebClient.builder()
                .baseUrl("https://racingsoundtracks.com:445")
                .exchangeStrategies(strategies)
                .build();
    }

    @Value("${spring.application.name}")
    String appName;

    @Value("${bot.token}")
    private String botSecret;

    @Value("${textchannel.id}")
    private String channelId;

    @Value("${changelog.id}")
    private String changeLogId;

    static JDA jda;

    public static JDA getJda() {
        return jda;
    }

    /**
     * @return the main 'endpoint' of the website
     */
    @GetMapping(value = "/")
    public String mainPage(HttpServletRequest request) {
        if (request != null && request.getServerName().equals("old.racingsoundtracks.com")) {
            // Let Apache handle old domain requests
            return "forward:/index.php";
            // or return some error to let Apache take over
        }
        return "redirect:/content/home";
    }

    /**
     * @param model view model
     * @return endpoint of admin panel
     */
    @GetMapping(value = "/manage/manage")
    public String manage(Model model) throws LoginException, ResourceNotFoundException, InterruptedException {
        addCommonAttributes(model, "genericAt", new String[]{"Manage"});
        return "manage";
    }

    /**
     * @param model view model
     * @return endpoint of login
     */
    @GetMapping(value = "/login")
    public String login(Model model) throws LoginException, ResourceNotFoundException, InterruptedException {
        addCommonAttributes(model, "genericAt", new String[]{"Login"});
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
        addCommonAttributes(model, "genericAt", new String[]{value});
        model.addAttribute("htmlToInject",
                baseController.getContentService().findByContentShort(value).getContentData());
        boolean isHome = value.contains("home");
        model.addAttribute("home", isHome);
        //when we are at home page, we have to render todays song in center
        model.addAttribute("todayssong", baseController.getTodaysSongService().getTodaysSong());
        return MIN_INDEX;
    }

    /**
     * @param model     view model
     * @param gameshort short name from 'game' table
     * @return whole display of game's soundtrack
     */
    @GetMapping(value = "/game/{gameshort}")
    public String game(Model model, @PathVariable("gameshort") String gameshort/*, HttpSession httpSession*/) throws LoginException, ResourceNotFoundException, InterruptedException {
        Game game = baseController.getGameService().findByGameShort(gameshort);
        model.addAttribute("endpoint", "/game/" + gameshort);
        model.addAttribute("game", game);
        addCommonAttributes(model, "gameSoundtrackAt", new String[]{game.getDisplayTitle()});
        //for few games there's custom theme to make website background a bit more interesting
        Optional<CustomTheme> customTheme = baseController.getCustomThemeService().findByGame(game);
        customTheme.ifPresent(theme -> model.addAttribute("customTheme", theme));
        model.addAttribute("songSubgroups", baseController.getSongSubgroupService().hasGameAnySongs(game));
        return MIN_INDEX;
    }

    /**
     * @param model          view model
     * @param customPlaylist array of song ids, example ["350","357","312","315","410","425","528"]
     * @return display of custom playlist songs table (after clicking 'custom playlist' button)
     * @throws ResourceNotFoundException exception when song subgroup not found
     * @throws JsonProcessingException   exception due to objectmapper
     */
    @PostMapping(value = "/custom/playlist")
    public String getCustomPlaylist(Model model, @RequestBody String customPlaylist)
            throws ResourceNotFoundException, JsonProcessingException, LoginException, InterruptedException {
        List<SongSubgroup> songSubgroupList = new ArrayList<>();
        if (customPlaylist == null || customPlaylist.isEmpty()) {
            logger.error("do something");
        } else {
            //yes this is stupid but i cannot find the way of sending user browser cache to the backend other than this
            String result = URLDecoder.decode(customPlaylist, StandardCharsets.UTF_8);
            String basicallyArray = result.replace("customPlaylist=", "");
            List<String> finalList = new ObjectMapper().readValue(basicallyArray,
                    TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
            //as array consits of song-subgroup id, we are looking for it in database
            for (String songSubgroupId : finalList) {
                songSubgroupList.add(baseController.getSongSubgroupService().findById(Integer.valueOf(
                        songSubgroupId)).orElseThrow(() -> new ResourceNotFoundException("No songsubgroup found with " +
                        "id " + songSubgroupId)));
            }
        }
        addCommonAttributes(model, "customPlaylistAt", null);
        model.addAttribute("customPlaylist", songSubgroupList);
        return MIN_INDEX;
    }

    /**
     * @param songId id of song to fetch info about
     * @return whole information about the song (once you click on 'infobutton' next to song)
     * @throws JsonProcessingException   exception due to objectmapper
     * @throws ResourceNotFoundException when song not found by id
     */
    @GetMapping(value = "/songInfo/{songId}")
    public @ResponseBody
    String provideSongModalInfo(@PathVariable("songId") int songId)
            throws JsonProcessingException, ResourceNotFoundException {
        Song song = baseController.getSongService().findById(songId).orElseThrow(
                () -> new ResourceNotFoundException("No song with id found " + songId));
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(Song.class, songSerializer);
        String result = objectMapper.writeValueAsString(song);
        if (logger.isDebugEnabled()) {
            logger.debug("result {}", result);
        }
        return result;
    }

    /**
     * @param model  view model
     * @param songId id of song to fetch usages of it
     * @return table with usages of the song across all games (once you click on 'find all usages' button
     * after clicking on song infopage
     * @throws ResourceNotFoundException exception when song is not found
     */
    @GetMapping(value = "/song/{songId}")
    public String provideSongInfo(Model model, @PathVariable("songId") String songId) throws ResourceNotFoundException, LoginException, InterruptedException {
        Song song = baseController.getSongService().findById(Integer.valueOf(songId)).orElseThrow(
                () -> new ResourceNotFoundException("No song found with id " + songId));
        model.addAttribute("songToCheck", song);
        model.addAttribute("songUsages", baseController.getSongSubgroupService().findBySong(song));
        addCommonAttributes(model, "genericAt", new String[]{song.getOfficialDisplayBand()
                + " - " + song.getOfficialDisplayTitle()});
        return MIN_INDEX;
    }

    /**
     * @param model    view model
     * @param authorId id of author to fetch info about
     * @return table with all songs where author is being part of (once you click on author hyperlink)
     * @throws ResourceNotFoundException author not found by id
     * @throws JsonProcessingException   exception due to objectmapper
     */
    @GetMapping(value = "/author/{authorId}")
    public String readAuthor(Model model, @PathVariable("authorId") int authorId)
            throws ResourceNotFoundException, JsonProcessingException, LoginException, InterruptedException {
        Author author =
                baseController.getAuthorService().findById(authorId).orElseThrow(
                        () -> new ResourceNotFoundException("No author found with id " + authorId));
        DiscoGSObj discoGSObj = baseController.getAuthorService().fetchInfoFromMap(author);
        List<AuthorAlias> allAliases = baseController.getAuthorAliasService().findByAuthor(author);
        //we build a lot of crap as author can be assigned to songs as composer, subcomposer, feat, remixer
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsComposer = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsSubcomposer = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsFeat = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsRemixed = new HashMap<>();
        for (AuthorAlias authorAlias : allAliases) {
            //and we have to remember that author can have multiple aliases
            //so we have to group songs by both type of contribution and alias name
            List<AuthorSong> allAuthorSongs = baseController.getAuthorSongService().findByAuthorAlias(authorAlias);
            for (AuthorSong authorSong : allAuthorSongs) {
                List<SongSubgroup> songSubgroupList =
                        baseController.getSongSubgroupService().findBySong(authorSong.getSong());
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
        List<AuthorMember> artistsAuthorIsMemberOf = baseController.getAuthorMemberService().findByMember(author);
        List<AuthorMember> artistsBeingMembersOfAuthor = baseController.getAuthorMemberService().findByAuthor(author);
        List<AuthorAlias> relatedAuthorAliases = null;
        List<Author> relatedAuthors = null;
        if (!artistsAuthorIsMemberOf.isEmpty()) {
            relatedAuthorAliases = new ArrayList<>();
            relatedAuthors = new ArrayList<>();
            for (AuthorMember authorMember : artistsAuthorIsMemberOf) {
                relatedAuthors.add(authorMember.getAuthor());
                relatedAuthorAliases.addAll(baseController.getAuthorAliasService().findByAuthor(authorMember.getAuthor()));
            }
        }
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> memberOfAsComposer = new HashMap<>();
        if (relatedAuthorAliases != null) {
            for (AuthorAlias memberOfAlias : relatedAuthorAliases) {
                //and we have to remember that author can have multiple aliases
                //so we have to group songs by both type of contribution and alias name
                List<AuthorSong> allAuthorSongs = baseController.getAuthorSongService().findByAuthorAlias(memberOfAlias);
                for (AuthorSong authorSong : allAuthorSongs) {
                    List<SongSubgroup> songSubgroupList =
                            baseController.getSongSubgroupService().findBySong(authorSong.getSong());
                    JustSomeHelper.fillMapForArtistDisplay(memberOfAlias, authorSong, Role.COMPOSER, songsAsComposer,
                            songSubgroupList);
                }
            }
        }
        //we give back info about author, discogs info and all the songs found, as well as the aliases
        model.addAttribute("discoGSObj", discoGSObj);
        model.addAttribute("author", author);
        model.addAttribute("songsAsComposer", songsAsComposer);
        model.addAttribute("songsAsSubcomposer", songsAsSubcomposer);
        model.addAttribute("songsAsFeat", songsAsFeat);
        model.addAttribute("songsRemixed", songsRemixed);
        model.addAttribute("allAliases", allAliases);
        if (relatedAuthorAliases != null) {
            model.addAttribute("relatedAuthorAliases", relatedAuthorAliases);
            model.addAttribute("relatedAuthors", relatedAuthors);
            model.addAttribute("memberOfAsComposer", memberOfAsComposer);
        }
        model.addAttribute("artistsBeingMembersOfAuthor", artistsBeingMembersOfAuthor);
        addCommonAttributes(model, "genericAt", new String[]{author.getName()});
        return MIN_INDEX;
    }

    /**
     * @param model   view model
     * @param genreId id of genre
     * @return table with songs associated with genre (limited to 50 entries) (once you click on genre link)
     * @throws ResourceNotFoundException when genre with this id not found
     */
    @GetMapping(value = "/genre/{genreId}")
    public String readGenreInfo(Model model, @PathVariable("genreId") int genreId) throws ResourceNotFoundException, LoginException, InterruptedException {
        Genre genre =
                baseController.getGenreService().findById(genreId).orElseThrow(
                        () -> new ResourceNotFoundException("No genre found with id " + genreId));
        List<SongGenre> songGenreList = baseController.getSongGenreService().findByGenre(genre, 50);
        List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
        //for brief look at the genre, we just render first 50 songs we find in db
        List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySongInSortedByIdAsc(songs);
        model.addAttribute("songSubgroupList", songSubgroupList);
        model.addAttribute("genre", genre);
        model.addAttribute("readFull", true);
        addCommonAttributes(model, "genericAt", new String[]{genre.getGenreName()});
        return MIN_INDEX;
    }

    /**
     * @param model   view model
     * @param genreId id of genre
     * @return table with songs associated with genre (after you click 'do you want to see full list' from genre table)
     * @throws ResourceNotFoundException when genre with this id not found
     */
    @GetMapping(value = "/genre/readfull/{genreId}")
    public String readGenreInfoFull(Model model, @PathVariable("genreId") int genreId)
            throws ResourceNotFoundException, LoginException, InterruptedException {
        Genre genre =
                baseController.getGenreService().findById(genreId).orElseThrow(
                        () -> new ResourceNotFoundException("No genre found with id " + genreId));
        //now we go full with hundreds of entries
        //first we get song-genre associations
        List<SongGenre> songGenreList = baseController.getSongGenreService().findByGenre(genre);
        //then we get just pure songs
        List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
        //finally retrieve all song-subgroup entries to show where song was really used
        List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySongInSortedByIdAsc(songs);
        model.addAttribute("songSubgroupList", songSubgroupList);
        model.addAttribute("genre", genre);
        addCommonAttributes(model, "fullGenreListAt", new String[]{genre.getGenreName()});
        return MIN_INDEX;
    }

    /**
     * @param model view model
     * @return table with todays songs randomly picked in last 30 days
     */
    @GetMapping(value = "/songhistory")
    public String getTodaysSongHistory(Model model) throws LoginException, ResourceNotFoundException, InterruptedException {
        List<TodaysSong> todays30Songs = baseController.getTodaysSongService().findAllFromLast30Days();
        model.addAttribute("todays30Songs", todays30Songs);
        model.addAttribute("readFull", true);
        addCommonAttributes(model, "archiveOfSongsAt", null);
        return MIN_INDEX;
    }

    /**
     * @param model view model
     * @return table with all todays songs ever calculated or fetched
     * @throws LoginException
     * @throws ResourceNotFoundException
     * @throws InterruptedException
     */
    @GetMapping(value = "/songhistory/readfull")
    public String getTodaysSongHistoryFull(Model model) throws LoginException, ResourceNotFoundException, InterruptedException {
        List<TodaysSong> todays30Songs = baseController.getTodaysSongService().findAll();
        model.addAttribute("todays30Songs", todays30Songs);
        addCommonAttributes(model, "archiveOfSongsAt", null);
        return MIN_INDEX;
    }

    @GetMapping(value = "/countryInfo/{countryId}")
    public String getArtistsFromCountry(Model model, @PathVariable("countryId") int countryId)
            throws ResourceNotFoundException, LoginException, InterruptedException {
        Country country =
                baseController.getCountryService().findById(countryId).orElseThrow(
                        () -> new ResourceNotFoundException("No country found with id " + countryId));
        List<Country> allCountries = baseController.getCountryService().findAll();
        List<AuthorCountry> authorsFromCountry = baseController.getAuthorCountryService().findByCountry(country);
        List<AuthorGameObj> rowsToDisplay = new ArrayList<>();
        for (AuthorCountry authorCountry : authorsFromCountry) {
            Author author = authorCountry.getAuthor();
            List<AuthorAlias> aliases = baseController.getAuthorAliasService().findByAuthor(author);
            Map<Game, Integer> songsPerGame = new HashMap<>();
            int countSongs = 0;
            for (AuthorAlias alias : aliases) {
                List<AuthorSong> authorSongs = baseController.getAuthorSongService().findByAuthorAlias(alias);
                countSongs += authorSongs.size();
                for (AuthorSong authorSong : authorSongs) {
                    Song song = authorSong.getSong();
                    List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySong(song);
                    //it can be that one song appears in more than one subgroup in a game
                    //so i don't want to screw up counting and make a distinct game check here
                    songSubgroupList = songSubgroupList.stream().filter(JustSomeHelper.distinctByKey(songSubgroup ->
                            songSubgroup.getSubgroup().getMainGroup().getGame())).toList();
                    for (SongSubgroup songSubgroup : songSubgroupList) {
                        Game game = songSubgroup.getSubgroup().getMainGroup().getGame();
                        if (!songsPerGame.containsKey(game)) {
                            songsPerGame.put(game, 1);
                        } else {
                            songsPerGame.put(game, songsPerGame.get(game) + 1);
                        }
                    }
                }
            }
            AuthorGameObj authorGameObj = new AuthorGameObj(author, countSongs, songsPerGame);
            rowsToDisplay.add(authorGameObj);
        }
        model.addAttribute("countryAuthors", rowsToDisplay);
        model.addAttribute("selectedCountry", country);
        model.addAttribute("allCountries", allCountries);
        addCommonAttributes(model, "countryAuthorsAt", new String[]{country.getCountryName()});
        return MIN_INDEX;
    }

    @GetMapping(value = "/corrections")
    public String getAllCorrections(Model model) throws LoginException, ResourceNotFoundException, InterruptedException {
        List<Correction> corrections = baseController.getCorrectionService().findAll();
        model.addAttribute("corrections", corrections);
        addCommonAttributes(model, "genericAt", new String[]{"Corrections"});
        return MIN_INDEX;
    }

    @GetMapping(value = "/changelog")
    public String getLatestChangelogs(Model model) throws LoginException, ResourceNotFoundException, InterruptedException {
        Page<Changelog> changelogsPage = baseController.getChangelogService().findAll(50);
        List<Changelog> changelogs = changelogsPage.stream().toList();
        model.addAttribute("changelogs", changelogs);
        model.addAttribute("readFull", true);
        addCommonAttributes(model, "genericAt", new String[]{"Changelog"});
        return MIN_INDEX;
    }

    @GetMapping(value = "/changelog/readfull")
    public String getAllChangelogs(Model model) throws LoginException, ResourceNotFoundException, InterruptedException {
        List<Changelog> changelogs = baseController.getChangelogService().findAll();
        model.addAttribute("changelogs", changelogs);
        addCommonAttributes(model, "genericAt", new String[]{"Changelog"});
        return MIN_INDEX;
    }
    /**
     * some attributes provided to model repeat with each invocation so let's group it in a single place
     *
     * @param model      view model
     * @param appNameKey key from message.properties to be translated
     * @param params     params to provide to the message translation
     */
    private void addCommonAttributes(Model model, String appNameKey, String[] params) throws LoginException, ResourceNotFoundException, InterruptedException {
        //name of the app, rather unrelevant
        model.addAttribute(APP_NAME, baseController.getLocalizedMessage(appNameKey, params));
        model.addAttribute(SERIES, baseController.getSerieService().findAllSortedByPositionAsc());
        model.addAttribute(GAMES_ALPHA, baseController.getGameService().findAllSortedByDisplayTitleAsc());
        model.addAttribute("translations", translationObjs);
        model.addAttribute("todayssong", baseController.getTodaysSongService().getTodaysSong());
    }

    /**
     * invoked when clicking on 'submit correction' button after clicking on 'bug' icon on game's soundtrack page
     * can be also triggered by 'report problem' action from top menu bar
     *
     * @param formData consists of type of issue, info from user and potential contact
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws InterruptedException
     */
    @PostMapping(value = "/correction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String submitCorrection(@RequestBody String formData)
            throws JsonProcessingException, InterruptedException {
        Map<?, ?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
        //can be that issue is reported to specific row in game's soundtrack
        int songSubgroupId = (int) objectMapper.get("affectedSongsubgroup");
        String pageUrl = (String) objectMapper.get("sourceUrl");
        String problemType = (String) objectMapper.get("problemType");
        String correctValue = (String) objectMapper.get("rightValue");
        String discordUserName = (String) objectMapper.get("discordUsername");
        try {
            Optional<SongSubgroup> songSubgroup;
            Correction correction;
            //so depending on that we will create the correction and store in database
            if (songSubgroupId != -1) {
                songSubgroup = baseController.getSongSubgroupService().findById(songSubgroupId);
                if (songSubgroup.isPresent()) {
                    correction = new Correction(songSubgroup.get(), pageUrl, ProblemType.valueOf(problemType), correctValue, discordUserName);
                } else {
                    throw new ResourceNotFoundException("no songSubgroup found with id " + songSubgroupId);
                }
            } else {
                songSubgroup = Optional.empty();
                correction = new Correction(null, pageUrl, ProblemType.valueOf(problemType), correctValue, discordUserName);
            }
            correction.setCorrectionStatus(CorrectionStatus.PENDING);
            correction = baseController.getCorrectionService().save(correction);
            //if user was provided, we will send the message to discord server so admin can check this correction
            rebuildJda(botSecret);
            List<Member> foundUsers;
            if (!discordUserName.isEmpty()) {
                discordUserName = discordUserName.replace("@", "");
                foundUsers = jda.getGuilds().get(0).retrieveMembersByPrefix(discordUserName, 1).get();
            } else {
                foundUsers = new ArrayList<>();
            }
            //i think i took most of that from some tutorial to make it look readable
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
            //if user was there and found in Discord list, message would be sent to thank for the correction
            if (!foundUsers.isEmpty()) {
                foundUsers.get(0).getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage("Thanks for submitting correction to RacingSoundtracks.com\nI will try to handle this fast\nHere are the details of your correction").queue());
                foundUsers.get(0).getUser().openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessageEmbeds(embed).queue());
            }
        } catch (ResourceNotFoundException | InterruptedException e) {
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
        Optional<Game> game = baseController.getGameService().findById(gameId);
        if (game.isPresent()) {
            Optional<CustomTheme> customTheme = baseController.getCustomThemeService().findByGame(game.get());
            if (customTheme.isPresent()) {
                return objectMapper.writeValueAsString(customTheme.get());
            }
        }
        return objectMapper.writeValueAsString(null);
    }

    @GetMapping(value = "/dynamictheme/{bgId}")
    public @ResponseBody String getDynamicTheme(@PathVariable("bgId") Integer bgId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<CustomTheme> customTheme = baseController.getCustomThemeService().findById(bgId);
        if (customTheme.isPresent()) {
            return objectMapper.writeValueAsString(customTheme.get());
        }
        return objectMapper.writeValueAsString(null);
    }

    /**
     * endpoint to fetch lyrics for a specific song subgroup
     * @param songSubgroupId id of the song subgroup
     * @return lyrics text or empty string if not found
     * @throws ResourceNotFoundException when song subgroup not found
     */
    @GetMapping(value = "/lyrics/{songSubgroupId}")
    public @ResponseBody String getLyrics(@PathVariable("songSubgroupId") int songSubgroupId) throws ResourceNotFoundException {
        SongSubgroup songSubgroup = baseController.getSongSubgroupService().findById(songSubgroupId)
                .orElseThrow(() -> new ResourceNotFoundException("SongSubgroup not found with id " + songSubgroupId));
        
        // Priority logic: songSubgroup.lyrics -> song.lyrics
        String lyrics = songSubgroup.getLyrics() != null ? 
            songSubgroup.getLyrics() : songSubgroup.getSong().getLyrics();
        
        return lyrics != null ? lyrics : "";
    }

    /**
     * endpoint used for stuff from Toni's Music Library - not relevant to the website
     * he uploads a lot of music and includes original filenames
     * now we can use filename as input to get video title for him as well as description
     * to save a lot of time on editing hundreds of videos
     *
     * @param songFilename
     * @return
     */
    @GetMapping(value = "/filename/{songFilename}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String getSongInfo(@PathVariable("songFilename") String songFilename) {
        songFilename = URLDecoder.decode(songFilename, StandardCharsets.UTF_8);
        List<SongSubgroup> songsByFilename = baseController.getSongSubgroupService().findByFilenameStartsWith(songFilename);
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
            List<SongSubgroup> songSubgroupSameGameList = baseController.getSongSubgroupService().findBySong(realSong);
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

    /**
     * just rebuilding the JDA to be able to send / receive messages via bot
     *
     * @param botSecret
     * @throws LoginException
     * @throws InterruptedException
     */
    public static JDA rebuildJda(String botSecret) throws InterruptedException {
        if (jda == null) {
            jda = JDABuilder.createDefault(botSecret).build();
            jda.awaitReady();
        }
        return jda;
    }

    /**
     * Handle requests to old.racingsoundtracks.com subdomain by proxying to Apache server on port 445
     */
    @RequestMapping(value = "/**", headers = "host=old.racingsoundtracks.com")
    public ResponseEntity<byte[]> handleOldSubdomain(HttpServletRequest request, HttpServletResponse response) {
        try {
            String requestUri = request.getRequestURI();
            String queryString = request.getQueryString();
            String fullPath = requestUri + (queryString != null ? "?" + queryString : "");
            logger.info("Proxying old subdomain request: {}", fullPath);
            
            // Get response as bytes to handle both text and binary content
            byte[] responseBody = webClient.get()
                    .uri(fullPath)
                    .header("Host", "old.racingsoundtracks.com")
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            
            // Determine content type from the request path
            String contentType = getContentType(requestUri);
            
            // Only rewrite HTML content
            if (contentType.contains("text/html") && responseBody != null) {
                String htmlContent = new String(responseBody, StandardCharsets.UTF_8);
                String rewrittenBody = htmlContent
                        .replace("racingsoundtracks.com:445", "old.racingsoundtracks.com")
                        .replace("href=\"/", "href=\"https://old.racingsoundtracks.com/")
                        .replace("src=\"/", "src=\"https://old.racingsoundtracks.com/");
                responseBody = rewrittenBody.getBytes(StandardCharsets.UTF_8);
            }
            
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(responseBody);
                    
        } catch (WebClientResponseException e) {
            logger.error("Error proxying to Apache server: {}", e.getMessage());
            return ResponseEntity.status(e.getStatusCode())
                    .body(("Error accessing old site: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            logger.error("Unexpected error proxying request: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body("Internal server error while proxying request".getBytes());
        }
    }

    /**
     * Helper method to determine content type based on file extension
     */
    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        } else if (requestUri.endsWith(".js")) {
            return "application/javascript";
        } else if (requestUri.endsWith(".png")) {
            return "image/png";
        } else if (requestUri.endsWith(".jpg") || requestUri.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (requestUri.endsWith(".gif")) {
            return "image/gif";
        } else if (requestUri.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return "text/html; charset=UTF-8";
        }
    }

    /**
     * used in that little dropdown to select language via flag
     */
    @PostConstruct
    private void setupTranslationList() {
        Country england = baseController.getCountryService().findByCountryName("England").orElse(new Country());
        Country germany = baseController.getCountryService().findByCountryName("Germany").orElse(new Country());
        Country greece = baseController.getCountryService().findByCountryName("Greece").orElse(new Country());
        Country spain = baseController.getCountryService().findByCountryName("Spain").orElse(new Country());
        Country france = baseController.getCountryService().findByCountryName("France").orElse(new Country());
        Country india = baseController.getCountryService().findByCountryName("India").orElse(new Country());
        Country hungary = baseController.getCountryService().findByCountryName("Hungary").orElse(new Country());
        Country indonesia = baseController.getCountryService().findByCountryName("Indonesia").orElse(new Country());
        Country italy = baseController.getCountryService().findByCountryName("Italy").orElse(new Country());
        Country japan = baseController.getCountryService().findByCountryName("Japan").orElse(new Country());
        Country poland = baseController.getCountryService().findByCountryName("Poland").orElse(new Country());
        Country portugal = baseController.getCountryService().findByCountryName("Portugal").orElse(new Country());
        Country russia = baseController.getCountryService().findByCountryName("Russia").orElse(new Country());
        Country turkey = baseController.getCountryService().findByCountryName("Turkey").orElse(new Country());
        Country ukraine = baseController.getCountryService().findByCountryName("Ukraine").orElse(new Country());
        Country china = baseController.getCountryService().findByCountryName("China").orElse(new Country());
        Country arabia = baseController.getCountryService().findByCountryName("Saudi Arabia").orElse(new Country());
        translationObjs.add(new TranslationObj("en", england.getCountryName(), england.getLocalLink(), "english"));
        translationObjs.add(new TranslationObj("ar", "Arabic", arabia.getLocalLink(), "arabic"));
        translationObjs.add(new TranslationObj("de", germany.getCountryName(), germany.getLocalLink(), "german"));
        translationObjs.add(new TranslationObj("el", greece.getCountryName(), greece.getLocalLink(), "greek"));
        translationObjs.add(new TranslationObj("es", spain.getCountryName(), spain.getLocalLink(), "spanish"));
        translationObjs.add(new TranslationObj("fr", france.getCountryName(), france.getLocalLink(), "french"));
        translationObjs.add(new TranslationObj("hi", india.getCountryName(), india.getLocalLink(), "hindi"));
        translationObjs.add(new TranslationObj("hu", hungary.getCountryName(), hungary.getLocalLink(), "hungarian"));
        translationObjs.add(new TranslationObj("id", indonesia.getCountryName(), indonesia.getLocalLink(), "indonesian"));
        translationObjs.add(new TranslationObj("it", italy.getCountryName(), italy.getLocalLink(), "italian"));
        translationObjs.add(new TranslationObj("jp", japan.getCountryName(), japan.getLocalLink(), "japanese"));
        translationObjs.add(new TranslationObj("pl", poland.getCountryName(), poland.getLocalLink(), "polish"));
        translationObjs.add(new TranslationObj("pt", portugal.getCountryName(), portugal.getLocalLink(), "portuguese"));
        translationObjs.add(new TranslationObj("ru", russia.getCountryName(), russia.getLocalLink(), "russian"));
        translationObjs.add(new TranslationObj("tr", turkey.getCountryName(), turkey.getLocalLink(), "turkish"));
        translationObjs.add(new TranslationObj("uk", ukraine.getCountryName(), ukraine.getLocalLink(), "ukrainian"));
        translationObjs.add(new TranslationObj("zh", china.getCountryName(), china.getLocalLink(), "chinese"));
    }
}
