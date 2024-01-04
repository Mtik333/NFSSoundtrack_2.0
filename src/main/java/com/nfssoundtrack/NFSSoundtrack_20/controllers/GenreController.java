package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.others.GenreService;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GenreRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongGenreRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(path = "/genre")
public class GenreController {

    @Value("${spring.application.name}")
    String appName;
    @Autowired
    private GenreService genreService;
    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    GenreRepository genreRepository;
    @Autowired
    SongGenreRepository songGenreRepository;

    @Autowired
    SongSubgroupRepository songSubgroupRepository;

    @GetMapping(value = "/readfull/{genreId}")
    public String readGenreInfoFull(Model model, @PathVariable("genreId") String genreId) throws JsonProcessingException {
        Genre genre = genreRepository.findById(Integer.valueOf(genreId)).get();
        List<SongGenre> songGenreList = songGenreRepository.findByGenre(genre);
        List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
        List<SongSubgroup> songSubgroupList = songSubgroupRepository.findBySongIn(songs, Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("songSubgroupList", songSubgroupList);
        model.addAttribute("genre", genre);
        model.addAttribute("author", null);
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        return "index";
    }

    @GetMapping(value = "/read/{genreId}")
    public String readGenreInfo(Model model, @PathVariable("genreId") String genreId) throws JsonProcessingException {
        Genre genre = genreRepository.findById(Integer.valueOf(genreId)).get();
        List<SongGenre> songGenreList = songGenreRepository.findByGenre(genre, Pageable.ofSize(50));
        List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
        List<SongSubgroup> songSubgroupList = songSubgroupRepository.findBySongIn(songs, Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("songSubgroupList", songSubgroupList);
        model.addAttribute("genre", genre);
        model.addAttribute("readFull", true);
        model.addAttribute("author", null);
        model.addAttribute("customPlaylist", null);
        model.addAttribute("appName", genre.getGenreName() + " - " + appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        return "index";
    }
}