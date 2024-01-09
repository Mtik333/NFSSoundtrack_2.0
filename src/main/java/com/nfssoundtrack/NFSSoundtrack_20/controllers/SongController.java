package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/song")
public class SongController {

    private static final Logger logger = LoggerFactory.getLogger(SerieController.class);
    @Autowired
    SongRepository songRepository;

    @Autowired
    AuthorSongRepository authorSongRepository;

    @Autowired
    SongGenreRepository songGenreRepository;
    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @PutMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String mergeSongs(@RequestBody String formData) throws JsonProcessingException {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        String idToMerge = (String) mergeInfo.get("songToMergeId");
        String targetId = (String) mergeInfo.get("targetSongId");
        Song songToMerge = songRepository.findById(Integer.valueOf(idToMerge)).get();
        Song targetSong = songRepository.findById(Integer.valueOf(targetId)).get();
        List<SongGenre> songGenresFromMerge = songGenreRepository.findBySong(songToMerge);
        List<SongGenre> songGenresFromTarget = songGenreRepository.findBySong(targetSong);
        List<SongGenre> songGenresToDelete = new ArrayList<>();
        for (SongGenre songGenre : songGenresFromMerge){
            Optional<SongGenre> songGenreDuplicate = songGenresFromTarget.stream().filter(songGenre1
                    -> songGenre1.getGenre().equals(songGenre.getGenre())).findFirst();
            if (songGenreDuplicate.isPresent()){
                songGenresToDelete.add(songGenre);
            } else {
                songGenre.setSong(targetSong);
            }
        }
        songGenreRepository.saveAll(songGenresFromMerge);
        songGenreRepository.deleteAll(songGenresToDelete);
        List<SongSubgroup> songSubgroupList = songSubgroupRepository.findBySong(songToMerge);
        for (SongSubgroup songSubgroup : songSubgroupList){
            songSubgroup.setSong(targetSong);
        }
        songSubgroupRepository.saveAll(songSubgroupList);
        List<AuthorSong> authorSongs = authorSongRepository.findBySong(songToMerge);
        authorSongRepository.deleteAll(authorSongs);
        songRepository.delete(songToMerge);
        return new ObjectMapper().writeValueAsString("OK");
    }
}
