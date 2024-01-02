package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.ArtistSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.others.GameSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.others.SerieSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/serie")
public class SerieController {

    @Autowired
    SerieRepository serieRepository;

    @Autowired
    GameRepository gameRepository;
    @GetMapping(value = "/readAll")
    public @ResponseBody String readSeries(Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Serie.class, new SerieSerializer(Serie.class));
        objectMapper.registerModule(simpleModule);
        List<Serie> series = serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position"));
        return objectMapper.writeValueAsString(series);
    }

    @GetMapping(value = "/read/{serieId}")
    public @ResponseBody String readGamesFromSerie(@PathVariable("serieId") String serieId,Model model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Game.class, new GameSerializer(Game.class));
        objectMapper.registerModule(simpleModule);
        Serie serie = serieRepository.findById(Integer.valueOf(serieId)).get();
        return objectMapper.writeValueAsString(serie.getGames());
    }

    @PutMapping(value = "/updatePositions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putSeriePositions(@RequestBody String formData) throws JsonProcessingException {
        System.out.println("???");
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            Long serieId = Long.parseLong(String.valueOf(linkedHashMap.get("serieId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            Serie serie = serieRepository.findById(Math.toIntExact(serieId)).get();
            serie.setPosition(position);
            serieRepository.save(serie);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/updateGames", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putGamesPositions(@RequestBody String formData) throws JsonProcessingException {
        System.out.println("???");
        Map<?,?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
        String serieId = (String) objectMapper.get("serieId");
        String serieName = (String) objectMapper.get("serieName");
        List<?> arrayOfGames = (List<?>) objectMapper.get("arrayOfGames");
        Serie serie = serieRepository.findById(Integer.valueOf(serieId)).get();
        serie.setName(serieName);
        serieRepository.save(serie);
        for (Object obj : arrayOfGames) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            Long gameId = Long.parseLong(String.valueOf(linkedHashMap.get("gameId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            Game game = gameRepository.findById(Math.toIntExact(gameId)).get();
            game.setPosition(position);
            gameRepository.save(game);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String saveNewSerie(@RequestBody String formData) throws JsonProcessingException {
        String newSerieName = new ObjectMapper().readValue(formData, String.class);
        Serie serie = new Serie();
        serie.setPosition(10000L);
        serie.setName(newSerieName);
        serieRepository.save(serie);
        return new ObjectMapper().writeValueAsString("OK");
    }

}
