package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Genre;
import com.nfssoundtrack.racingsoundtracks.dbmodel.MainGroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Subgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.TodaysSong;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.GroupSerializer;
import com.nfssoundtrack.racingsoundtracks.serializers.GroupWithSubgroupsSerializer;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * controller used for handling groups
 * used in groupMgmt.js, subgroupMgmt.js, songsMgmt.js
 */
@Controller
@RequestMapping("/maingroup")
public class GroupController extends BaseControllerWithErrorHandling {

	public static final String POS = "_POS_";
	public static final String NO_GAME_FOUND_WITH_ID = "no game found with id ";
	public static final String UPDATE = "-UPDATE-";
	@Autowired
	GroupWithSubgroupsSerializer groupWithSubgroupsSerializer;

	@Autowired
	GroupSerializer groupSerializer;

	/**
	 * method to get all subgroups that belong to game we are editing
	 * this is the "manage subgroups" button in admin panel
	 * @param gameId id of game
	 * @return list of groups from the game
	 * @throws ResourceNotFoundException
	 * @throws JsonProcessingException
	 */
	@GetMapping(value = "/readForEditSubgroups/{gameId}")
	public @ResponseBody
	String gameGroupReadSubgroups(@PathVariable("gameId") int gameId)
			throws ResourceNotFoundException, JsonProcessingException {
		Game game = gameService.findById(gameId).orElseThrow(
				() -> new ResourceNotFoundException(NO_GAME_FOUND_WITH_ID + gameId));
		List<MainGroup> mainGroups = game.getMainGroups();
		ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(MainGroup.class, groupWithSubgroupsSerializer);
		return objectMapper.writeValueAsString(mainGroups);
	}

	@GetMapping(value = "/readForEditGroups/{gameId}")
	public @ResponseBody
	String gameGroupReadGroups(@PathVariable("gameId") int gameId)
			throws ResourceNotFoundException, JsonProcessingException {
		Game game = gameService.findById(gameId).orElseThrow(
				() -> new ResourceNotFoundException(NO_GAME_FOUND_WITH_ID + gameId));
		List<MainGroup> mainGroups = game.getMainGroups();
		ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(MainGroup.class, groupSerializer);
		return objectMapper.writeValueAsString(mainGroups);
	}

	@GetMapping(value = "/read/{gameId}")
	public @ResponseBody
	String gameGroupManage(@PathVariable("gameId") int gameId)
			throws ResourceNotFoundException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Game game = gameService.findById(gameId).orElseThrow(
				() -> new ResourceNotFoundException(NO_GAME_FOUND_WITH_ID + gameId));
		List<MainGroup> mainGroups = game.getMainGroups();
		return objectMapper.writeValueAsString(mainGroups);
	}

	@PostMapping(value = "/save/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String saveNewGroup(@PathVariable("gameId") int gameId, @RequestBody String formData)
			throws JsonProcessingException, ResourceNotFoundException {
		HashMap<String, Object> objectMapper = new ObjectMapper().readValue(formData,
				TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
		Game game = gameService.findById(gameId).orElseThrow(
				() -> new ResourceNotFoundException(NO_GAME_FOUND_WITH_ID + gameId));
		String groupName = objectMapper.get("groupName").toString();
		MainGroup mainGroup = new MainGroup(groupName, game);
		mainGroup = mainGroupService.save(mainGroup);
		List<String> subgroups = (List<String>) objectMapper.get("subgroupsNames");
		for (String subgroup : subgroups) {
			if (!subgroup.isEmpty()) {
				String[] subgroupNamePosition = subgroup.split(POS);
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
	String deleteGroup(@PathVariable("groupId") int groupId) throws ResourceNotFoundException {
		MainGroup mainGroup = mainGroupService.findById(groupId).orElseThrow(
				() -> new ResourceNotFoundException("no group found with id " + groupId));
		List<Subgroup> subgroups = mainGroup.getSubgroups();
		for (Subgroup subgroup : subgroups) {
			List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
			songSubgroupService.deleteAllInBatch(songSubgroupList);
		}
		subgroupService.deleteAllInBatch(subgroups);
		mainGroupService.delete(mainGroup);
		return "Delete successful " + groupId;
	}

	@PutMapping(value = "/put/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String putGroup(@PathVariable("groupId") int groupId, @RequestBody String formData)
			throws ResourceNotFoundException, JsonProcessingException {
		MainGroup mainGroup = mainGroupService.findById(groupId).orElseThrow(
				() -> new ResourceNotFoundException("no group with id found " + groupId));
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
			} else if (subgroup.contains(UPDATE)) {
				String subgroupId = subgroup.substring(0, subgroup.indexOf(UPDATE));
				Optional<Subgroup> subgroupOptional = subgroups.stream().filter(subgroup1 -> subgroupId.equals(
						String.valueOf(subgroup1.getId()))).findFirst();
				if (subgroupOptional.isPresent()) {
					String newSubgroupName = subgroup.substring(subgroup.indexOf(UPDATE) + 8);
					String[] subgroupPosition = newSubgroupName.split(POS);
					subgroupOptional.get().setSubgroupName(subgroupPosition[0]);
					subgroupOptional.get().setPosition(Integer.valueOf(subgroupPosition[1]));
					subgroupsToUpdate.add(subgroupOptional.get());
				}
			} else {
				String[] subgroupNamePosition = subgroup.split(POS);
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
			for (SongSubgroup potentialCorrected : songSubgroupList) {
				JustSomeHelper.unlinkSongWithCorrection(correctionService, potentialCorrected,
						";;" + potentialCorrected.toCorrectionString());
				List<TodaysSong> todaySongs = todaysSongService.findAllBySongSubgroup(potentialCorrected);
				if (!todaySongs.isEmpty()) {
					TodaysSong todaysSong = todaySongs.get(0);
					Song mainSong = potentialCorrected.getSong();
					List<SongSubgroup> songSubgroups = songSubgroupService.findBySong(mainSong);
					Optional<SongSubgroup> otherUsageOfSong = songSubgroups.stream().filter(songSubgroup ->
							!songSubgroup.getId().equals(potentialCorrected.getId())).findFirst();
					if (otherUsageOfSong.isPresent()) {
						todaysSong.setSongSubgroup(otherUsageOfSong.get());
						todaysSongService.save(todaysSong);
					} else {
						Long biggestId = songSubgroupService.findTopByOrderByIdDesc().getId();
						int nextSongId = ThreadLocalRandom.current().nextInt(1, Math.toIntExact(biggestId));
						SongSubgroup targetSong = songSubgroupService.findById(nextSongId).orElseThrow(() ->
								new ResourceNotFoundException("no songsubgroup found with id " + nextSongId));
						todaysSong.setSongSubgroup(targetSong);
						todaysSongService.save(todaysSong);
					}
				}
			}
			songSubgroupService.deleteAllInBatch(songSubgroupList);
		}
		subgroupService.deleteAllInBatch(subgroupsToDelete);
		ObjectMapper objectMapper2 = new ObjectMapper();
		mainGroup = mainGroupService.save(mainGroup);
		return objectMapper2.writeValueAsString(mainGroup);
	}

	@PutMapping(value = "/updatePositions", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String putGroupPositions(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
		List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
		for (Object obj : objectMapper) {
			LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
			long groupId = Long.parseLong(String.valueOf(linkedHashMap.get("groupId")));
			Integer position = Integer.parseInt(String.valueOf(linkedHashMap.get("position")));
			MainGroup mainGroup = mainGroupService.findById(Math.toIntExact(groupId)).orElseThrow(
					() -> new ResourceNotFoundException("no serie with id found " + groupId));
			mainGroup.setPosition(position);
			mainGroupService.save(mainGroup);
		}
		return new ObjectMapper().writeValueAsString("OK");
	}
}
