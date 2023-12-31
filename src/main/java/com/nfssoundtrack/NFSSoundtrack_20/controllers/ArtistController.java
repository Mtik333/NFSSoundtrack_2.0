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

import java.util.*;

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
        Map<String,List<SongSubgroup>> songsAsComposer = new HashMap<>();
        Map<String,List<SongSubgroup>> songsAsSubcomposer = new HashMap<>();
        Map<String,List<SongSubgroup>> songsAsFeat = new HashMap<>();
        Map<String,List<SongSubgroup>> songsRemixed = new HashMap<>();
        for (AuthorAlias authorAlias : allAliases){
           List<AuthorSong> allAuthorSongs = authorSongRepository.findByAuthorAlias(authorAlias);
           for (AuthorSong authorSong : allAuthorSongs){
               List<SongSubgroup> songSubgroupList = songSubgroupRepository.findBySong(authorSong.getSong());
               if (Role.COMPOSER.equals(authorSong.getRole())){
                   if (songsAsComposer.get(authorAlias.getAlias())==null){
                       songsAsComposer.put(authorAlias.getAlias(), new ArrayList<>(songSubgroupList));
                   } else {
                       songsAsComposer.get(authorAlias.getAlias()).addAll(songSubgroupList);
                   }
               }
               if (Role.SUBCOMPOSER.equals(authorSong.getRole())){
                   if (songsAsSubcomposer.get(authorAlias.getAlias())==null){
                       songsAsSubcomposer.put(authorAlias.getAlias(), new ArrayList<>(songSubgroupList));
                   } else {
                       songsAsSubcomposer.get(authorAlias.getAlias()).addAll(songSubgroupList);
                   }
               }
               if (Role.FEAT.equals(authorSong.getRole())){
                   if (songsAsFeat.get(authorAlias.getAlias())==null){
                       songsAsFeat.put(authorAlias.getAlias(), new ArrayList<>(songSubgroupList));
                   } else {
                       songsAsFeat.get(authorAlias.getAlias()).addAll(songSubgroupList);
                   }
               }
               if (Role.REMIX.equals(authorSong.getRole())){
                   if (songsRemixed.get(authorAlias.getAlias())==null){
                       songsRemixed.put(authorAlias.getAlias(), new ArrayList<>(songSubgroupList));
                   } else {
                       songsRemixed.get(authorAlias.getAlias()).addAll(songSubgroupList);
                   }
               }
           }
        }
        model.addAttribute("author", author);
        model.addAttribute("genre", null);
        model.addAttribute("songsAsComposer", songsAsComposer);
        model.addAttribute("songsAsSubcomposer", songsAsSubcomposer);
        model.addAttribute("songsAsFeat", songsAsFeat);
        model.addAttribute("songsRemixed", songsRemixed);
        model.addAttribute("allAliases", allAliases);
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        return "index";
    }

    @GetMapping(value="/authorAlias/{input}")
    public @ResponseBody String readAliasesFromArtist(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (input.isEmpty()){
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
        if (input.isEmpty()){
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
