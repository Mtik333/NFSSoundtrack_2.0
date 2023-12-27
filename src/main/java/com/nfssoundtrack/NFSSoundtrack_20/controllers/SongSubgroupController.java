package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Instrumental;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/songSubgroup")
public class SongSubgroupController {

    @Autowired
    private SubgroupRepository subgroupRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @PutMapping(value = "/put/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putSubgroup(@PathVariable("subgroupId") String subgroupId, @RequestBody String formData) throws JsonProcessingException {
        System.out.println("???");
        Subgroup subgroup = subgroupRepository.getReferenceById(Integer.valueOf(subgroupId));
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            Long songSubgroupId = Long.parseLong(String.valueOf(linkedHashMap.get("songSubgroupId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            SongSubgroup songSubgroup = songSubgroupRepository.getReferenceById(Math.toIntExact(songSubgroupId));
            songSubgroup.setPosition(position);
            songSubgroupRepository.save(songSubgroup);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }
}
