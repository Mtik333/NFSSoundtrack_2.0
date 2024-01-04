package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.ArtistSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.others.AuthorAliasSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/author")
public class ArtistController {
    @Value("${spring.application.name}")
    String appName;
    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    AuthorSongRepository authorSongRepository;

    @Autowired
    AuthorAliasRepository authorAliasRepository;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @GetMapping(value = "/{authorId}")
    public String author(Model model, @PathVariable("authorId") String authorId) {
        Author author = authorRepository.findById(Integer.valueOf(authorId)).get();
        List<AuthorAlias> allAliases = authorAliasRepository.findByAuthor(author);
        authorSongRepository.findByAuthorAlias(allAliases.get(0));
        Map<String, Map<Song, List<SongSubgroup>>> songsAsComposer = new HashMap<>();
        Map<String, Map<Song, List<SongSubgroup>>> songsAsSubcomposer = new HashMap<>();
        Map<String, Map<Song, List<SongSubgroup>>> songsAsFeat = new HashMap<>();
        Map<String, Map<Song, List<SongSubgroup>>> songsRemixed = new HashMap<>();
        for (AuthorAlias authorAlias : allAliases) {
            List<AuthorSong> allAuthorSongs = authorSongRepository.findByAuthorAlias(authorAlias);
            for (AuthorSong authorSong : allAuthorSongs) {
                fillMapForArtistDisplay(authorAlias, authorSong, Role.COMPOSER, songsAsComposer);
                fillMapForArtistDisplay(authorAlias, authorSong, Role.SUBCOMPOSER, songsAsSubcomposer);
                fillMapForArtistDisplay(authorAlias, authorSong, Role.REMIX, songsAsFeat);
                fillMapForArtistDisplay(authorAlias, authorSong, Role.FEAT, songsRemixed);
            }
        }
        model.addAttribute("author", author);
        model.addAttribute("genre", null);
        model.addAttribute("customPlaylist", null);
        model.addAttribute("songsAsComposer", songsAsComposer);
        model.addAttribute("songsAsSubcomposer", songsAsSubcomposer);
        model.addAttribute("songsAsFeat", songsAsFeat);
        model.addAttribute("songsRemixed", songsRemixed);
        model.addAttribute("allAliases", allAliases);
        model.addAttribute("appName", author.getName() + " - " + appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        return "index";
    }

    private void fillMapForArtistDisplay(AuthorAlias authorAlias, AuthorSong authorSong, Role role,
                                         Map<String, Map<Song, List<SongSubgroup>>> songsAsComposer) {
        List<SongSubgroup> songSubgroupList = songSubgroupRepository.findBySong(authorSong.getSong());
        if (role.equals(authorSong.getRole())) {
            if (songsAsComposer.get(authorAlias.getAlias()) == null) {
                Map<Song, List<SongSubgroup>> songsPerSubgroup = new HashMap<>();
                songsPerSubgroup.put(authorSong.getSong(), songSubgroupList);
                songsAsComposer.put(authorAlias.getAlias(), songsPerSubgroup);
            } else {
                Map<Song, List<SongSubgroup>> songsPerSubgroup = songsAsComposer.get(authorAlias.getAlias());
                if (songsPerSubgroup.get(authorSong.getSong()) != null) {
                    songsPerSubgroup.get(authorSong.getSong()).addAll(songSubgroupList);
                } else {
                    songsPerSubgroup.put(authorSong.getSong(), songSubgroupList);
                }
                songsAsComposer.put(authorAlias.getAlias(), songsPerSubgroup);
            }
        }
    }

    @GetMapping(value = "/authorAlias/{input}")
    public @ResponseBody String readAliasesFromArtist(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString("[]");
        }
        SimpleModule simpleModule = new SimpleModule();
        AuthorSong authorSong = authorSongRepository.findById(Integer.valueOf(input)).get();
        Author author = authorSong.getAuthorAlias().getAuthor();
        List<AuthorAlias> authorAliases = authorAliasRepository.findByAuthor(author);
        simpleModule.addSerializer(AuthorAlias.class, new AuthorAliasSerializer(AuthorAlias.class));
        objectMapper.registerModule(simpleModule);
        String result = objectMapper.writeValueAsString(authorAliases);
        return result;
    }


    @GetMapping(value = "/aliasName/{input}")
    public @ResponseBody String readAliases(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString("[]");
        }
//        SimpleModule simpleModule = new SimpleModule();
//        AuthorSong authorSong = authorSongRepository.findById(Integer.valueOf(input)).get();
//        Author author = authorSong.getAuthorAlias().getAuthor();
//        List<AuthorAlias> authorAliases = authorAliasRepository.findByAuthor(author);
//        simpleModule.addSerializer(AuthorAlias.class, new AuthorAliasSerializer(AuthorAlias.class));
//        objectMapper.registerModule(simpleModule);
//        String result = objectMapper.writeValueAsString(authorAliases);
//        return result;
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(AuthorAlias.class, new AuthorAliasSerializer(AuthorAlias.class));
        objectMapper.registerModule(simpleModule);
        if (input.length() <= 3) {
            AuthorAlias authorList = authorAliasRepository.findByAlias(input);
            String result = objectMapper.writeValueAsString(Collections.singleton(authorList));
            return result;
        } else {
            List<AuthorAlias> authorList = authorAliasRepository.findByAliasContains(input);
            String result = objectMapper.writeValueAsString(authorList);
            return result;
        }
    }

    @GetMapping(value = "/authorName/{input}")
    public @ResponseBody String readSpecialArtists(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Author.class, new ArtistSerializer(Author.class));
        objectMapper.registerModule(simpleModule);
        if (input.length() <= 3) {
            Author authorList = authorRepository.findByName(input);
            String result = objectMapper.writeValueAsString(Collections.singleton(authorList));
            return result;
        } else {
            List<Author> authorList = authorRepository.findByNameContains(input);
            String result = objectMapper.writeValueAsString(authorList);
            return result;
        }
    }
}
