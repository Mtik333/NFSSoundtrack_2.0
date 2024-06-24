package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping(path = "/search")
public class SearchController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Value("${spring.application.name}")
    String appName;

    @GetMapping(value = "/basic")
    public String searchStuff(Model model, @RequestParam("searchData") String searchData) {
        List<AuthorAlias> authorAliases = new ArrayList<>();
        Map<Song,Set<Game>> songTitleList = new HashMap<>();
        Map<Song,Set<Game>> songLyricsList = new HashMap<>();
        List<Genre> genreList = new ArrayList<>();
        String query = searchData.trim();
        if (searchData.isEmpty()) {
            System.out.println("well...");
        } else if (searchData.length() <= 3) {
            //treat as exact input
            Optional<AuthorAlias> authorAlias = authorAliasService.findByAlias(query);
            authorAlias.ifPresent(authorAliases::add);
            List<Song> foundSongs = songService.findByOfficialDisplayTitle(query);
            for (Song song : foundSongs){
                List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(song);
                Set<Game> games = new HashSet<>();
                for (SongSubgroup songSubgroup : songSubgroupList){
                    games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                }
                songTitleList.put(song,games);
            }
            List<Song> foundLyrics = songService.findByLyrics(query);
            for (Song song : foundLyrics){
                List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(song);
                Set<Game> games = new HashSet<>();
                for (SongSubgroup songSubgroup : songSubgroupList){
                    games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                }
                songLyricsList.put(song,games);
            }
            Optional<Genre> genre = genreService.findByGenreName(query);
            genre.ifPresent(genreList::add);
        } else {
            boolean fullPhraseSearch = query.contains("\"");
            if (fullPhraseSearch) {
                query = query.replaceAll("\"", "").trim();
                Optional<AuthorAlias> authorAlias = authorAliasService.findByAlias(query);
                if (authorAlias.isPresent()) {
                    authorAliases.add(authorAlias.get());
                }
                List<Song> foundSongs = songService.findByOfficialDisplayTitle(query);
                for (Song song : foundSongs){
                    List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(song);
                    Set<Game> games = new HashSet<>();
                    for (SongSubgroup songSubgroup : songSubgroupList){
                        games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                    }
                    songTitleList.put(song,games);
                }
                List<Song> foundLyrics = songService.findByLyrics(query);
                for (Song song : foundLyrics){
                    List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(song);
                    Set<Game> games = new HashSet<>();
                    for (SongSubgroup songSubgroup : songSubgroupList){
                        games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                    }
                    songLyricsList.put(song,games);
                }
                genreService.findByGenreName(query).ifPresent(genreList::add);
            } else {
                authorAliases = authorAliasService.findByAliasContains(query);
                List<Song> foundSongs = songService.findByOfficialDisplayTitleContains(query);
                for (Song song : foundSongs){
                    List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(song);
                    Set<Game> games = new HashSet<>();
                    for (SongSubgroup songSubgroup : songSubgroupList){
                        games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                    }
                    songTitleList.put(song,games);
                }
                List<Song> foundLyrics = songService.findByLyricsContains(query);
                for (Song song : foundLyrics){
                    List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(song);
                    Set<Game> games = new HashSet<>();
                    for (SongSubgroup songSubgroup : songSubgroupList){
                        games.add(songSubgroup.getSubgroup().getMainGroup().getGame());
                    }
                    songLyricsList.put(song,games);
                }
                genreList = genreService.findByGenreNameContains(query);
            }
        }
        model.addAttribute("appName", "Search results at " + appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        model.addAttribute("authorAliases", authorAliases);
        model.addAttribute("songTitleList", songTitleList);
        model.addAttribute("songLyricsList", songLyricsList);
        model.addAttribute("genreList", genreList);
        model.addAttribute("search", true);
        model.addAttribute("searchPhrase", searchData);
        return "min/index";
    }
}
