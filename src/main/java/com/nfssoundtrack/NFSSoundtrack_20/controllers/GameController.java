package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.GameStatus;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.GameEditSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping(path = "/gamedb")
public class GameController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    SerieRepository serieRepository;

    @Autowired
    GameRepository gameRepository;

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String saveNewGame(@RequestBody String formData) throws Exception {
        Map<String, String> gameProps = new ObjectMapper().readValue(formData,
                TypeFactory.defaultInstance().constructMapType(Map.class, String.class, String.class));
        String serieId = gameProps.get("serieId");
        Serie currentSerie = serieService.findById(Integer.parseInt(serieId)).orElseThrow(() -> new Exception("No " +
                "serie with id found " + serieId));
        Game game = new Game();
        String gameTitle = gameProps.get("gameTitle");
        String displayTitle = gameProps.get("displayTitle");
        String gameShort = gameProps.get("gameShort");
        String gamePrefix = gameProps.get("gamePrefix");
        GameStatus gameStatus = GameStatus.valueOf(gameProps.get("gameStatus"));
        String spotifyId = gameProps.get("spotifyId");
        String deezerId = gameProps.get("deezerId");
        String tidalId = gameProps.get("tidalId");
        String youtubeId = gameProps.get("youtubeId");
        String soundcloudId =gameProps.get("soundcloudId");
        Game newGame = new Game();
        newGame.setSerie(currentSerie);
        newGame.setGameTitle(gameTitle);
        newGame.setGameShort(gameShort);
        if (!"null".equals(gamePrefix) && !gamePrefix.equals("undefined")) {
            newGame.setPrefix(gamePrefix);
        }
        newGame.setDisplayTitle(displayTitle);
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

    @GetMapping(value = "/read/{gameId}")
    public @ResponseBody String gameGroupManage(Model model, @PathVariable("gameId") String gameId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        Game game = gameRepository.findById(Integer.valueOf(gameId)).get();
        simpleModule.addSerializer(Game.class, new GameEditSerializer(Game.class));
        objectMapper.registerModule(simpleModule);
        String result = objectMapper.writeValueAsString(game);
        return result;
    }

    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String modifyGame(@RequestBody String formData) throws JsonProcessingException {
		//readerforupdating
        Map<?, ?> gameProps = new ObjectMapper().readValue(formData, Map.class);
        String id = gameProps.get("id").toString();
        String gameTitle = (String) gameProps.get("gameTitle");
        String displayTitle = (String) gameProps.get("displayTitle");
        String gameShort = (String) gameProps.get("gameShort");
        String gamePrefix = (String) gameProps.get("gamePrefix");
        GameStatus gameStatus = GameStatus.valueOf((String) gameProps.get("gameStatus"));
        String spotifyId = (String) gameProps.get("spotifyId");
        String deezerId = (String) gameProps.get("deezerId");
        String tidalId = (String) gameProps.get("tidalId");
        String youtubeId = (String) gameProps.get("youtubeId");
        String soundcloudId = (String) gameProps.get("soundcloudId");
        Game gameToEdit = gameRepository.findById(Integer.valueOf(id)).get();
        gameToEdit.setGameTitle(gameTitle);
        gameToEdit.setGameShort(gameShort);
        if (!"null".equals(gamePrefix) && !gamePrefix.equals("undefined")) {
            gameToEdit.setPrefix(gamePrefix);
        }
        gameToEdit.setDisplayTitle(displayTitle);
        gameToEdit.setGameStatus(gameStatus);
        if (!"".equals(spotifyId) && !spotifyId.equals("undefined")) {
            gameToEdit.setSpotifyId(spotifyId);
        } else {
            gameToEdit.setSpotifyId(null);
        }
        if (!"".equals(deezerId) && !deezerId.equals("undefined")) {
            gameToEdit.setDeezerId(deezerId);
        } else {
            gameToEdit.setDeezerId(null);
        }
        if (!"".equals(tidalId) && !tidalId.equals("undefined")) {
            gameToEdit.setTidalId(tidalId);
        } else {
            gameToEdit.setTidalId(null);
        }
        if (!"".equals(youtubeId) && !youtubeId.equals("undefined")) {
            gameToEdit.setYoutubeId(youtubeId);
        } else {
            gameToEdit.setYoutubeId(null);
        }
        if (!"".equals(soundcloudId) && !soundcloudId.equals("undefined")) {
            gameToEdit.setSoundcloudId(soundcloudId);
        } else {
            gameToEdit.setSoundcloudId(null);
        }
        gameRepository.save(gameToEdit);
        return new ObjectMapper().writeValueAsString("OK");
    }
}
