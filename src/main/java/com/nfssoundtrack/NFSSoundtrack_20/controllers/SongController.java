package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorSongRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongGenreRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        for (SongGenre songGenre : songGenresFromMerge) {
            Optional<SongGenre> songGenreDuplicate = songGenresFromTarget.stream().filter(songGenre1
                    -> songGenre1.getGenre().equals(songGenre.getGenre())).findFirst();
            if (songGenreDuplicate.isPresent()) {
                songGenresToDelete.add(songGenre);
            } else {
                songGenre.setSong(targetSong);
            }
        }
        songGenreRepository.saveAll(songGenresFromMerge);
        songGenreRepository.deleteAll(songGenresToDelete);
        List<SongSubgroup> songSubgroupList = songSubgroupRepository.findBySong(songToMerge);
        for (SongSubgroup songSubgroup : songSubgroupList) {
            songSubgroup.setSong(targetSong);
        }
        songSubgroupRepository.saveAll(songSubgroupList);
        List<AuthorSong> authorSongs = authorSongRepository.findBySong(songToMerge);
        authorSongRepository.deleteAll(authorSongs);
        songRepository.delete(songToMerge);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/findExact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String findSong(@RequestBody String formData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        if (formData.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        String band = (String) mergeInfo.get("band");
        String title = (String) mergeInfo.get("title");
        List<Song> matchingSongs = songRepository.findByOfficialDisplayBandAndOfficialDisplayTitle(band, title);
        if (!matchingSongs.isEmpty()) {
            return objectMapper.writeValueAsString(matchingSongs.get(0));
        } else {
            return objectMapper.writeValueAsString(null);
        }
    }
}
