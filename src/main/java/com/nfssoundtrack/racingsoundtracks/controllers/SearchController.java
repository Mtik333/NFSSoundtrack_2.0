package com.nfssoundtrack.racingsoundtracks.controllers;

import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.login.LoginException;
import java.util.*;

/**
 * controller used for handling search done by users
 * so this is the basic view, not the admin panel stuff
 */
@Controller
@RequestMapping(path = "/search")
public class SearchController  {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final BaseControllerWithErrorHandling baseController;

    public SearchController(BaseControllerWithErrorHandling baseController) {
        this.baseController = baseController;
    }

    @Value("${spring.application.name}")
    String appName;

    /**
     * at this point we just search for the information in the database
     * we can search for author (band), song title, lyrics
     * todo make search interpret input like: "artist:Something" for artist, song title or lyrics
     *
     * @param model      just a spring model to put results to for thymeleaf
     * @param searchData string without any particular length check
     * @return index.html with several response parameters to the frontend
     */
    @GetMapping(value = "/basic")
    public String searchStuff(Model model, @RequestParam("searchData") String searchData) throws LoginException, ResourceNotFoundException, InterruptedException {
        List<AuthorAlias> authorAliases = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();
        Map<Song, Set<Game>> songTitleList = new HashMap<>();
        Map<Song, Set<Game>> songLyricsList = new HashMap<>();
        List<Genre> genreList = new ArrayList<>();
        String query = searchData.trim();
        if (searchData.isEmpty()) {
            //someone can really try to search foe nothing
            logger.debug("well...");
        } else if (searchData.length() <= 3) {
            //treat as exact input as makes no sense to look for something having 3 or less characters in so many tables
            Optional<AuthorAlias> authorAlias = baseController.getAuthorAliasService().findByAlias(query);
            authorAlias.ifPresent(authorAliases::add);
            Optional<Genre> genre = baseController.getGenreService().findByGenreName(query);
            genre.ifPresent(genres::add);
            List<Song> foundSongs = baseController.getSongService().findByOfficialDisplayTitle(query);
            for (Song song : foundSongs) {
                List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySong(song);
                Set<Game> games = new HashSet<>();
                //we want to show all games (in search results) where this song is actually used
                for (SongSubgroup songSubgroup : songSubgroupList) {
                    games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                }
                songTitleList.put(song, games);
            }
            //extremely unlikely to have lyrics with only 3 letters
            //todo check if we should really look for any lyrics here
            List<Song> foundLyrics = baseController.getSongService().findByLyrics(query);
            for (Song song : foundLyrics) {
                List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySong(song);
                Set<Game> games = new HashSet<>();
                //in case of lyrics we also collect all games for found song
                for (SongSubgroup songSubgroup : songSubgroupList) {
                    games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                }
                songLyricsList.put(song, games);
            }
        } else {
            //it can be that someone puts quotation marks - in this case we do exact search
            //of stuffin quotation marks
            boolean fullPhraseSearch = query.contains("\"");
            if (fullPhraseSearch) {
                query = query.replace("\"", "").trim();
                Optional<AuthorAlias> authorAlias = baseController.getAuthorAliasService().findByAlias(query);
                if (authorAlias.isPresent()) {
                    authorAliases.add(authorAlias.get());
                }
                Optional<Genre> genre = baseController.getGenreService().findByGenreName(query);
                genre.ifPresent(genres::add);
                List<Song> foundSongs = baseController.getSongService().findByOfficialDisplayTitle(query);
                for (Song song : foundSongs) {
                    List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySong(song);
                    Set<Game> games = new HashSet<>();
                    //we want to show all games (in search results) where this song is actually used
                    for (SongSubgroup songSubgroup : songSubgroupList) {
                        games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                    }
                    songTitleList.put(song, games);
                }
                List<Song> foundLyrics = baseController.getSongService().findByLyrics(query);
                for (Song song : foundLyrics) {
                    List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySong(song);
                    Set<Game> games = new HashSet<>();
                    for (SongSubgroup songSubgroup : songSubgroupList) {
                        games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                    }
                    songLyricsList.put(song, games);
                }
                baseController.getGenreService().findByGenreName(query).ifPresent(genreList::add);
            } else {
                //otherwise we will be doing the contains-search for input that user provided
                authorAliases = baseController.getAuthorAliasService().findByAliasContains(query);
                genreList = baseController.getGenreService().findByGenreNameContains(query);
                List<Song> foundSongs = baseController.getSongService().findByOfficialDisplayTitleContains(query);
                for (Song song : foundSongs) {
                    List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySong(song);
                    Set<Game> games = new HashSet<>();
                    //we want to show all games (in search results) where this song is actually used
                    for (SongSubgroup songSubgroup : songSubgroupList) {
                        games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                    }
                    songTitleList.put(song, games);
                }
                List<Song> foundLyrics = baseController.getSongService().findByLyricsContains(query);
                for (Song song : foundLyrics) {
                    List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySong(song);
                    Set<Game> games = new HashSet<>();
                    for (SongSubgroup songSubgroup : songSubgroupList) {
                        games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                    }
                    songLyricsList.put(song, games);
                }
            }
        }
        model.addAttribute("appName", baseController.getLocalizedMessage("searchResultsAt", new String[]{appName}));
        model.addAttribute(WebsiteViewsController.SERIES, baseController.getSerieService().findAllSortedByPositionAsc());
        model.addAttribute(WebsiteViewsController.GAMES_ALPHA, baseController.getGameService().findAllSortedByDisplayTitleAsc());
        model.addAttribute("todayssong", baseController.getTodaysSongService().getTodaysSong());
        model.addAttribute("authorAliases", authorAliases);
        model.addAttribute("songTitleList", songTitleList);
        model.addAttribute("songLyricsList", songLyricsList);
        model.addAttribute("genreList", genreList);
        model.addAttribute("search", true);
        model.addAttribute("searchPhrase", searchData);
        model.addAttribute("translations", WebsiteViewsController.translationObjs);
        return "index";
    }
}
