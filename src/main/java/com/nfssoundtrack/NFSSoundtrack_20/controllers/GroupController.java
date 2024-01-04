package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.MainGroupRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/maingroup")
public class GroupController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MainGroupRepository mainGroupRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @Autowired
    private SubgroupRepository subgroupRepository;

    @GetMapping(value = "/read/{gameId}")
    public @ResponseBody String gameGroupManage(Model model, @PathVariable("gameId") String gameId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Game game = gameRepository.findById(Integer.valueOf(gameId)).get();
        List<MainGroup> mainGroups = game.getMainGroups();
        for (MainGroup mainGroup : mainGroups){
            List<Subgroup> subgroups = mainGroup.getSubgroups();
            for (Subgroup subgroup : subgroups){
                List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
                for (SongSubgroup songSubgroup : songSubgroupList){
                    songSubgroup.getSong();
                    break;
                }
                break;
            }
            mainGroup.getSubgroups().sort(Comparator.comparing(Subgroup::getPosition));
        }
        return objectMapper.writeValueAsString(mainGroups);
    }

    @PostMapping(value = "/save/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String saveNewGroup(@PathVariable("gameId") String gameId, @RequestBody String formData) throws JsonProcessingException {
        HashMap<?, ?> objectMapper = new ObjectMapper().readValue(formData, HashMap.class);
        MainGroup mainGroup = new MainGroup();
        mainGroup.setGroupName(String.valueOf(objectMapper.get("groupName")));
        mainGroup.setGame(gameRepository.findById(Integer.valueOf(gameId)).get());
        mainGroup = mainGroupRepository.save(mainGroup);
        List<String> subgroups = (List<String>) objectMapper.get("subgroupsNames");
        for (String subgroup : subgroups) {
            if (!subgroup.isEmpty()) {
                Subgroup targetSubgroup = new Subgroup();
                String[] subgroupNamePosition = subgroup.split("_POS_");
                targetSubgroup.setSubgroupName(subgroupNamePosition[0]);
                targetSubgroup.setMainGroup(mainGroup);
                targetSubgroup.setPosition(Integer.valueOf(subgroupNamePosition[1]));
                subgroupRepository.save(targetSubgroup);
            }
        }
        ObjectMapper objectMapper2 = new ObjectMapper();
        return objectMapper2.writeValueAsString(mainGroupRepository.getReferenceById(Math.toIntExact(mainGroup.getId())));
    }

    @DeleteMapping(value = "/delete/{groupId}")
    public @ResponseBody String deleteGroup(@PathVariable("groupId") String groupId) {
        MainGroup mainGroup = mainGroupRepository.findById(Integer.valueOf(groupId)).get();
        List<Subgroup> subgroups = mainGroup.getSubgroups();
        for (Subgroup subgroup : subgroups) {
            List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
            songSubgroupRepository.deleteAllInBatch(songSubgroupList);
        }
        subgroupRepository.deleteAllInBatch(subgroups);
        mainGroupRepository.delete(mainGroup);
        return "Delete successful";
    }

    @PutMapping(value = "/put/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putGroup(@PathVariable("groupId") String groupId, @RequestBody String formData) throws JsonProcessingException {
        MainGroup mainGroup = mainGroupRepository.findById(Integer.valueOf(groupId)).get();
        List<Subgroup> subgroups = mainGroup.getSubgroups();
        HashMap<?, ?> objectMapper = new ObjectMapper().readValue(formData, HashMap.class);
        mainGroup.setGroupName(String.valueOf(objectMapper.get("groupName")));
        List<String> updatedSubgroups = (List<String>) objectMapper.get("subgroupsNames");
        List<Subgroup> subgroupsToDelete = new ArrayList<>();
        List<Subgroup> subgroupsToUpdate = new ArrayList<>();
        for (String subgroup : updatedSubgroups) {
            if (subgroup.contains("-DELETE")) {
                String subgroupId = subgroup.substring(0, subgroup.indexOf("-DELETE"));
                Optional<Subgroup> subgroupOptional = subgroups.stream().filter(subgroup1 -> subgroupId.equals(
                        String.valueOf(subgroup1.getId()))).findFirst();
                subgroupOptional.ifPresent(subgroupsToDelete::add);
            } else if (subgroup.contains("-UPDATE-")){
                String subgroupId = subgroup.substring(0, subgroup.indexOf("-UPDATE-"));
                Optional<Subgroup> subgroupOptional = subgroups.stream().filter(subgroup1 -> subgroupId.equals(
                        String.valueOf(subgroup1.getId()))).findFirst();
                if (subgroupOptional.isPresent()) {
                    String newSubgroupName = subgroup.substring(subgroup.indexOf("-UPDATE-")+8);
                    String[] subgroupPosition = newSubgroupName.split("_POS_");
                    subgroupOptional.get().setSubgroupName(subgroupPosition[0]);
                    subgroupOptional.get().setPosition(Integer.valueOf(subgroupPosition[1]));
                    subgroupsToUpdate.add(subgroupOptional.get());
                }
            } else {
                Subgroup newSubgroup = new Subgroup();
                String[] subgroupNamePosition = subgroup.split("_POS_");
                newSubgroup.setSubgroupName(subgroupNamePosition[0]);
                newSubgroup.setMainGroup(mainGroup);
                newSubgroup.setPosition(Integer.valueOf(subgroupNamePosition[1]));
                subgroupRepository.save(newSubgroup);
            }
        }
        for (Subgroup subgroup : subgroupsToUpdate){
            subgroupRepository.save(subgroup);
        }
        for (Subgroup subgroup : subgroupsToDelete) {
            List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
            songSubgroupRepository.deleteAllInBatch(songSubgroupList);
        }
        subgroupRepository.deleteAllInBatch(subgroupsToDelete);
        ObjectMapper objectMapper2 = new ObjectMapper();
        mainGroup = mainGroupRepository.save(mainGroup);
        return objectMapper2.writeValueAsString(mainGroup);
    }
}
