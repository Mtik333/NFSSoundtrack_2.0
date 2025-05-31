package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.deserializers.GameDeserializer;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.GameEditSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * controller used to create / modify games
 * used in gameMgmt.js and serieMgmt.js
 */
@Controller
@RequestMapping(path = "/gamedb")
public class GameController extends BaseControllerWithErrorHandling {

    @Autowired
    GameDeserializer newGameDeserializer;

    @Autowired
    GameEditSerializer gameEditSerializer;

    /**
     * method used to create new game in serieMgmt.js
     * you can see when you go to 'manage series', then 'edit' of series and then 'new game'
     *
     * @param formData consists of various fields, @see GameDeserializer
     * @return OK if successful
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String saveNewGame(@RequestBody String formData) throws JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerDeserializerForObjectMapper(Game.class, newGameDeserializer);
        Game newGame = objectMapper.readValue(formData, Game.class);
        //at this point we should have game ready to save
        String message = "Creating game " + newGame.getDisplayTitle() + " in series: " + newGame.getSerie().getName();
        gameService.save(newGame);
        MainGroup allGroup = new MainGroup();
        allGroup.setGame(newGame);
        allGroup.setPosition(1);
        allGroup.setGroupName("All");
        //we need to automatically create "All" group as the kind of placeholder to display all the songs from the game
        mainGroupService.save(allGroup);
        Subgroup allSubgroup = new Subgroup();
        allSubgroup.setPosition(1);
        allSubgroup.setMainGroup(allGroup);
        allSubgroup.setSubgroupName("All");
        //we also need "All" subgroup for exactly the same purpose
        //"All" group is supposed to have only "All" subgroup and display all of the game's soundtrack
        subgroupService.save(allSubgroup);
        //as we are creating new game, we clear cache for 'series' field in order to refresh content of left-side menu
        Cache cache = cacheManager.getCache(WebsiteViewsController.SERIES);
        if (cache != null) {
            cache.clear();
        }
        //similarly we clear cache of all-games display in that left menu
        Cache cache2 = cacheManager.getCache(WebsiteViewsController.GAMES_ALPHA);
        if (cache2 != null) {
            cache2.clear();
        }
        sendMessageToChannel(EntityType.GAME, "create", message,
                EntityUrl.GAME, newGame.getDisplayTitle(), newGame.getGameShort());
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method gives game entity back to frontend in gameMgmt.js
     * you can see it triggered when using 'edit game' on a game
     *
     * @param gameId id of game to display in admin panel
     * @return json of game entity
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/read/{gameId}")
    public @ResponseBody
    String gameGroupManage(@PathVariable("gameId") int gameId)
            throws ResourceNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(Game.class, gameEditSerializer);
        Game game = gameService.findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException("No game found with id " + gameId));
        return objectMapper.writeValueAsString(game);
    }

    /**
     * method updates single game in database
     * used in gameMgmt.js when you click on 'save changes' when clicking 'edit game'
     *
     * @param formData various fields, see the deserializer
     * @param gameId   id of game being updated
     * @return OK if successful
     * @throws ResourceNotFoundException
     * @throws IOException
     */
    @PutMapping(value = "/put/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String modifyGame(@RequestBody String formData, @PathVariable("gameId") int gameId)
            throws ResourceNotFoundException, IOException {
        ObjectMapper objectMapper = JustSomeHelper.registerDeserializerForObjectMapper(Game.class, newGameDeserializer);
        Game gameToEdit = gameService.findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException("No game with id found " + gameId));
        String message = "Updating game " + gameToEdit.getDisplayTitle();
        gameToEdit = objectMapper.readerForUpdating(gameToEdit).readValue(formData, Game.class);
        gameService.save(gameToEdit);
        String gameShort = gameToEdit.getGameShort();
        removeCacheEntry(gameShort);
        sendMessageToChannel(EntityType.GAME, "update", message,
                EntityUrl.GAME, gameToEdit.getDisplayTitle(), gameToEdit.getGameShort());
        return new ObjectMapper().writeValueAsString("OK");
    }
}
