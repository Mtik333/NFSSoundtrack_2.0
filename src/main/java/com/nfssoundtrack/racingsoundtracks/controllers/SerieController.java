package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.GameSerializer;
import com.nfssoundtrack.racingsoundtracks.serializers.SerieSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
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

    public static final String SERIES = "series";
    @Autowired
    GameSerializer gameSerializer;
    @Autowired
    SerieSerializer serieSerializer;

    @GetMapping(value = "/readAll")
    public @ResponseBody
    String readSeries() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Serie.class, serieSerializer);
        objectMapper.registerModule(simpleModule);
        List<Serie> series = serieService.findAllSortedByPositionAsc();
        return objectMapper.writeValueAsString(series);
    }

    @GetMapping(value = "/read/{serieId}")
    public @ResponseBody
    String readGamesFromSerie(@PathVariable("serieId") int serieId) throws ResourceNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Game.class, gameSerializer);
        objectMapper.registerModule(simpleModule);
        Serie serie = serieService.findById(serieId).orElseThrow(
                () -> new ResourceNotFoundException("No serie found with id " + serieId));
        return objectMapper.writeValueAsString(serie.getGames());
    }

    @PutMapping(value = "/updatePositions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putSeriePositions(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
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
        Cache cache = cacheManager.getCache(SERIES);
        if (cache!=null){
            cache.clear();
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/updateGames", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putGamesPositions(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
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
        Cache cache = cacheManager.getCache(SERIES);
        if (cache!=null){
            cache.clear();
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String saveNewSerie(@RequestBody String formData) throws JsonProcessingException {
        String newSerieName = new ObjectMapper().readValue(formData, String.class);
        Serie serie = new Serie(10000L, newSerieName);
        serieService.save(serie);
        Cache cache = cacheManager.getCache(SERIES);
        if (cache!=null){
            cache.clear();
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

}
