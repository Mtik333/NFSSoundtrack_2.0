package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
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

/**
 * controller used to merge songs and find exact song when creating a new one
 */
@Controller
@RequestMapping(path = "/song")
public class SongController extends BaseControllerWithErrorHandling {

    /**
     * method used to merge one song with another
     * used in mergeSong.js when you click on 'save' in 'merge songs' module
     *
     * @param formData consists of ids of song to merge and target song to be merged into
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String mergeSongs(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        //so we are supposed to put id of two songs, target song being the one that remains after merge
        String idToMerge = (String) mergeInfo.get("songToMergeId");
        String targetId = (String) mergeInfo.get("targetSongId");
        //maybe we want to merge songs but keep the ingame title intact because that's how it is in the game
        Boolean addInGameTitle = (Boolean) mergeInfo.get("pushIngameTitle");
        Song songToMerge =
                songService.findById(Integer.valueOf(idToMerge)).orElseThrow(
                        () -> new ResourceNotFoundException("No song with id " +
                                "found " + idToMerge));
        Song targetSong = songService.findById(Integer.valueOf(targetId)).orElseThrow(
                () -> new ResourceNotFoundException("No song with id " +
                        "found " + idToMerge));
        String message = "Merging song " + songToMerge.toAnotherChangeLogString()
                + " into song " + targetSong.toAnotherChangeLogString();
        //aside from the songs found, we need to check the genres of both songs
        List<SongGenre> songGenresFromMerge = songGenreService.findBySong(songToMerge);
        List<SongGenre> songGenresFromTarget = songGenreService.findBySong(targetSong);
        List<SongGenre> songGenresToDelete = new ArrayList<>();
        for (SongGenre songGenre : songGenresFromMerge) {
            //we dont want to duplicate genres per song after the merge
            //so we would keep them in list and get rid of them later
            Optional<SongGenre> songGenreDuplicate = songGenresFromTarget.stream().filter(songGenre1
                    -> songGenre1.getGenre().equals(songGenre.getGenre())).findFirst();
            if (songGenreDuplicate.isPresent()) {
                songGenresToDelete.add(songGenre);
            } else {
                //for genres that are on to-merge song, we have to associate them with target song now
                songGenre.setSong(targetSong);
            }
        }
        songGenreService.saveAll(songGenresFromMerge);
        songGenreService.deleteAll(songGenresToDelete);
        List<SongSubgroup> songSubgroupList = songSubgroupService.findBySong(songToMerge);
        //now for each usage of song in a subgroup we will change association to the target song
        //and change the links too, inherit from target
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
            //as we merge song in a game, we clear cached info about songs in that game
            //so that when acessing the page, songs list will get updated
            removeCacheEntry(gameShort);
        }
        //now we are safe to update song-subgroup associatiosn and get rid of song completely
        songSubgroupService.saveAll(songSubgroupList);
        List<AuthorSong> authorSongs = authorSongService.findBySong(songToMerge);
        //song can be associated with 1 or more composers so we delete associations too
        authorSongService.deleteAll(authorSongs);
        songService.delete(songToMerge);
        sendMessageToChannel(EntityType.SONG, "update", message,
                EntityUrl.SONG, targetSong.toAnotherChangeLogString(), String.valueOf(targetSong.getId()));
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method used to find song that exists already in the database
     * used in songsMgmt.js when you type in on 'official title' when creating new song via 'manage songs'
     * when editing song globally, 'existing song id' will be there already
     * so you can imagine that we have a new game which suddenly uses song
     * that was in the database already, so we dont want to duplicate it
     * and this method is supposed to find such song and give back the id of song
     *
     * @param formData is just the name of artist / band and title of the song
     * @return json with song if such was found
     * @throws JsonProcessingException
     */
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
        //made this dedicated query to look for such song there
        //i dont think there would be 2 identical songs but maybe that could happen?
        List<Song> matchingSongs = songService.findByOfficialDisplayBandAndOfficialDisplayTitleContains(band, title);
        if (!matchingSongs.isEmpty()) {
            return objectMapper.writeValueAsString(matchingSongs.get(0));
        } else {
            return objectMapper.writeValueAsString(null);
        }
    }
}
