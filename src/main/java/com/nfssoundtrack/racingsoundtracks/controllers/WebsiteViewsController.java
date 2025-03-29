package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.*;
import com.nfssoundtrack.racingsoundtracks.serializers.SongSerializer;
import com.nfssoundtrack.racingsoundtracks.serializers.SongSubgroupFilenameSerializer;
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
import java.util.*;
import java.util.List;

/**
 * main controller with all the basic endpoint for non-authenticated users
 */
@Controller
public class WebsiteViewsController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteViewsController.class);
    public static final String APP_NAME = "appName";
    public static final String MIN_INDEX = "index";
    public static final String DESC_BREAK = "\n====================\n\n";
    public static final String SERIES = "series";
    public static final String GAMES_ALPHA = "gamesAlpha";
    public static List<TranslationObj> translationObjs = new ArrayList<>();

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
                contentService.findByContentShort(value).getContentData());
        boolean isHome = value.contains("home");
        model.addAttribute("home", isHome);
        //when we are at home page, we have to render todays song in center
        model.addAttribute("todayssong", todaysSongService.getTodaysSong());
        return MIN_INDEX;
    }

    /**
     * @param model     view model
     * @param gameshort short name from 'game' table
     * @return whole display of game's soundtrack
     */
    @GetMapping(value = "/game/{gameshort}")
    public String game(Model model, @PathVariable("gameshort") String gameshort/*, HttpSession httpSession*/) throws LoginException, ResourceNotFoundException, InterruptedException {
        Game game = gameService.findByGameShort(gameshort);
        model.addAttribute("endpoint", "/game/" + gameshort);
        model.addAttribute("game", game);
        addCommonAttributes(model, "gameSoundtrackAt", new String[]{game.getDisplayTitle()});
        //for few games there's custom theme to make website background a bit more interesting
        Optional<CustomTheme> customTheme = customThemeService.findByGame(game);
        customTheme.ifPresent(theme -> model.addAttribute("customTheme", theme));
        model.addAttribute("songSubgroups", songSubgroupService.hasGameAnySongs(game));
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
                songSubgroupList.add(songSubgroupService.findById(Integer.valueOf(
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
        Song song = songService.findById(songId).orElseThrow(
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
        Song song = songService.findById(Integer.valueOf(songId)).orElseThrow(
                () -> new ResourceNotFoundException("No song found with id " + songId));
        model.addAttribute("songToCheck", song);
        model.addAttribute("songUsages", songSubgroupService.findBySong(song));
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
                authorService.findById(authorId).orElseThrow(
                        () -> new ResourceNotFoundException("No author found with id " + authorId));
        DiscoGSObj discoGSObj = authorService.fetchInfoFromMap(author);
        List<AuthorAlias> allAliases = authorAliasService.findByAuthor(author);
        //we build a lot of crap as author can be assigned to songs as composer, subcomposer, feat, remixer
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsComposer = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsSubcomposer = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsFeat = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsRemixed = new HashMap<>();
        for (AuthorAlias authorAlias : allAliases) {
            //and we have to remember that author can have multiple aliases
            //so we have to group songs by both type of contribution and alias name
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
        List<AuthorMember> artistsAuthorIsMemberOf = authorMemberService.findByMember(author);
        List<AuthorMember> artistsBeingMembersOfAuthor = authorMemberService.findByAuthor(author);
        List<AuthorAlias> relatedAuthorAliases = null;
        List<Author> relatedAuthors = null;
        if (!artistsAuthorIsMemberOf.isEmpty()) {
            relatedAuthorAliases = new ArrayList<>();
            relatedAuthors = new ArrayList<>();
            for (AuthorMember authorMember : artistsAuthorIsMemberOf) {
                relatedAuthors.add(authorMember.getAuthor());
                relatedAuthorAliases.addAll(authorAliasService.findByAuthor(authorMember.getAuthor()));
            }
        }
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> memberOfAsComposer = new HashMap<>();
        if (relatedAuthorAliases != null) {
            for (AuthorAlias memberOfAlias : relatedAuthorAliases) {
                //and we have to remember that author can have multiple aliases
                //so we have to group songs by both type of contribution and alias name
                List<AuthorSong> allAuthorSongs = authorSongService.findByAuthorAlias(memberOfAlias);
                for (AuthorSong authorSong : allAuthorSongs) {
                    List<SongSubgroup> songSubgroupList =
                            songSubgroupService.findBySong(authorSong.getSong());
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
        if (relatedAuthorAliases!=null){
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
                genreService.findById(genreId).orElseThrow(
                        () -> new ResourceNotFoundException("No genre found with id " + genreId));
        List<SongGenre> songGenreList = songGenreService.findByGenre(genre, 50);
        List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
        //for brief look at the genre, we just render first 50 songs we find in db
        List<SongSubgroup> songSubgroupList = songSubgroupService.findBySongInSortedByIdAsc(songs);
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
                genreService.findById(genreId).orElseThrow(
                        () -> new ResourceNotFoundException("No genre found with id " + genreId));
        //now we go full with hundreds of entries
        //first we get song-genre associations
        List<SongGenre> songGenreList = songGenreService.findByGenre(genre);
        //then we get just pure songs
        List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
        //finally retrieve all song-subgroup entries to show where song was really used
        List<SongSubgroup> songSubgroupList = songSubgroupService.findBySongInSortedByIdAsc(songs);
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
        List<TodaysSong> todays30Songs = todaysSongService.findAllFromLast30Days();
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
        List<TodaysSong> todays30Songs = todaysSongService.findAll();
        model.addAttribute("todays30Songs", todays30Songs);
        addCommonAttributes(model, "archiveOfSongsAt", null);
        return MIN_INDEX;
    }

    @GetMapping(value = "/countryInfo/{countryId}")
    public String getArtistsFromCountry(Model model, @PathVariable("countryId") int countryId)
            throws ResourceNotFoundException, LoginException, InterruptedException {
        Country country =
                countryService.findById(countryId).orElseThrow(
                        () -> new ResourceNotFoundException("No country found with id " + countryId));
        List<Country> allCountries = countryService.findAll();
        List<AuthorCountry> authorsFromCountry = authorCountryService.findByCountry(country);
        List<AuthorGameObj> rowsToDisplay = new ArrayList<>();
        for (AuthorCountry authorCountry : authorsFromCountry) {
            Author author = authorCountry.getAuthor();
            List<AuthorAlias> aliases = authorAliasService.findByAuthor(author);
            Map<Game, Integer> songsPerGame = new HashMap<>();
            Set<Game> games = new HashSet<>();
            int countSongs = 0;
            for (AuthorAlias alias : aliases) {
                List<AuthorSong> authorSongs = authorSongService.findByAuthorAlias(alias);
                countSongs += authorSongs.size();
                for (AuthorSong authorSong : authorSongs) {
                    Song song = authorSong.getSong();
                    List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(song);
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
    public String corrections(Model model) throws LoginException, ResourceNotFoundException, InterruptedException {
        List<Correction> corrections = correctionService.findAll();
        model.addAttribute("corrections", corrections);
        addCommonAttributes(model, "genericAt", new String[]{"Corrections"});
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
        model.addAttribute(APP_NAME, getLocalizedMessage(appNameKey, params));
        model.addAttribute(SERIES, serieService.findAllSortedByPositionAsc());
        model.addAttribute(GAMES_ALPHA, gameService.findAllSortedByDisplayTitleAsc());
        model.addAttribute("translations", translationObjs);
        model.addAttribute("todayssong", todaysSongService.getTodaysSong());
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
                songSubgroup = songSubgroupService.findById(songSubgroupId);
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
            correction = correctionService.save(correction);
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

    /**
     * just rebuilding the JDA to be able to send / receive messages via bot
     *
     * @param botSecret
     * @throws LoginException
     * @throws InterruptedException
     */
    public static void rebuildJda(String botSecret) throws LoginException, InterruptedException {
        if (jda == null) {
            jda = JDABuilder.createDefault(botSecret).build();
            jda.awaitReady();
        }
    }

    /**
     * used in that little dropdown to select language via flag
     */
    @PostConstruct
    private void setupTranslationList() {
        Country england = countryService.findByCountryName("England").orElse(new Country());
        Country germany = countryService.findByCountryName("Germany").orElse(new Country());
        Country greece = countryService.findByCountryName("Greece").orElse(new Country());
        Country spain = countryService.findByCountryName("Spain").orElse(new Country());
        Country france = countryService.findByCountryName("France").orElse(new Country());
        Country india = countryService.findByCountryName("India").orElse(new Country());
        Country hungary = countryService.findByCountryName("Hungary").orElse(new Country());
        Country indonesia = countryService.findByCountryName("Indonesia").orElse(new Country());
        Country italy = countryService.findByCountryName("Italy").orElse(new Country());
        Country japan = countryService.findByCountryName("Japan").orElse(new Country());
        Country poland = countryService.findByCountryName("Poland").orElse(new Country());
        Country portugal = countryService.findByCountryName("Portugal").orElse(new Country());
        Country russia = countryService.findByCountryName("Russia").orElse(new Country());
        Country turkey = countryService.findByCountryName("Turkey").orElse(new Country());
        Country ukraine = countryService.findByCountryName("Ukraine").orElse(new Country());
        Country china = countryService.findByCountryName("China").orElse(new Country());
        Country arabia = countryService.findByCountryName("Saudi Arabia").orElse(new Country());
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
