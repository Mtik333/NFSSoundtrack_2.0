package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.GroupSerializer;
import com.nfssoundtrack.racingsoundtracks.serializers.GroupWithSubgroupsSerializer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * controller used for handling groups
 * used in groupMgmt.js, subgroupMgmt.js, songsMgmt.js
 */
@RestController
@RequestMapping("/maingroup")
public class GroupController  {

    public static final String POS = "_POS_";
    public static final String NO_GAME_FOUND_WITH_ID = "no game found with id ";
    public static final String UPDATE = "-UPDATE-";

    private final BaseControllerWithErrorHandling baseController;
    private final GroupWithSubgroupsSerializer groupWithSubgroupsSerializer;
    private final GroupSerializer groupSerializer;

    public GroupController(BaseControllerWithErrorHandling baseController, GroupWithSubgroupsSerializer groupWithSubgroupsSerializer, GroupSerializer groupSerializer) {
        this.baseController = baseController;
        this.groupWithSubgroupsSerializer = groupWithSubgroupsSerializer;
        this.groupSerializer = groupSerializer;
    }

    /**
     * method to get all groups that belong to game we are editing
     * triggered while clicking "manage subgroups" button on a game
     * used in subgroupMgmt.js
     *
     * @param gameId id of game
     * @return json list of groups from the game
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/readForEditSubgroups/{gameId}")
    public String gameGroupReadSubgroups(@PathVariable("gameId") int gameId)
            throws ResourceNotFoundException, JsonProcessingException {
        Game game = baseController.getGameService().findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException(NO_GAME_FOUND_WITH_ID + gameId));
        List<MainGroup> mainGroups = game.getMainGroups();
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(MainGroup.class,
                groupWithSubgroupsSerializer);
        return objectMapper.writeValueAsString(mainGroups);
    }

    /**
     * method to get all groups that belong to game we are editing but we do not fetch info about songs in subgroup
     * so we just fill that "subgroups" dropdown when editing songs too
     * this is the "manage groups" button in admin panel but it also gets triggered when you cancel editing song
     * or when you click 'manage songs' on a game
     * used in groupMgmt.js and songMgmt.js
     *
     * @param gameId id of game
     * @return json list of groups from the game
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/readForEditGroups/{gameId}")
    public String gameGroupReadGroups(@PathVariable("gameId") int gameId)
            throws ResourceNotFoundException, JsonProcessingException {
        Game game = baseController.getGameService().findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException(NO_GAME_FOUND_WITH_ID + gameId));
        List<MainGroup> mainGroups = game.getMainGroups();
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(MainGroup.class, groupSerializer);
        return objectMapper.writeValueAsString(mainGroups);
    }

    /**
     * method to save new group in the game and subgroups too
     * used in groupMgmt.js when you click 'submit' on 'new group' in 'manage groups' of game
     *
     * @param gameId   game of id to save new group to
     * @param formData basically consists of group name and names of subgroups
     * @return json of saved group
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PostMapping(value = "/save/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String saveNewGroup(@PathVariable("gameId") int gameId, @RequestBody String formData)
            throws JsonProcessingException, ResourceNotFoundException {
        //think about making this a bit prettier to read?
        //here we basically create hashamp with string as key and object as value
        HashMap<String, Object> objectMapper = new ObjectMapper().readValue(formData,
                TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
        Game game = baseController.getGameService().findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException(NO_GAME_FOUND_WITH_ID + gameId));
        String groupName = objectMapper.get("groupName").toString();
        MainGroup mainGroup = new MainGroup(groupName, game);
        String message = "Saving group " + mainGroup.getGroupName() + " in game " + game.getDisplayTitle();
        //first we save the new group
        mainGroup = baseController.getMainGroupService().save(mainGroup);
        baseController.sendMessageToChannel(EntityType.MAIN_GROUP, "create", message,
                EntityUrl.GAME, game.getDisplayTitle(), game.getGameShort());
        //we will likely have some groups created when doing such request
        List<String> subgroups = (List<String>) objectMapper.get("subgroupsNames");
        for (String subgroup : subgroups) {
            //not sure if this can happen but just for safety not allowing empty subgroups to be created
            if (!subgroup.isEmpty()) {
                String[] subgroupNamePosition = subgroup.split(POS);
                Subgroup targetSubgroup = new Subgroup(subgroupNamePosition[0],
                        Integer.valueOf(subgroupNamePosition[1]), mainGroup);
                String localMessage = "Creating subgroup " + targetSubgroup.getSubgroupName();
                baseController.getSubgroupService().save(targetSubgroup);
                baseController.sendMessageToChannel(EntityType.SUBGROUP, "create", localMessage,
                        EntityUrl.GAME, game.getDisplayTitle(), game.getGameShort());
            }
        }
        ObjectMapper objectMapper2 = new ObjectMapper();
        return objectMapper2.writeValueAsString(mainGroup);
    }

    /**
     * method to delete group from database
     * obviously we need to delete all songs associated with subgroups that belong to this group
     * used in groupMgmt.js when you click 'delete' button when clicking on 'manage groups' on a game
     *
     * @param groupId id of group
     * @return strange text as I should rather return "OK" value there but this is not a json return
     * @throws ResourceNotFoundException
     */
    @DeleteMapping(value = "/delete/{groupId}")
    public String deleteGroup(@PathVariable("groupId") int groupId) throws ResourceNotFoundException {
        MainGroup mainGroup = baseController.getMainGroupService().findById(groupId).orElseThrow(
                () -> new ResourceNotFoundException("no group found with id " + groupId));
        String message = "Deleting group " + mainGroup.getGroupName() + " in game " + mainGroup.getGame().getDisplayTitle();
        List<Subgroup> subgroups = mainGroup.getSubgroups();
        //first we delete entries of songs linked to subgroups that are in this group
        for (Subgroup subgroup : subgroups) {
            List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
            baseController.getSongSubgroupService().deleteAllInBatch(songSubgroupList);
            //todo remove dangling songs if song exists only in subgroup in this game?
        }
        //now we can delete subgroups and finally the main group
        baseController.getSubgroupService().deleteAllInBatch(subgroups);
        baseController.sendMessageToChannel(EntityType.MAIN_GROUP, "delete", message,
                EntityUrl.GAME, mainGroup.getGame().getDisplayTitle(), mainGroup.getGame().getGameShort());
        baseController.getMainGroupService().delete(mainGroup);
        return "Delete successful " + groupId;
    }

    /**
     * method used to modify existing group - this is the "submit" button in "edit" button in "manage groups"
     * used in groupMgmt.js
     *
     * @param groupId  id of group to modify
     * @param formData all of the info existing on group level
     *                 we can try to delete, create or modify (name of) subgroups too
     * @return json of main group saved
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @PutMapping(value = "/put/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String putGroup(@PathVariable("groupId") int groupId, @RequestBody String formData)
            throws ResourceNotFoundException, JsonProcessingException {
        MainGroup mainGroup = baseController.getMainGroupService().findById(groupId).orElseThrow(
                () -> new ResourceNotFoundException("no group with id found " + groupId));
        String message = "Updating group " + mainGroup.getGroupName() + " in game " + mainGroup.getGame().getDisplayTitle();
        List<Subgroup> subgroups = mainGroup.getSubgroups();
        HashMap<?, ?> objectMapper = new ObjectMapper().readValue(formData,
                TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
        mainGroup.setGroupName(String.valueOf(objectMapper.get("groupName")));
        List<String> updatedSubgroups = (List<String>) objectMapper.get("subgroupsNames");
        //so we have to handle cases of deleting subgroups and updating subgroups
        List<Subgroup> subgroupsToDelete = new ArrayList<>();
        List<Subgroup> subgroupsToUpdate = new ArrayList<>();
        for (String subgroup : updatedSubgroups) {
            if (subgroup.contains("-DELETE")) {
                //so clever to give values with hardcoded string to indicate that we want to delete subgroup
                String subgroupId = subgroup.substring(0, subgroup.indexOf("-DELETE"));
                Optional<Subgroup> subgroupOptional = subgroups.stream().filter(subgroup1 -> subgroupId.equals(
                        String.valueOf(subgroup1.getId()))).findFirst();
                subgroupOptional.ifPresent(subgroupsToDelete::add);
                //at this point we just put subgroup to our list
            } else if (subgroup.contains(UPDATE)) {
                String subgroupId = subgroup.substring(0, subgroup.indexOf(UPDATE));
                Optional<Subgroup> subgroupOptional = subgroups.stream().filter(subgroup1 -> subgroupId.equals(
                        String.valueOf(subgroup1.getId()))).findFirst();
                //here it can be that we update subgroup name and also its position
                if (subgroupOptional.isPresent()) {
                    String newSubgroupName = subgroup.substring(subgroup.indexOf(UPDATE) + 8);
                    String[] subgroupPosition = newSubgroupName.split(POS);
                    subgroupOptional.get().setSubgroupName(subgroupPosition[0]);
                    subgroupOptional.get().setPosition(Integer.valueOf(subgroupPosition[1]));
                    subgroupsToUpdate.add(subgroupOptional.get());
                }
            } else {
                //other case is just creating the new subgroup with specific position
                String[] subgroupNamePosition = subgroup.split(POS);
                Subgroup newSubgroup = new Subgroup(subgroupNamePosition[0], Integer.valueOf(subgroupNamePosition[1]),
                        mainGroup);
                baseController.getSubgroupService().save(newSubgroup);
            }
        }
        //for modifying subgroups its easy, just save to db
        for (Subgroup subgroup : subgroupsToUpdate) {
            String localMessage = "Updating subgroup " + subgroup.getSubgroupName()
                    + " in group " + subgroup.getMainGroup().getGroupName() + " in game "
                    + subgroup.getMainGroup().getGame().getDisplayTitle();
            baseController.getSubgroupService().save(subgroup);
            baseController.sendMessageToChannel(EntityType.SUBGROUP, "update", localMessage,
                    EntityUrl.GAME, mainGroup.getGame().getDisplayTitle(), mainGroup.getGame().getGameShort());
        }
        for (Subgroup subgroup : subgroupsToDelete) {
            //for deleting subgroups there are more problems
            String localMessage = "Deleting subgroup " + subgroup.getSubgroupName()
                    + " in group " + subgroup.getMainGroup().getGroupName() + " in game "
                    + subgroup.getMainGroup().getGame().getDisplayTitle();
            List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
            for (SongSubgroup potentialCorrected : songSubgroupList) {
                //some of the songs might have been reported in corrections, so we have to
                //unlink song with correction to avoid foreign key violation
                JustSomeHelper.unlinkSongWithCorrection(baseController.getCorrectionService(), potentialCorrected,
                        ";;" + potentialCorrected.toCorrectionString());
                //we also need to check if song was used in todays song table
                List<TodaysSong> todaySongs = baseController.getTodaysSongService().findAllBySongSubgroup(potentialCorrected);
                if (!todaySongs.isEmpty()) {
                    TodaysSong todaysSong = todaySongs.get(0);
                    Song mainSong = potentialCorrected.getSong();
                    //so if song was used in other game, we can simply replace song-subgroup id to
                    //the one that is going to remain as exists in other game
                    List<SongSubgroup> songSubgroups = baseController.getSongSubgroupService().findBySong(mainSong);
                    Optional<SongSubgroup> otherUsageOfSong = songSubgroups.stream().filter(songSubgroup ->
                            !songSubgroup.getId().equals(potentialCorrected.getId())).findFirst();
                    if (otherUsageOfSong.isPresent()) {
                        todaysSong.setSongSubgroup(otherUsageOfSong.get());
                        baseController.getTodaysSongService().save(todaysSong);
                    } else {
                        //if there's no other usage of song, we just replace todays song entry with random song
                        Long biggestId = baseController.getSongSubgroupService().findTopByOrderByIdDesc().getId();
                        int nextSongId = ThreadLocalRandom.current().nextInt(1, Math.toIntExact(biggestId));
                        SongSubgroup targetSong = baseController.getSongSubgroupService().findById(nextSongId).orElseThrow(() ->
                                new ResourceNotFoundException("no songsubgroup found with id " + nextSongId));
                        todaysSong.setSongSubgroup(targetSong);
                        baseController.getTodaysSongService().save(todaysSong);
                    }
                }
            }
            //now we can finally delete associations with songs as there will be no foreign key violation
            baseController.getSongSubgroupService().deleteAllInBatch(songSubgroupList);
            baseController.sendMessageToChannel(EntityType.SUBGROUP, "delete", localMessage,
                    EntityUrl.GAME, mainGroup.getGame().getDisplayTitle(), mainGroup.getGame().getGameShort());
        }
        //if songs are deleted from subgroup that are are deleting, we can delete subgroups as well
        baseController.getSubgroupService().deleteAllInBatch(subgroupsToDelete);
        ObjectMapper objectMapper2 = new ObjectMapper();
        mainGroup = baseController.getMainGroupService().saveUpdate(mainGroup);
        baseController.sendMessageToChannel(EntityType.MAIN_GROUP, "update", message,
                EntityUrl.GAME, mainGroup.getGame().getDisplayTitle(), mainGroup.getGame().getGameShort());
        return objectMapper2.writeValueAsString(mainGroup);
    }

    /**
     * method used to modify positions of the groups - this is the "update positions in DB" button in "manage groups"
     * used in groupMgmt.js
     *
     * @param formData info about group id and positions
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/updatePositions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String putGroupPositions(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        //list of key-value entries where key is group id and value is its position
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            long groupId = Long.parseLong(String.valueOf(linkedHashMap.get("groupId")));
            Integer position = Integer.parseInt(String.valueOf(linkedHashMap.get("position")));
            MainGroup mainGroup = baseController.getMainGroupService().findById(Math.toIntExact(groupId)).orElseThrow(
                    () -> new ResourceNotFoundException("no serie with id found " + groupId));
            if (!position.equals(mainGroup.getPosition()) && position % 10 != 0) {
                String localMessage = "Updating position of group in game "
                        + mainGroup.getGame().getDisplayTitle() + " to " + position;
                baseController.sendMessageToChannel(EntityType.MAIN_GROUP, "update", localMessage,
                        EntityUrl.GAME, mainGroup.getGame().getDisplayTitle(), mainGroup.getGame().getGameShort());
            }
            mainGroup.setPosition(position);
            baseController.getMainGroupService().saveUpdate(mainGroup);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }
}
