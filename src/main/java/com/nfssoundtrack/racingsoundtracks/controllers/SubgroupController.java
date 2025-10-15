package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * controller for handling subgroups, mainly reading info and updating it
 * used in songsMgmt.js, subgroupMgmt.js
 */
@RestController
@RequestMapping("/subgroup")
public class SubgroupController  {

    private final BaseControllerWithErrorHandling baseController;

    public SubgroupController(BaseControllerWithErrorHandling baseController) {
        this.baseController = baseController;
    }

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
    public String subGroupManage(@PathVariable("subgroupId") int subgroupId)
            throws ResourceNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Subgroup subgroup = baseController.getSubgroupService().findById(subgroupId).orElseThrow(() ->
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
    public String putSubgroup(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData)
            throws ResourceNotFoundException, JsonProcessingException {
        Subgroup subgroup = baseController.getSubgroupService().findById(subgroupId)
                .orElseThrow(() -> new ResourceNotFoundException("No subgroup with id found " + subgroupId));
        String message = "Updating subgroup " + subgroup.getSubgroupName() + " in group " + subgroup.getMainGroup().getGroupName()
                + " in game " + subgroup.getMainGroup().getGame().getDisplayTitle();
        //so we get the subgroup
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        Map<String, List<String[]>> songsToAssign = new HashMap<>();
        List<String> songsToDetach = new ArrayList<>();
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            String songId = String.valueOf(linkedHashMap.get("song_id"));
            //i want to get exact song subgroup, for example because i want to put outro version in other subgroup
            String songSubgroupId = String.valueOf(linkedHashMap.get("songsubgroup_id"));
            String position = String.valueOf(linkedHashMap.get("position"));
            String state = String.valueOf(linkedHashMap.get("state"));
            //filling map of songs to add / delete to subgroup
            if (state.equals("ADD")) {
                List<String[]> arrayOfSongSubgroups = songsToAssign.get(songId);
                if (arrayOfSongSubgroups==null){
                    arrayOfSongSubgroups = new ArrayList<>();
                }
                arrayOfSongSubgroups.add(new String[]{songSubgroupId, position});
                songsToAssign.put(songId, arrayOfSongSubgroups);
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
                JustSomeHelper.unlinkSongWithTodaysSong(baseController.getTodaysSongService(), songSubgroup, mySong, baseController.getSongSubgroupService());
                JustSomeHelper.unlinkSongWithCorrection(baseController.getCorrectionService(), songSubgroup,
                        "; deleted song-subgroup: " + songSubgroup.toCorrectionString());
                String localMessage = "Removing song " + mySong.toAnotherChangeLogString()
                        + " from subgroup " + subgroup.getSubgroupName()
                        + " in group " + subgroup.getMainGroup().getGroupName()
                        + " in game " + subgroup.getMainGroup().getGame().getDisplayTitle();
                //we are then safe to delete song-subgroup association
                baseController.getSongSubgroupService().delete(songSubgroup);
                baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "delete", localMessage,
                        EntityUrl.SONG, mySong.toAnotherChangeLogString(), String.valueOf(mySong.getId()));
                //delete orphaned stuff - maybe song is not used anywhere else?
                List<SongSubgroup> orphanedSong = baseController.getSongSubgroupService().findBySong(mySong);
                if (orphanedSong.isEmpty()) {
                    String localDeepMessage = "Removing song " + mySong.toAnotherChangeLogString()
                            + " from database completely";
                    List<SongGenre> songGenres = baseController.getSongGenreService().findBySong(mySong);
                    //if that's the case, we scrap song-genre, author-song associations
                    baseController.getSongGenreService().deleteAll(songGenres);
                    List<AuthorSong> authorSongs = baseController.getAuthorSongService().findBySong(mySong);
                    baseController.getAuthorSongService().deleteAll(authorSongs);
                    baseController.sendMessageToChannel(EntityType.SONG, "delete", localDeepMessage,
                            EntityUrl.SONG, mySong.toAnotherChangeLogString(), String.valueOf(mySong.getId()));
                    baseController.getSongService().delete(mySong);
                }
            }
        }
        //we will put position of new song in subgroup based on how many songs we are trying to remove / add
        int positionPrefix = 10 * songsToDetach.size();
        for (Map.Entry<String, List<String[]>> song : songsToAssign.entrySet()) {
            Song song1 = baseController.getSongService().findById(Integer.valueOf(song.getKey()))
                    .orElseThrow(() -> new ResourceNotFoundException("No song with id found " + song));
            //using logic to create new songsubgroup based on details of existing songsubgroup
            for (String[] array : song.getValue()){
                String songSubgroupId = array[0];
                int position = Integer.parseInt(array[1]) + positionPrefix;
                //our getvalue is basically song-subgroup of song we associate with this subgroup
                if (songSubgroupId != null) {
                    SongSubgroup optionalSongSubgroup = baseController.getSongSubgroupService().findById(Integer.valueOf(songSubgroupId))
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "No songSubgroupId with id found " + songSubgroupId));
                    SongSubgroup songSubgroup = new SongSubgroup(optionalSongSubgroup);
                    songSubgroup.setPosition((long) position);
                    songSubgroup.setSubgroup(subgroup);
                    songSubgroup.setSong(song1);
                    String localMessage = "Adding song " + song1.toAnotherChangeLogString()
                            + " to subgroup " + subgroup.getSubgroupName()
                            + " in group " + subgroup.getMainGroup().getGroupName()
                            + " in game " + subgroup.getMainGroup().getGame().getDisplayTitle();
                    baseController.getSongSubgroupService().save(songSubgroup);
                    baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "create", localMessage,
                            EntityUrl.SONG, song1.toAnotherChangeLogString(), String.valueOf(song1.getId()));
                } else {
                    //i feel like this is not used at all
                    List<SongSubgroup> existingSubgroups = baseController.getSongSubgroupService().findBySong(song1);
                    SongSubgroup originalSongSubgroup = existingSubgroups.stream().filter(songSubgroup1 ->
                                    songSubgroup1.getSubgroup().getMainGroup().getGame().equals(subgroup.getMainGroup().getGame()))
                            .findFirst().orElseThrow(
                                    () -> new ResourceNotFoundException(
                                            "THere should be original subgroup here but not found"));
                    SongSubgroup songSubgroup = new SongSubgroup(originalSongSubgroup);
                    songSubgroup.setPosition((long) position);
                    songSubgroup.setSubgroup(subgroup);
                    songSubgroup.setSong(song1);
                    String localMessage = "[temp] Adding song " + song1.toAnotherChangeLogString()
                            + " to subgroup " + subgroup.getSubgroupName()
                            + " in group " + subgroup.getMainGroup().getGroupName()
                            + " in game " + subgroup.getMainGroup().getGame().getDisplayTitle();
                    baseController.getSongSubgroupService().save(songSubgroup);
                    baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "create", localMessage,
                            EntityUrl.SONG, song1.toAnotherChangeLogString(), String.valueOf(song1.getId()));
                }
            }
        }
        //again cleaning the cache of game when updating subgroup
        String gameShort = subgroup.getMainGroup().getGame().getGameShort();
        baseController.removeCacheEntry(gameShort);
        baseController.sendMessageToChannel(EntityType.SUBGROUP, "update", message,
                EntityUrl.GAME, subgroup.getMainGroup().getGame().getDisplayTitle(),
                subgroup.getMainGroup().getGame().getGameShort());
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
    public String moveSubgroupToOtherGroup(@RequestBody String formData)
            throws JsonProcessingException, ResourceNotFoundException {
        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) new ObjectMapper().readValue(formData, Map.class);
        long subgroupId = Long.parseLong(String.valueOf(linkedHashMap.get("subgroupId")));
        long groupId = Long.parseLong(String.valueOf(linkedHashMap.get("targetGroupId")));
        MainGroup mainGroup = baseController.getMainGroupService().findById(Math.toIntExact(groupId))
                .orElseThrow(() -> new ResourceNotFoundException("No mainGroup found with id " + groupId));
        Subgroup subgroup = baseController.getSubgroupService().findById(Math.toIntExact(subgroupId))
                .orElseThrow(() -> new ResourceNotFoundException("No subgroup found with id " + subgroupId));
        String message = "Moving subgroup " + subgroup.getSubgroupName() + " from group " + subgroup.getMainGroup().getGroupName()
                + " to group " + mainGroup.getGroupName() + " in game " + subgroup.getMainGroup().getGame().getDisplayTitle();
        //nothing special here, we use method to change main group
        subgroup.setMainGroup(mainGroup);
        baseController.getSubgroupService().save(subgroup);
        //need to clean cache of game after moving subgroup
        String gameShort = mainGroup.getGame().getGameShort();
        baseController.removeCacheEntry(gameShort);
        baseController.sendMessageToChannel(EntityType.SUBGROUP, "update", message,
                EntityUrl.GAME, subgroup.getMainGroup().getGame().getDisplayTitle(),
                subgroup.getMainGroup().getGame().getGameShort());
        return new ObjectMapper().writeValueAsString("OK");
    }

}
