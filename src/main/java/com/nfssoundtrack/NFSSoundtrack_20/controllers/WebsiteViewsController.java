package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.DiscoGSObj;
import com.nfssoundtrack.NFSSoundtrack_20.others.JustSomeHelper;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.SongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/")
    public String mainPage(Model model) {
        return "redirect:/content/home";
    }

    @RequestMapping(value = "/manage/manage")
    public String manage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        return "manage";
    }

    @RequestMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        model.addAttribute("login", true);
        return "login";
    }

    @RequestMapping(value = "/content/{value}")
    public String topMenuEntry(Model model, @PathVariable("value") String value) {
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        model.addAttribute("htmlToInject",
                contentService.findByContentShort(value).getContentData());
        model.addAttribute("home", value.contains("home"));
        model.addAttribute("appName", value + " - " + appName);
        return "index";
    }

    @RequestMapping(value = "/game/{gameshort}")
    public String game(Model model, @PathVariable("gameshort") String gameshort) {
        Game game = gameService.findByGameShort(gameshort);
        model.addAttribute("endpoint", "/game/" + gameshort);
        model.addAttribute("game", game);
        model.addAttribute("appName", game.getDisplayTitle() + " soundtrack at NFSSoundtrack.com");
        model.addAttribute("gamegroups", game.getMainGroups());
        model.addAttribute("songSubgroups", songSubgroupService.fetchFromGame(game));
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        return "index";
    }

    @PostMapping(value = "/custom/playlist")
    public String getCustomPlaylist(Model model, @RequestBody String customPlaylist) throws Exception {
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
        model.addAttribute("appName", "Custom playlist - NFSSoundtrack.com");
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        model.addAttribute("customPlaylist", songSubgroupList);
        return "index";
    }

    @GetMapping(value = "/songInfo/{songId}")
    public @ResponseBody
    String provideSongModalInfo(@PathVariable("songId") int songId) throws Exception {
        Song song = songService.findById(songId).orElseThrow(() -> new ResourceNotFoundException("No song with id found " + songId));
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

    @GetMapping(value = "/song/{songId}")
    public String provideSongInfo(Model model, @PathVariable("songId") String songId) throws Exception {
        Song song = songService.findById(Integer.valueOf(songId)).orElseThrow(() -> new ResourceNotFoundException("No song found with id " + songId));
        model.addAttribute("appName", song.getOfficialDisplayBand()
                + " - " + song.getOfficialDisplayTitle() + " - " + appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        model.addAttribute("songToCheck", song);
        model.addAttribute("songUsages", songSubgroupService.findBySong(song));
        return "index";
    }

    @GetMapping(value = "/author/{authorId}")
    public String readAuthor(Model model, @PathVariable("authorId") int authorId) throws Exception {
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

    @GetMapping(value = "/genre/{genreId}")
    public String readGenreInfo(Model model, @PathVariable("genreId") int genreId) throws Exception {
        Genre genre =
                genreService.findById(genreId).orElseThrow(() -> new ResourceNotFoundException("No genre found with id " + genreId));
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

    @GetMapping(value = "/genre/readfull/{genreId}")
    public String readGenreInfoFull(Model model, @PathVariable("genreId") int genreId) throws Exception {
        Genre genre =
                genreService.findById(genreId).orElseThrow(() -> new ResourceNotFoundException("No genre found with id " + genreId));
        List<SongGenre> songGenreList = songGenreService.findByGenre(genre);
        List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
        List<SongSubgroup> songSubgroupList = songSubgroupService.findBySongInSortedByIdAsc(songs);
        model.addAttribute("songSubgroupList", songSubgroupList);
        model.addAttribute("genre", genre);
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        return "index";
    }
}
