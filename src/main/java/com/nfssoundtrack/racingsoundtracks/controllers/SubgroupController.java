package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/subgroup")
public class SubgroupController extends BaseControllerWithErrorHandling {

    @GetMapping(value = "/read/{subgroupId}")
    public @ResponseBody
    String subGroupManage(@PathVariable("subgroupId") int subgroupId) throws ResourceNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Subgroup subgroup = subgroupService.findById(subgroupId).orElseThrow(() ->
                new ResourceNotFoundException("no subgroup with id found " + subgroupId));
        return objectMapper.writeValueAsString(subgroup);
    }

    @PutMapping(value = "/put/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSubgroup(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData)
            throws ResourceNotFoundException, JsonProcessingException {
        Subgroup subgroup = subgroupService.findById(subgroupId)
                .orElseThrow(() -> new ResourceNotFoundException("No subgroup with id found " + subgroupId));
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        Map<String, String> songsToAssign = new HashMap<>();
        List<String> songsToDetach = new ArrayList<>();
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            String songId = String.valueOf(linkedHashMap.get("song_id"));
            //i want to get exact song subgroup, for example because i want to put outro version in other subgroup
            String songSubgroupId = String.valueOf(linkedHashMap.get("songsubgroup_id"));
            String state = String.valueOf(linkedHashMap.get("state"));
            if (state.equals("ADD")) {
                songsToAssign.put(songId, songSubgroupId);
            } else if (state.equals("DELETE")) {
                songsToDetach.add(songId);
            }
        }
        List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
        for (String song : songsToDetach) {
            Optional<SongSubgroup> subgroupOptional = songSubgroupList.stream().filter(
                    songSubgroup -> String.valueOf(songSubgroup.getSong().getId()).equals(song)).findFirst();
            if (subgroupOptional.isPresent()) {
                Song mySong = subgroupOptional.get().getSong();
                SongSubgroup songSubgroup = subgroupOptional.get();
                List<Correction> relatedCorrections = correctionService.findBySongSubgroup(songSubgroup);
                for (Correction correction : relatedCorrections) {
                    correction.setSongSubgroup(null);
                    correction.setCorrectValue(correction.getCorrectValue()
                            + "; deleted song-subgroup: " + songSubgroup.toCorrectionString());
                    correctionService.save(correction);
                }
                songSubgroupService.delete(songSubgroup);
                //delete orphaned stuff
                List<SongSubgroup> orphanedSong = songSubgroupService.findBySong(mySong);
                if (orphanedSong.isEmpty()) {
                    List<SongGenre> songGenres = songGenreService.findBySong(mySong);
                    songGenreService.deleteAll(songGenres);
                    List<AuthorSong> authorSongs = authorSongService.findBySong(mySong);
                    authorSongService.deleteAll(authorSongs);
                    songService.delete(mySong);
                }
            }
        }
        int position = 10 + (10 * songsToDetach.size());
        for (Map.Entry<String, String> song : songsToAssign.entrySet()) {
            position += 10;
            Song song1 = songService.findById(Integer.valueOf(song.getKey()))
                    .orElseThrow(() -> new ResourceNotFoundException("No song with id found " + song));
            //using logic to create new songsubgroup based on details of existing songsubgroup
            String songSubgroupId = song.getValue();
            if (songSubgroupId != null) {
                SongSubgroup optionalSongSubgroup = songSubgroupService.findById(Integer.valueOf(songSubgroupId))
                        .orElseThrow(() -> new ResourceNotFoundException("No songSubgroupId with id found " + songSubgroupId));
                SongSubgroup songSubgroup = new SongSubgroup(optionalSongSubgroup);
                songSubgroup.setPosition((long) position);
                songSubgroup.setSubgroup(subgroup);
                songSubgroup.setSong(song1);
                songSubgroupService.save(songSubgroup);
            } else {
                List<SongSubgroup> existingSubgroups = songSubgroupService.findBySong(song1);
                SongSubgroup originalSongSubgroup = existingSubgroups.stream().filter(songSubgroup1 ->
                                songSubgroup1.getSubgroup().getMainGroup().getGame().equals(subgroup.getMainGroup().getGame()))
                        .findFirst().orElseThrow(
                                () -> new ResourceNotFoundException("THere should be original subgroup here but not found"));
                SongSubgroup songSubgroup = new SongSubgroup(originalSongSubgroup);
                songSubgroup.setPosition((long) position);
                songSubgroup.setSubgroup(subgroup);
                songSubgroup.setSong(song1);
                songSubgroupService.save(songSubgroup);
            }
        }
        String gameShort = subgroup.getMainGroup().getGame().getGameShort();
        removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/readAllSubgroup/{gameId}")
    public @ResponseBody
    String readAllSubgroupManage(@PathVariable("gameId") int gameId) throws ResourceNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("No game found with id " + gameId));
        List<MainGroup> mainGroups = game.getMainGroups();
        for (MainGroup mainGroup : mainGroups) {
            if (mainGroup.getGroupName().contentEquals("All")) {
                return objectMapper.writeValueAsString(mainGroup.getSubgroups().get(0));
            }
        }
        throw new ResourceNotFoundException("you sudnt come here");
    }

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
        subgroup.setMainGroup(mainGroup);
        subgroupService.save(subgroup);
        String gameShort = mainGroup.getGame().getGameShort();
        removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

}
