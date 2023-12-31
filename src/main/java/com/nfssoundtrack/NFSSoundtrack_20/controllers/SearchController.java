package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorAliasRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/search")
public class SearchController {

    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AuthorAliasRepository authorAliasRepository;
    @GetMapping(value = "/basic")
    public String searchStuff(Model model, @RequestParam("searchData") String searchData) {
        List<AuthorAlias> authorAliases = new ArrayList<>();
        List<Song> songTitleList = new ArrayList<>();
        List<Song> songLyricsList = new ArrayList<>();
        boolean search = false;
        if (searchData.isEmpty()){
            System.out.println("well...");
        }
        else if (searchData.length()<=3){
            //treat as exact input
            AuthorAlias authorAlias = authorAliasRepository.findByAlias(searchData.trim());
            if (authorAlias!=null){
                authorAliases.add(authorAlias);
            }
            songTitleList = songRepository.findByOfficialDisplayTitle(searchData.trim());
            songLyricsList = songRepository.findByLyrics(searchData.trim());
        } else {
            boolean fullPhraseSearch = searchData.contains("\"");
            if (fullPhraseSearch){
                searchData = searchData.replaceAll("\"","");
                AuthorAlias authorAlias = authorAliasRepository.findByAlias(searchData.trim());
                if (authorAlias!=null){
                    authorAliases.add(authorAlias);
                }
                songTitleList = songRepository.findByOfficialDisplayTitle(searchData.trim());
                songLyricsList = songRepository.findByLyrics(searchData.trim());
            } else {
                authorAliases = authorAliasRepository.findByAliasContains(searchData.trim());
                songTitleList = songRepository.findByOfficialDisplayTitleContains(searchData.trim());
                songLyricsList = songRepository.findByLyricsContains(searchData.trim());
            }
        }
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        model.addAttribute("songSubgroups",null);
        model.addAttribute("author",null);
        model.addAttribute("authorAliases",authorAliases);
        model.addAttribute("songTitleList",songTitleList);
        model.addAttribute("songLyricsList",songLyricsList);
        model.addAttribute("search",true);
        model.addAttribute("searchPhrase",searchData);
        return "index";
    }
}
