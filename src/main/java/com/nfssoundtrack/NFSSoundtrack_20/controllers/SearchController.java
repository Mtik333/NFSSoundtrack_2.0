package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/search")
public class SearchController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Value("${spring.application.name}")
    String appName;

    @GetMapping(value = "/basic")
    public String searchStuff(Model model, @RequestParam("searchData") String searchData) {
        List<AuthorAlias> authorAliases = new ArrayList<>();
        List<Song> songTitleList = new ArrayList<>();
        List<Song> songLyricsList = new ArrayList<>();
        List<Genre> genreList = new ArrayList<>();
        String query = searchData.trim();
        if (searchData.isEmpty()) {
            System.out.println("well...");
        } else if (searchData.length() <= 3) {
            //treat as exact input
            Optional<AuthorAlias> authorAlias = authorAliasService.findByAlias(query);
            authorAlias.ifPresent(authorAliases::add);
            songTitleList = songService.findByOfficialDisplayTitle(query);
            songLyricsList = songService.findByLyrics(query);
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
                songTitleList = songService.findByOfficialDisplayTitle(query);
                songLyricsList = songService.findByLyrics(query);
                genreService.findByGenreName(query).ifPresent(genreList::add);

            } else {
                authorAliases = authorAliasService.findByAliasContains(query);
                songTitleList = songService.findByOfficialDisplayTitleContains(query);
                songLyricsList = songService.findByLyricsContains(query);
                genreList = genreService.findByGenreNameContains(query);
            }
        }
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        model.addAttribute("authorAliases", authorAliases);
        model.addAttribute("songTitleList", songTitleList);
        model.addAttribute("songLyricsList", songLyricsList);
        model.addAttribute("genreList", genreList);
        model.addAttribute("search", true);
        model.addAttribute("searchPhrase", searchData);
        return "index";
    }
}
