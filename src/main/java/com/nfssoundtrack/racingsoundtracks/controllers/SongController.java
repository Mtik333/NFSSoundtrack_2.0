package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorSong;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongGenre;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
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
public class SongController extends BaseControllerWithErrorHandling {

    @PutMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String mergeSongs(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        String idToMerge = (String) mergeInfo.get("songToMergeId");
        String targetId = (String) mergeInfo.get("targetSongId");
        Boolean addInGameTitle = (Boolean) mergeInfo.get("pushIngameTitle");
        Song songToMerge =
                songService.findById(Integer.valueOf(idToMerge)).orElseThrow(() -> new ResourceNotFoundException("No song with id " +
                        "found " + idToMerge));
        Song targetSong = songService.findById(Integer.valueOf(targetId)).orElseThrow(
                () -> new ResourceNotFoundException("No song with id " +
                        "found " + idToMerge));
        List<SongGenre> songGenresFromMerge = songGenreService.findBySong(songToMerge);
        List<SongGenre> songGenresFromTarget = songGenreService.findBySong(targetSong);
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
        songGenreService.saveAll(songGenresFromMerge);
        songGenreService.deleteAll(songGenresToDelete);
        List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(songToMerge);
        for (SongSubgroup songSubgroup : songSubgroupList) {
            songSubgroup.setSong(targetSong);
            if (Boolean.TRUE.equals(addInGameTitle)) {
                songSubgroup.setIngameDisplayTitle(songToMerge.getOfficialDisplayTitle());
            }
            songSubgroup.setSpotifyId(targetSong.getSpotifyId());
            songSubgroup.setDeezerId(targetSong.getDeezerId());
            songSubgroup.setTidalLink(targetSong.getTidalLink());
            songSubgroup.setSoundcloudLink(targetSong.getSoundcloudLink());
            songSubgroup.setItunesLink(targetSong.getItunesLink());
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            removeCacheEntry(gameShort);
        }
        songSubgroupService.saveAll(songSubgroupList);
        List<AuthorSong> authorSongs = authorSongService.findBySong(songToMerge);
        authorSongService.deleteAll(authorSongs);
        songService.delete(songToMerge);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/findExact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String findSong(@RequestBody String formData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        if (formData.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        String band = (String) mergeInfo.get("band");
        String title = (String) mergeInfo.get("title");
        List<Song> matchingSongs = songService.findByOfficialDisplayBandAndOfficialDisplayTitleContains(band, title);
        if (!matchingSongs.isEmpty()) {
            return objectMapper.writeValueAsString(matchingSongs.get(0));
        } else {
            return objectMapper.writeValueAsString(null);
        }
    }
}
