package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.GameStatus;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(path = "/gamedb")
public class GameController {

    @Autowired
    SerieRepository serieRepository;

    @Autowired
    GameRepository gameRepository;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String saveNewGame(@RequestBody String formData) throws JsonProcessingException {
        Map<?, ?> gameProps = new ObjectMapper().readValue(formData, Map.class);
        String serieId = (String) gameProps.get("serieId");
        Serie currentSerie = serieRepository.findById(Integer.valueOf(serieId)).get();
        String gameTitle = (String) gameProps.get("gameTitle");
        String gameShort = (String) gameProps.get("gameShort");
        String gamePrefix = (String) gameProps.get("gamePrefix");
        GameStatus gameStatus = GameStatus.valueOf((String) gameProps.get("gameStatus"));
        String spotifyId = (String) gameProps.get("spotifyId");
        String deezerId = (String) gameProps.get("deezerId");
        String tidalId = (String) gameProps.get("tidalId");
        String youtubeId = (String) gameProps.get("youtubeId");
        String soundcloudId = (String) gameProps.get("soundcloudId");
        Game newGame = new Game();
        newGame.setSerie(currentSerie);
        newGame.setGameTitle(gameTitle);
        newGame.setGameShort(gameShort);
        if (!"null".equals(gamePrefix) && !gamePrefix.equals("undefined")) {
            newGame.setPrefix(gamePrefix);
        }
        newGame.setDisplayTitle((newGame.getPrefix() + " " + newGame.getGameTitle()).trim());
        newGame.setGameStatus(gameStatus);
        if (!"".equals(spotifyId) && !spotifyId.equals("undefined")) {
            newGame.setSpotifyId(spotifyId);
        }
        if (!"".equals(deezerId) && !deezerId.equals("undefined")) {
            newGame.setDeezerId(deezerId);
        }
        if (!"".equals(tidalId) && !tidalId.equals("undefined")) {
            newGame.setTidalId(tidalId);
        }
        if (!"".equals(youtubeId) && !youtubeId.equals("undefined")) {
            newGame.setYoutubeId(youtubeId);
        }
        if (!"".equals(soundcloudId) && !soundcloudId.equals("undefined")) {
            newGame.setSoundcloudId(soundcloudId);
        }
        newGame.setPosition(10000L);
        gameRepository.save(newGame);
        return new ObjectMapper().writeValueAsString("OK");
    }
}
