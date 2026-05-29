package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.GamePlatform;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Platform;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.services.GamePlatformService;
import com.nfssoundtrack.racingsoundtracks.services.PlatformService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/gameplatform")
public class GamePlatformController {

    private final BaseControllerWithErrorHandling baseController;
    private final GamePlatformService gamePlatformService;
    private final PlatformService platformService;

    public GamePlatformController(BaseControllerWithErrorHandling baseController,
                                  GamePlatformService gamePlatformService,
                                  PlatformService platformService) {
        this.baseController = baseController;
        this.gamePlatformService = gamePlatformService;
        this.platformService = platformService;
    }

    @GetMapping("/readByGame/{gameId}")
    public String readByGame(@PathVariable int gameId)
            throws ResourceNotFoundException, JsonProcessingException {
        Game game = baseController.getGameService().findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("No game with id " + gameId));
        List<GamePlatform> list = gamePlatformService.findByGame(game);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.createArrayNode();
        for (GamePlatform gp : list) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", gp.getId());
            node.put("platformId", gp.getPlatform().getId());
            node.put("platformName", gp.getPlatform().getPlatform());
            node.put("retroachievementsUrl", gp.getRetroachievementsUrl());
            node.put("hasSet", gp.getHasSet() != null && gp.getHasSet());
            array.add(node);
        }
        return mapper.writeValueAsString(array);
    }

    @GetMapping("/platforms")
    public String getAllPlatforms() throws JsonProcessingException {
        List<Platform> platforms = platformService.findAll();
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.createArrayNode();
        for (Platform p : platforms) {
            ObjectNode node = mapper.createObjectNode();
            node.put("id", p.getId());
            node.put("platform", p.getPlatform());
            array.add(node);
        }
        return mapper.writeValueAsString(array);
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String save(@RequestBody String formData)
            throws ResourceNotFoundException, IOException {
        JsonNode node = new ObjectMapper().readTree(formData);
        int gameId = node.get("gameId").asInt();
        int platformId = node.get("platformId").asInt();
        Game game = baseController.getGameService().findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("No game with id " + gameId));
        Platform platform = platformService.findAll().stream()
                .filter(p -> p.getId().equals((long) platformId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No platform with id " + platformId));
        GamePlatform gp = new GamePlatform(platform, game);
        gp.setRetroachievementsUrl(JustSomeHelper.returnProperValueToDb(node.get("retroachievementsUrl").asText()));
        gp.setHasSet(node.get("hasSet").asBoolean());
        gamePlatformService.save(gp);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/put/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String put(@RequestBody String formData, @PathVariable int id)
            throws ResourceNotFoundException, IOException {
        GamePlatform gp = gamePlatformService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No game platform with id " + id));
        JsonNode node = new ObjectMapper().readTree(formData);
        gp.setRetroachievementsUrl(JustSomeHelper.returnProperValueToDb(node.get("retroachievementsUrl").asText()));
        gp.setHasSet(node.get("hasSet").asBoolean());
        gamePlatformService.save(gp);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable int id)
            throws ResourceNotFoundException, JsonProcessingException {
        gamePlatformService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No game platform with id " + id));
        gamePlatformService.deleteById(id);
        return new ObjectMapper().writeValueAsString("OK");
    }
}
