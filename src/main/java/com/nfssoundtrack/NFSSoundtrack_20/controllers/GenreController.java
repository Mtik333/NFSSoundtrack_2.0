package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.others.GenreService;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;

@Controller
@RequestMapping(path="/genre")
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
    @GetMapping(value = "/read/{genreId}")
    public String readGenreInfo(Model model,
                                              @PathVariable("genreId") String genreId,
                                              @RequestParam(required = false, name = "page") Optional<Integer> page,
                                              @RequestParam(required = false, name = "size") Optional<Integer> size) throws JsonProcessingException {
        Genre genre = genreRepository.findById(Integer.valueOf(genreId)).get();
        List<SongGenre> songGenreList = songGenreRepository.findByGenre(genre);
        List<Song> songs = songGenreList.stream().map(SongGenre::getSong).toList();
        List<SongSubgroup> songSubgroupList = songSubgroupRepository.findBySongIn(songs, Sort.by(Sort.Direction.ASC,"id"));
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(15);
        Page<SongSubgroup> genrePage = genreService.findPaginated(PageRequest.of(currentPage - 1, pageSize),songSubgroupList);
        model.addAttribute("genrePage", genrePage);
        int totalPages = genrePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("genre", genre);
        model.addAttribute("author", null);
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        return "index";
    }
}