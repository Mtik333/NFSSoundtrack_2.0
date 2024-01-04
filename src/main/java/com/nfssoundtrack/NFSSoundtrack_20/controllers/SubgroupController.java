package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subgroup")
public class SubgroupController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @Autowired
    private SubgroupRepository subgroupRepository;

    @Autowired
    private SongRepository songRepository;

    @GetMapping(value = "/read/{gameId}")
    public @ResponseBody String subGroupManage(Model model, @PathVariable("gameId") String gameId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game game = gameRepository.findById(Integer.valueOf(gameId)).get();
        List<MainGroup> mainGroups = game.getMainGroups();
        List<Subgroup> subgroups = new ArrayList<>();
        for (MainGroup mainGroup : mainGroups) {
            subgroups.addAll(mainGroup.getSubgroups());
        }
        return objectMapper.writeValueAsString(subgroups);
    }

    @PutMapping(value = "/put/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putSubgroup(@PathVariable("subgroupId") String subgroupId, @RequestBody String formData) throws JsonProcessingException {
        System.out.println("???");
        Subgroup subgroup = subgroupRepository.findById(Integer.valueOf(subgroupId)).get();
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
            Optional<SongSubgroup> subgroupOptional = songSubgroupList.stream().filter(songSubgroup -> String.valueOf(songSubgroup.getSong().getId()).equals(song)).findFirst();
            if (subgroupOptional.isPresent()) {
                songSubgroupRepository.delete(subgroupOptional.get());
            }
        }
        int position = 10 + (10 * songsToDetach.size());
        for (String song : songsToAssign) {
            position += 10;
            Song song1 = songRepository.findById(Integer.valueOf(song)).get();
            List<SongSubgroup> existingSubgroups = songSubgroupRepository.findBySong(song1);
            SongSubgroup originalSongSubgroup = existingSubgroups.stream().filter(songSubgroup ->
                            songSubgroup.getSubgroup().getMainGroup().getGame().equals(subgroup.getMainGroup().getGame()))
                    .findFirst().get();
            SongSubgroup songSubgroup = new SongSubgroup();
            songSubgroup.setSubgroup(subgroup);
            songSubgroup.setSong(song1);
            songSubgroup.setSpotifyId(originalSongSubgroup.getSpotifyId());
            songSubgroup.setInstrumental(originalSongSubgroup.getInstrumental());
            songSubgroup.setPosition(Long.valueOf(position));
            songSubgroup.setLyrics(originalSongSubgroup.getLyrics());
            songSubgroup.setRemix(originalSongSubgroup.getRemix());
            songSubgroup.setSrcId(originalSongSubgroup.getSrcId());
            songSubgroupRepository.save(songSubgroup);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/readAllSubgroup/{gameId}")
    public @ResponseBody String readAllSubgroupManage(Model model, @PathVariable("gameId") String gameId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game game = gameRepository.findById(Integer.valueOf(gameId)).get();
        List<MainGroup> mainGroups = game.getMainGroups();
        List<Subgroup> subgroups = new ArrayList<>();
        for (MainGroup mainGroup : mainGroups) {
            subgroups.addAll(mainGroup.getSubgroups());
        }
        return objectMapper.writeValueAsString(subgroups);
    }

}
