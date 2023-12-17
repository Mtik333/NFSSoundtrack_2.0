package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(path="/all")
public class AllController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorAliasRepository authorAliasRepository;

    @Autowired
    private AuthorCountryRepository authorCountryRepository;

    @Autowired
    private AuthorSongRepository authorSongRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MainGroupRepository groupRepository;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @Autowired
    private SubgroupRepository subgroupRepository;


    @GetMapping(path="/allauthor")
    public @ResponseBody List<Author> getAllAuthors() {
        // This returns a JSON or XML with the users
        return authorRepository.findAll();
    }

    @GetMapping(path="/allauthoralias")
    public @ResponseBody List<AuthorAlias> getAllAuthorAlias() {
        // This returns a JSON or XML with the users
        return authorAliasRepository.findAll();
    }

    @GetMapping(path="/allauthorcountry")
    public @ResponseBody List<AuthorCountry> getAllAuthorCountry() {
        // This returns a JSON or XML with the users
        return authorCountryRepository.findAll();
    }

    @GetMapping(path="/allauthorsong")
    public ResponseEntity<List<AuthorSong>> getAllAuthorSong() {
        // This returns a JSON or XML with the users
        return new ResponseEntity<>(authorSongRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping(path="/allgame")
    public @ResponseBody List<Game> getAllGame() {
        // This returns a JSON or XML with the users
        return gameRepository.findAll();
    }

    @GetMapping(path="/allgroup")
    public @ResponseBody List<MainGroup> getAllGroup() {
        // This returns a JSON or XML with the users
        return groupRepository.findAll();
    }

    @GetMapping(path="/allserie")
    public @ResponseBody List<Serie> getAllSerie() {
        // This returns a JSON or XML with the users
        return serieRepository.findAll();
    }

    @GetMapping(path="/allsong")
    public @ResponseBody List<Song> getAllSong() {
        // This returns a JSON or XML with the users
        return songRepository.findAll();
    }

    @GetMapping(path="/allsongsubgroup")
    public @ResponseBody List<SongSubgroup> getAllSongSubgroup() {
        // This returns a JSON or XML with the users
        return songSubgroupRepository.findAll();
    }

    @GetMapping(path="/allsubgroup")
    public @ResponseBody List<Subgroup> getAllSubgroup() {
        // This returns a JSON or XML with the users
        return subgroupRepository.findAll();
    }
}
