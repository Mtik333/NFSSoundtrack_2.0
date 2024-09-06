package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * controller for handling subgroups, mainly reading info and updating it
 * used in songsMgmt.js, subgroupMgmt.js
 */
@Controller
@RequestMapping("/subgroup")
public class SubgroupController extends BaseControllerWithErrorHandling {

    /**
     * method to show all songs from the subgroup
     * you can see it triggered when you click on 'manage groups' and just select the subgroup from dropdown
     *
     * @param subgroupId id of subgroup to show
     * @return json of whole subgroup including games
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/read/{subgroupId}")
    public @ResponseBody
    String subGroupManage(@PathVariable("subgroupId") int subgroupId)
            throws ResourceNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Subgroup subgroup = subgroupService.findById(subgroupId).orElseThrow(() ->
                new ResourceNotFoundException("no subgroup with id found " + subgroupId));
        return objectMapper.writeValueAsString(subgroup);
    }

    /**
     * method used to update the subgroup used in subgroupMgmt.js
     * happens when you click on "update subgroup"
     *
     * @param subgroupId id of subgroup
     * @param formData   basically info about the song, song-subgroup entry and whether
     *                   it is added or deleted from subgroup
     * @return OK if successful
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @PutMapping(value = "/put/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSubgroup(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData)
            throws ResourceNotFoundException, JsonProcessingException {
        Subgroup subgroup = subgroupService.findById(subgroupId)
                .orElseThrow(() -> new ResourceNotFoundException("No subgroup with id found " + subgroupId));
        //so we get the subgroup
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        Map<String, String> songsToAssign = new HashMap<>();
        List<String> songsToDetach = new ArrayList<>();
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            String songId = String.valueOf(linkedHashMap.get("song_id"));
            //i want to get exact song subgroup, for example because i want to put outro version in other subgroup
            String songSubgroupId = String.valueOf(linkedHashMap.get("songsubgroup_id"));
            String state = String.valueOf(linkedHashMap.get("state"));
            //filling map of songs to add / delete to subgroup
            if (state.equals("ADD")) {
                songsToAssign.put(songId, songSubgroupId);
            } else if (state.equals("DELETE")) {
                songsToDetach.add(songId);
            }
        }
        //first we will go through songs that we want to remove from subgroup
        List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
        for (String song : songsToDetach) {
            //we will have id of song to detach in database song subgroup list
            Optional<SongSubgroup> subgroupOptional = songSubgroupList.stream().filter(
                    songSubgroup -> String.valueOf(songSubgroup.getSong().getId()).equals(song)).findFirst();
            if (subgroupOptional.isPresent()) {
                Song mySong = subgroupOptional.get().getSong();
                SongSubgroup songSubgroup = subgroupOptional.get();
                //if we remove song from subgroup but this specific one was used as today song
                //then either we link other usage of this song or just replace it with some other song
                JustSomeHelper.unlinkSongWithTodaysSong(todaysSongService, songSubgroup, mySong, songSubgroupService);
                JustSomeHelper.unlinkSongWithCorrection(correctionService, songSubgroup,
                        "; deleted song-subgroup: " + songSubgroup.toCorrectionString());
                //we are then safe to delete song-subgroup association
                songSubgroupService.delete(songSubgroup);
                //delete orphaned stuff - maybe song is not used anywhere else?
                List<SongSubgroup> orphanedSong = songSubgroupService.findBySong(mySong);
                if (orphanedSong.isEmpty()) {
                    List<SongGenre> songGenres = songGenreService.findBySong(mySong);
                    //if that's the case, we scrap song-genre, author-song associations
                    songGenreService.deleteAll(songGenres);
                    List<AuthorSong> authorSongs = authorSongService.findBySong(mySong);
                    authorSongService.deleteAll(authorSongs);
                    songService.delete(mySong);
                }
            }
        }
        //we will put position of new song in subgroup based on how many songs we are trying to remove / add
        int position = 10 + (10 * songsToDetach.size());
        for (Map.Entry<String, String> song : songsToAssign.entrySet()) {
            position += 10;
            Song song1 = songService.findById(Integer.valueOf(song.getKey()))
                    .orElseThrow(() -> new ResourceNotFoundException("No song with id found " + song));
            //using logic to create new songsubgroup based on details of existing songsubgroup
            String songSubgroupId = song.getValue();
            //our getvalue is basically song-subgroup of song we associate with this subgroup
            if (songSubgroupId != null) {
                SongSubgroup optionalSongSubgroup = songSubgroupService.findById(Integer.valueOf(songSubgroupId))
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No songSubgroupId with id found " + songSubgroupId));
                SongSubgroup songSubgroup = new SongSubgroup(optionalSongSubgroup);
                songSubgroup.setPosition((long) position);
                songSubgroup.setSubgroup(subgroup);
                songSubgroup.setSong(song1);
                songSubgroupService.save(songSubgroup);
            } else {
                //i feel like this is not used at all
                List<SongSubgroup> existingSubgroups = songSubgroupService.findBySong(song1);
                SongSubgroup originalSongSubgroup = existingSubgroups.stream().filter(songSubgroup1 ->
                                songSubgroup1.getSubgroup().getMainGroup().getGame().equals(subgroup.getMainGroup().getGame()))
                        .findFirst().orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "THere should be original subgroup here but not found"));
                SongSubgroup songSubgroup = new SongSubgroup(originalSongSubgroup);
                songSubgroup.setPosition((long) position);
                songSubgroup.setSubgroup(subgroup);
                songSubgroup.setSong(song1);
                songSubgroupService.save(songSubgroup);
            }
        }
        //again cleaning the cache of game when updating subgroup
        String gameShort = subgroup.getMainGroup().getGame().getGameShort();
        removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method used for moving subgroup from one group to another
     * used in subgroupMgmt.js, you can click on 'move subgroup to group' after clicking 'manage subgroups'
     *
     * @param formData it is just id of subgroup and main group to move this subgroup to
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/moveSubgroup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String moveSubgroupToOtherGroup(@RequestBody String formData)
            throws JsonProcessingException, ResourceNotFoundException {
        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) new ObjectMapper().readValue(formData, Map.class);
        long subgroupId = Long.parseLong(String.valueOf(linkedHashMap.get("subgroupId")));
        long groupId = Long.parseLong(String.valueOf(linkedHashMap.get("targetGroupId")));
        MainGroup mainGroup = mainGroupService.findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new ResourceNotFoundException("No mainGroup found with id " + groupId));
        Subgroup subgroup = subgroupService.findById(Math.toIntExact(subgroupId))
                .orElseThrow(() -> new ResourceNotFoundException("No subgroup found with id " + subgroupId));
        //nothing special here, we use method to change main group
        subgroup.setMainGroup(mainGroup);
        subgroupService.save(subgroup);
        //need to clean cache of game after moving subgroup
        String gameShort = mainGroup.getGame().getGameShort();
        removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

}
