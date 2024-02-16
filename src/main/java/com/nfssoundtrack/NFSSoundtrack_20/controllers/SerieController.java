package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.GameSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.SerieSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/serie")
public class SerieController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(SerieController.class);
    @Autowired
    GameSerializer gameSerializer;
    @Autowired
    SerieSerializer serieSerializer;

    @GetMapping(value = "/readAll")
    public @ResponseBody
    String readSeries(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Serie.class, serieSerializer);
        objectMapper.registerModule(simpleModule);
        List<Serie> series = serieService.findAllSortedByPositionAsc();
        return objectMapper.writeValueAsString(series);
    }

    @GetMapping(value = "/read/{serieId}")
    public @ResponseBody
    String readGamesFromSerie(@PathVariable("serieId") int serieId, Model model) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Game.class, gameSerializer);
        objectMapper.registerModule(simpleModule);
        Serie serie = serieService.findById(serieId).orElseThrow(
                () -> new ResourceNotFoundException("No serie found with id " + serieId));
        return objectMapper.writeValueAsString(serie.getGames());
    }

    @PutMapping(value = "/updatePositions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putSeriePositions(@RequestBody String formData) throws Exception {
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            long serieId = Long.parseLong(String.valueOf(linkedHashMap.get("serieId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            Serie serie = serieService.findById(Math.toIntExact(serieId)).orElseThrow(
                    () -> new ResourceNotFoundException("no serie with id found " + serieId));
            serie.setPosition(position);
            serieService.save(serie);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/updateGames", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putGamesPositions(@RequestBody String formData) throws Exception {
        Map<?, ?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
        String serieId = (String) objectMapper.get("serieId");
        String serieName = (String) objectMapper.get("serieName");
        List<?> arrayOfGames = (List<?>) objectMapper.get("arrayOfGames");
        Serie serie = serieService.findById(Integer.parseInt(serieId)).orElseThrow(
                () -> new ResourceNotFoundException("no serie with id found " + serieId));
        serie.setName(serieName);
        serieService.save(serie);
        for (Object obj : arrayOfGames) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            long gameId = Long.parseLong(String.valueOf(linkedHashMap.get("gameId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            Game game = gameService.findById(Math.toIntExact(gameId)).orElseThrow(
                    () -> new ResourceNotFoundException("No game with id " +
                            "found " + gameId));
            game.setPosition(position);
            gameService.save(game);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String saveNewSerie(@RequestBody String formData) throws JsonProcessingException {
        String newSerieName = new ObjectMapper().readValue(formData, String.class);
        Serie serie = new Serie(10000L, newSerieName);
        serieService.save(serie);
        return new ObjectMapper().writeValueAsString("OK");
    }

}
