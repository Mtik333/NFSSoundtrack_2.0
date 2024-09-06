package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.GameSerializer;
import com.nfssoundtrack.racingsoundtracks.serializers.SerieSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * controller to handle creating / modifying / deleting series
 */
@Controller
@RequestMapping(path = "/serie")
public class SerieController extends BaseControllerWithErrorHandling {

    public static final String SERIES = "series";
    @Autowired
    GameSerializer gameSerializer;
    @Autowired
    SerieSerializer serieSerializer;

    /**
     * method to render all series from database
     * used in serieMgmt.js when clicking on 'manage series' button
     *
     * @return json with all series
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/readAll")
    public @ResponseBody
    String readSeries() throws JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(Serie.class, serieSerializer);
        List<Serie> series = serieService.findAllSortedByPositionAsc();
        return objectMapper.writeValueAsString(series);
    }

    /**
     * method to render specific serie as well as all of its games
     * used in serieMgmt.js when you click on 'edit' button in 'manage series'
     *
     * @param serieId id of serie to render
     * @return json with serie info and related games
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/read/{serieId}")
    public @ResponseBody
    String readGamesFromSerie(@PathVariable("serieId") int serieId)
            throws ResourceNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(Game.class, gameSerializer);
        Serie serie = serieService.findById(serieId).orElseThrow(
                () -> new ResourceNotFoundException("No serie found with id " + serieId));
        return objectMapper.writeValueAsString(serie.getGames());
    }

    /**
     * method to update positions of series in the database
     * used in serieMgmt.js when you click on 'update positions in DB' in 'manage series'
     *
     * @param formData info about all series and their new positions
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/updatePositions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSeriePositions(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
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
        if (cache != null) {
            cache.clear();
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method used to update the positions of games in series and potential change of serie name
     * used in serieMgmt.js when you click on 'edit' of serie in 'manage series'
     *
     * @param formData consists of basic serie info and list of games
     *                 each game will have its id and position provided
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/updateGames", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putGamesPositions(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
        Map<?, ?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
        String serieId = (String) objectMapper.get("serieId");
        String serieName = (String) objectMapper.get("serieName");
        //single object in arrayOfGame consists of game id and position
        List<?> arrayOfGames = (List<?>) objectMapper.get("arrayOfGames");
        Serie serie = serieService.findById(Integer.parseInt(serieId)).orElseThrow(
                () -> new ResourceNotFoundException("no serie with id found " + serieId));
        //we save the serie name although 99% of situation we will not change it
        serie.setName(serieName);
        serieService.save(serie);
        for (Object obj : arrayOfGames) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            long gameId = Long.parseLong(String.valueOf(linkedHashMap.get("gameId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            Game game = gameService.findById(Math.toIntExact(gameId)).orElseThrow(
                    () -> new ResourceNotFoundException("No game with id " +
                            "found " + gameId));
            //for each found game we gonna just update game's position in series
            game.setPosition(position);
            gameService.save(game);
        }
        //since we updated the positions of games in series, we notify db to re-fetch this info
        //so that info is updated from user perspective too
        Cache cache = cacheManager.getCache(SERIES);
        if (cache != null) {
            cache.clear();
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method to save new series in the database
     * used in seriesMgmt.js when you 'save' new serie through 'new series' button in 'manage series'
     *
     * @param formData just a serie name
     * @return OK if successful
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String saveNewSerie(@RequestBody String formData) throws JsonProcessingException {
        String newSerieName = new ObjectMapper().readValue(formData, String.class);
        Serie serie = new Serie(10000L, newSerieName);
        serieService.save(serie);
        Cache cache = cacheManager.getCache(SERIES);
        if (cache != null) {
            cache.clear();
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

}
