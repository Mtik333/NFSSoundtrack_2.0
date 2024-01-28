package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subgroup")
public class SubgroupController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(SubgroupController.class);

    @GetMapping(value = "/read/{subgroupId}")
    public @ResponseBody
    String subGroupManage(@PathVariable("subgroupId") int subgroupId) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Subgroup subgroup = subgroupService.findById(subgroupId) .orElseThrow(() ->
                new ResourceNotFoundException("no subgroup with id found " + subgroupId));
        return objectMapper.writeValueAsString(subgroup);
    }

    @PutMapping(value = "/put/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSubgroup(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData)
            throws Exception {
        System.out.println("???");
        Subgroup subgroup = subgroupService.findById(subgroupId)
                .orElseThrow(() -> new ResourceNotFoundException("No subgroup with id found " + subgroupId));
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        List<String> songsToAssign = new ArrayList<>();
        List<String> songsToDetach = new ArrayList<>();
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            String songId = String.valueOf(linkedHashMap.get("song_id"));
            String state = String.valueOf(linkedHashMap.get("state"));
            if (state.equals("ADD")) {
                songsToAssign.add(songId);
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
                songSubgroupService.delete(subgroupOptional.get());
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
        for (String song : songsToAssign) {
            position += 10;
            Song song1 = songService.findById(Integer.valueOf(song))
                    .orElseThrow(() -> new ResourceNotFoundException("No song with id found " + song));
            List<SongSubgroup> existingSubgroups = songSubgroupService.findBySong(song1);
            SongSubgroup originalSongSubgroup = existingSubgroups.stream().filter(songSubgroup ->
                            songSubgroup.getSubgroup().getMainGroup().getGame().equals(subgroup.getMainGroup().getGame()))
                    .findFirst().orElseThrow(
                            () -> new ResourceNotFoundException("THere should be original subgroup here but not found"));
            SongSubgroup songSubgroup = new SongSubgroup(originalSongSubgroup);
            songSubgroup.setPosition((long) position);
            songSubgroup.setSubgroup(subgroup);
            songSubgroup.setSong(song1);
            songSubgroupService.save(songSubgroup);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/readAllSubgroup/{gameId}")
    public @ResponseBody
    String readAllSubgroupManage(@PathVariable("gameId") int gameId) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("No game found with id " + gameId));
        List<MainGroup> mainGroups = game.getMainGroups();
        for (MainGroup mainGroup : mainGroups) {
            if (mainGroup.getGroupName().contentEquals("All")){
                return objectMapper.writeValueAsString(mainGroup.getSubgroups().get(0));
            }
        }
        throw new Exception("you sudnt come here");
    }

}
