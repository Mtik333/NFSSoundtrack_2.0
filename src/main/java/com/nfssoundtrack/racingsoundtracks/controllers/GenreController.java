package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Genre;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.serializers.GenreSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * i screwed up and there are some endpoint with similar start in main controller class
 * this single one is used in songsMgmt.js so it should not be exposed to non-authenticated user
 */
@RestController
@RequestMapping(path = "/genre")
public class GenreController {

    private final BaseControllerWithErrorHandling baseController;
    private final GenreSerializer genreSerializer;

    public GenreController(BaseControllerWithErrorHandling baseController, GenreSerializer genreSerializer) {
        this.baseController = baseController;
        this.genreSerializer = genreSerializer;
    }


    @GetMapping(value = "/readAll")
    public String readGenres() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Genre> genres = baseController.getGenreService().findAll();
        return objectMapper.writeValueAsString(genres);
    }

    /**
     * method to get genres with similar name from database
     * used in songMgmt.js when you type in "genre" when creating new song or editing song globally
     *
     * @param input text with supposed genre name
     * @return json list of genres matching input criteria
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/genreName/{genreInput}")
    public String readAliases(@PathVariable("genreInput") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(Genre.class, genreSerializer);
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        if (input.length() <= 3) {
            //again if genre is short like EDM, we just look for exact value in databsae and return
            Optional<Genre> genre = baseController.getGenreService().findByGenreName(input);
            if (genre.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(Collections.singleton(genre.get()));
        } else {
            List<Genre> genreList = baseController.getGenreService().findByGenreNameContains(input);
            if (genreList == null) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(genreList);
        }
    }

}