package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import com.nfssoundtrack.NFSSoundtrack_20.deserializers.GameDeserializer;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.GameEditSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/gamedb")
public class GameController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    GameDeserializer newGameDeserializer;

    @Autowired
    GameEditSerializer gameEditSerializer;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String saveNewGame(@RequestBody String formData) throws Exception {
        SimpleModule module = new SimpleModule();
        ObjectMapper objectMapper = new ObjectMapper();
        module.addDeserializer(Game.class, newGameDeserializer);
        objectMapper.registerModule(module);
        Game newGame = objectMapper.readValue(formData, Game.class);
        gameService.save(newGame);
        MainGroup allGroup = new MainGroup();
        allGroup.setGame(newGame);
        allGroup.setPosition(1);
        allGroup.setGroupName("All");
        mainGroupService.save(allGroup);
        Subgroup allSubgroup = new Subgroup();
        allSubgroup.setPosition(1);
        allSubgroup.setMainGroup(allGroup);
        allSubgroup.setSubgroupName("All");
        subgroupService.save(allSubgroup);
        cacheManager.getCache("series").clear();
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/read/{gameId}")
    public @ResponseBody
    String gameGroupManage(@PathVariable("gameId") int gameId) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        Game game = gameService.findById(gameId).orElseThrow(() -> new ResourceNotFoundException("No game found with id " + gameId));
        simpleModule.addSerializer(Game.class, gameEditSerializer);
        objectMapper.registerModule(simpleModule);
        return objectMapper.writeValueAsString(game);
    }

    @PutMapping(value = "/put/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String modifyGame(@RequestBody String formData, @PathVariable("gameId") int gameId) throws Exception {
        SimpleModule module = new SimpleModule();
        ObjectMapper objectMapper = new ObjectMapper();
        module.addDeserializer(Game.class, newGameDeserializer);
        objectMapper.registerModule(module);
        Game gameToEdit = gameService.findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException("No game with id found " + gameId));
        gameToEdit = objectMapper.readerForUpdating(gameToEdit).readValue(formData, Game.class);
        gameService.save(gameToEdit);
        String gameShort = gameToEdit.getGameShort();
        removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }
}
