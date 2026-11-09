package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.services.GamePlaylistService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/ripSoundtrack")
public class RipSoundtrackController {

    private final BaseControllerWithErrorHandling baseController;

    public RipSoundtrackController(BaseControllerWithErrorHandling baseController) {
        this.baseController = baseController;
    }

    @GetMapping("/byGame/{gameId}")
    public String getByGame(@PathVariable("gameId") int gameId)
            throws ResourceNotFoundException, IOException {
        Game game = baseController.getGameService().findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("No game with id " + gameId));
        List<GamePlatform> gamePlatforms = baseController.getGamePlatformService().findByGame(game);
        List<RipSoundtrack> rippedSoundtracks = baseController.getRipSoundtrackService().findByGame(game);
        ObjectMapper om = new ObjectMapper();
        ObjectNode objectNode = om.createObjectNode();
        ArrayNode ripOstArray = om.createArrayNode();
        for (RipSoundtrack rip : rippedSoundtracks) {
            ObjectNode node = om.createObjectNode();
            node.put("id", rip.getId());
            node.put("gamePlatformId", rip.getGamePlatform().getId());
            node.put("platformId", rip.getGamePlatform().getPlatform().getId());
            node.put("platformName", rip.getGamePlatform().getPlatform().getPlatform());
            node.put("link", rip.getLink());
            node.put("source", rip.getRipSource().value());
            node.put("altTitle", rip.getAltTitle() != null ? rip.getAltTitle() : "");
            ripOstArray.add(node);
        }
        objectNode.set("ripSoundtracks", ripOstArray);
        ArrayNode gamePlatformArray = om.createArrayNode();
        for (GamePlatform gamePlatform : gamePlatforms){
            ObjectNode node = om.createObjectNode();
            node.put("id", gamePlatform.getId());
            node.put("platformId", gamePlatform.getPlatform().getId());
            node.put("gameId", gamePlatform.getGame().getId());
            node.put("platformName", gamePlatform.getPlatform().getPlatform());
            gamePlatformArray.add(node);
        }
        objectNode.set("gamePlatforms", gamePlatformArray);
        return om.writeValueAsString(objectNode);
    }

    @PostMapping(value = "/save/{gamePlatformId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String save(@PathVariable("gamePlatformId") int gamePlatformId, @RequestBody String body)
            throws ResourceNotFoundException, IOException {
        GamePlatform gamePlatform = baseController.getGamePlatformService().findById(gamePlatformId)
                .orElseThrow(() -> new ResourceNotFoundException("No game platform with id " + gamePlatformId));
        ObjectMapper om = new ObjectMapper();
        JsonNode json = om.readTree(body);
        RipSoundtrack ripSoundtrack = new RipSoundtrack();
        String altTitle = json.has("altTitle") ? json.get("altTitle").asText("") : "";
        ripSoundtrack.setAltTitle(altTitle.isBlank() ? null : altTitle);
        ripSoundtrack.setLink(json.get("link").asText());
        ripSoundtrack.setRipSource(RipSource.valueOf(json.get("source").asText()));
        ripSoundtrack.setGamePlatform(gamePlatform);
        baseController.getRipSoundtrackService().save(ripSoundtrack);
        ObjectNode resp = om.createObjectNode();
        resp.put("id", ripSoundtrack.getId());
        resp.put("gamePlatformId", ripSoundtrack.getGamePlatform().getId());
        resp.put("platformId", ripSoundtrack.getGamePlatform().getPlatform().getId());
        resp.put("platformName", ripSoundtrack.getGamePlatform().getPlatform().getPlatform());
        return om.writeValueAsString(resp);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String update(@PathVariable("id") long id, @RequestBody String body)
            throws ResourceNotFoundException, IOException {
        RipSoundtrack rip = baseController.getRipSoundtrackService().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No rip soundtrack with id " + id));
        ObjectMapper om = new ObjectMapper();
        JsonNode json = om.readTree(body);
        String altTitle = json.has("altTitle") ? json.get("altTitle").asText("") : "";
        rip.setAltTitle(altTitle.isBlank() ? null : altTitle);
        rip.setLink(json.get("link").asText());
        rip.setRipSource(RipSource.valueOf(json.get("source").asText()));
        Integer gamePlatformId = json.get("gamePlatformId").asInt();
        GamePlatform gamePlatform = baseController.getGamePlatformService().findById(gamePlatformId)
                .orElseThrow(() -> new ResourceNotFoundException("No game platform with id " + id));
        rip.setGamePlatform(gamePlatform);
        baseController.getRipSoundtrackService().save(rip);
        return om.writeValueAsString("OK");
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) throws IOException {
        baseController.getRipSoundtrackService().deleteById(id);
        return new ObjectMapper().writeValueAsString("OK");
    }
}
