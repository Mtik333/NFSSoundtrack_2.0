package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/subgroup")
public class SubgroupController {

    @Autowired
    private GameRepository gameRepository;

    @GetMapping(value = "/read/{gameId}")
    public @ResponseBody String subGroupManage(Model model, @PathVariable("gameId") String gameId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game game = gameRepository.findById(Integer.valueOf(gameId)).get();
        List<MainGroup> mainGroups = game.getMainGroups();
        List<Subgroup> subgroups = new ArrayList<>();
        for (MainGroup mainGroup : mainGroups){
            subgroups.addAll(mainGroup.getSubgroups());
        }
        return objectMapper.writeValueAsString(subgroups);
    }


    @GetMapping(value = "/readAllSubgroup/{gameId}")
    public @ResponseBody String readAllSubgroupManage(Model model, @PathVariable("gameId") String gameId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game game = gameRepository.findById(Integer.valueOf(gameId)).get();
        List<MainGroup> mainGroups = game.getMainGroups();
        List<Subgroup> subgroups = new ArrayList<>();
        for (MainGroup mainGroup : mainGroups){
            subgroups.addAll(mainGroup.getSubgroups());
        }
        return objectMapper.writeValueAsString(subgroups);
    }
}
