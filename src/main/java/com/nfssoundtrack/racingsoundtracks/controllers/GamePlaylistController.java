package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.GamePlaylist;
import com.nfssoundtrack.racingsoundtracks.dbmodel.PlaylistPlatform;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.services.GamePlaylistService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/gamePlaylist")
public class GamePlaylistController {

    private final GamePlaylistService gamePlaylistService;
    private final BaseControllerWithErrorHandling baseController;

    public GamePlaylistController(GamePlaylistService gamePlaylistService,
                                  BaseControllerWithErrorHandling baseController) {
        this.gamePlaylistService = gamePlaylistService;
        this.baseController = baseController;
    }

    @GetMapping("/byGame/{gameId}")
    public String getByGame(@PathVariable("gameId") int gameId)
            throws ResourceNotFoundException, IOException {
        Game game = baseController.getGameService().findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("No game with id " + gameId));
        List<GamePlaylist> playlists = gamePlaylistService.findByGame(game);
        ObjectMapper om = new ObjectMapper();
        ArrayNode arr = om.createArrayNode();
        for (GamePlaylist p : playlists) {
            ObjectNode node = om.createObjectNode();
            node.put("id", p.getId());
            node.put("platform", p.getPlatform().name());
            node.put("playlistUrl", p.getPlaylistUrl());
            node.put("label", p.getLabel() != null ? p.getLabel() : "");
            arr.add(node);
        }
        return om.writeValueAsString(arr);
    }

    @PostMapping(value = "/save/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String save(@PathVariable("gameId") int gameId, @RequestBody String body)
            throws ResourceNotFoundException, IOException {
        Game game = baseController.getGameService().findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("No game with id " + gameId));
        ObjectMapper om = new ObjectMapper();
        JsonNode json = om.readTree(body);
        GamePlaylist p = new GamePlaylist();
        p.setGame(game);
        p.setPlatform(PlaylistPlatform.valueOf(json.get("platform").asText()));
        p.setPlaylistUrl(json.get("playlistUrl").asText());
        String label = json.has("label") ? json.get("label").asText("") : "";
        p.setLabel(!label.isBlank() ? label : null);
        p = gamePlaylistService.save(p);
        ObjectNode resp = om.createObjectNode();
        resp.put("id", p.getId());
        return om.writeValueAsString(resp);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String update(@PathVariable("id") long id, @RequestBody String body)
            throws ResourceNotFoundException, IOException {
        GamePlaylist p = gamePlaylistService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No playlist with id " + id));
        ObjectMapper om = new ObjectMapper();
        JsonNode json = om.readTree(body);
        p.setPlatform(PlaylistPlatform.valueOf(json.get("platform").asText()));
        p.setPlaylistUrl(json.get("playlistUrl").asText());
        String label = json.has("label") ? json.get("label").asText("") : "";
        p.setLabel(!label.isBlank() ? label : null);
        gamePlaylistService.save(p);
        return om.writeValueAsString("OK");
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) throws IOException {
        gamePlaylistService.deleteById(id);
        return new ObjectMapper().writeValueAsString("OK");
    }
}
