package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorAliasRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (searchData.isEmpty()) {
            System.out.println("well...");
        } else if (searchData.length() <= 3) {
            //treat as exact input
            Optional<AuthorAlias> authorAlias = authorAliasService.findByAlias(searchData.trim());
            if (authorAlias.isPresent()) {
                authorAliases.add(authorAlias.get());
            }
            songTitleList = songService.findByOfficialDisplayTitle(searchData.trim());
            songLyricsList = songService.findByLyrics(searchData.trim());
        } else {
            boolean fullPhraseSearch = searchData.contains("\"");
            if (fullPhraseSearch) {
                searchData = searchData.replaceAll("\"", "");
                Optional<AuthorAlias> authorAlias = authorAliasService.findByAlias(searchData.trim());
                if (authorAlias.isPresent()) {
                    authorAliases.add(authorAlias.get());
                }
                songTitleList = songService.findByOfficialDisplayTitle(searchData.trim());
                songLyricsList = songService.findByLyrics(searchData.trim());
            } else {
                authorAliases = authorAliasService.findByAliasContains(searchData.trim());
                songTitleList = songService.findByOfficialDisplayTitleContains(searchData.trim());
                songLyricsList = songService.findByLyricsContains(searchData.trim());
            }
        }
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieService.findAllSortedByPositionAsc());
        model.addAttribute("authorAliases", authorAliases);
        model.addAttribute("songTitleList", songTitleList);
        model.addAttribute("songLyricsList", songLyricsList);
        model.addAttribute("search", true);
        model.addAttribute("searchPhrase", searchData);
        return "index";
    }
}
