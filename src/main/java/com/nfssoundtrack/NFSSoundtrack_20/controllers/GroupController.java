package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/maingroup")
public class GroupController extends BaseControllerWithErrorHandling {

	private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

	@GetMapping(value = "/read/{gameId}")
	public @ResponseBody
	String gameGroupManage(@PathVariable("gameId") int gameId) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		Game game = gameService.findById(gameId).orElseThrow(() -> new Exception("no game found with id " + gameId));
		List<MainGroup> mainGroups = game.getMainGroups();
		return objectMapper.writeValueAsString(mainGroups);
	}

	@PostMapping(value = "/save/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String saveNewGroup(@PathVariable("gameId") int gameId, @RequestBody String formData) throws Exception {
		HashMap<String, Object> objectMapper = new ObjectMapper().readValue(formData,
				TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
		Game game = gameService.findById(gameId).orElseThrow(() -> new Exception("No game with id found " + gameId));
		String groupName = objectMapper.get("groupName").toString();
		MainGroup mainGroup = new MainGroup(groupName, game);
		mainGroup = mainGroupService.save(mainGroup);
		List<String> subgroups = (List<String>) objectMapper.get("subgroupsNames");
		for (String subgroup : subgroups) {
			if (!subgroup.isEmpty()) {
				String[] subgroupNamePosition = subgroup.split("_POS_");
				Subgroup targetSubgroup = new Subgroup(subgroupNamePosition[0],
						Integer.valueOf(subgroupNamePosition[1]), mainGroup);
				subgroupService.save(targetSubgroup);
			}
		}
		ObjectMapper objectMapper2 = new ObjectMapper();
		return objectMapper2.writeValueAsString(mainGroup);
	}

	@DeleteMapping(value = "/delete/{groupId}")
	public @ResponseBody
	String deleteGroup(@PathVariable("groupId") int groupId) throws Exception {
		MainGroup mainGroup = mainGroupService.findById(groupId).orElseThrow(
				() -> new Exception("no group found with id " + groupId));
		List<Subgroup> subgroups = mainGroup.getSubgroups();
		for (Subgroup subgroup : subgroups) {
			List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
			songSubgroupService.deleteAllInBatch(songSubgroupList);
		}
		subgroupService.deleteAllInBatch(subgroups);
		mainGroupService.delete(mainGroup);
		return "Delete successful";
	}

	@PutMapping(value = "/put/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String putGroup(@PathVariable("groupId") int groupId, @RequestBody String formData) throws Exception {
		MainGroup mainGroup = mainGroupService.findById(groupId).orElseThrow(
				() -> new Exception("no group with id found " + groupId));
		List<Subgroup> subgroups = mainGroup.getSubgroups();
		HashMap<?, ?> objectMapper = new ObjectMapper().readValue(formData,
				TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
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
			} else if (subgroup.contains("-UPDATE-")) {
				String subgroupId = subgroup.substring(0, subgroup.indexOf("-UPDATE-"));
				Optional<Subgroup> subgroupOptional = subgroups.stream().filter(subgroup1 -> subgroupId.equals(
						String.valueOf(subgroup1.getId()))).findFirst();
				if (subgroupOptional.isPresent()) {
					String newSubgroupName = subgroup.substring(subgroup.indexOf("-UPDATE-") + 8);
					String[] subgroupPosition = newSubgroupName.split("_POS_");
					subgroupOptional.get().setSubgroupName(subgroupPosition[0]);
					subgroupOptional.get().setPosition(Integer.valueOf(subgroupPosition[1]));
					subgroupsToUpdate.add(subgroupOptional.get());
				}
			} else {
				String[] subgroupNamePosition = subgroup.split("_POS_");
				Subgroup newSubgroup = new Subgroup(subgroupNamePosition[0], Integer.valueOf(subgroupNamePosition[1]),
						mainGroup);
				subgroupService.save(newSubgroup);
			}
		}
		for (Subgroup subgroup : subgroupsToUpdate) {
			subgroupService.save(subgroup);
		}
		for (Subgroup subgroup : subgroupsToDelete) {
			List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
			songSubgroupService.deleteAllInBatch(songSubgroupList);
		}
		subgroupService.deleteAllInBatch(subgroupsToDelete);
		ObjectMapper objectMapper2 = new ObjectMapper();
		mainGroup = mainGroupService.save(mainGroup);
		return objectMapper2.writeValueAsString(mainGroup);
	}
}
