package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Country;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Genre;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.serializers.GenreSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * i screwed up and there are some endpoint with similar start in main controller class
 * this single one is used in songsMgmt.js so it should not be exposed to non-authenticated user
 */
@Controller
@RequestMapping(path = "/genre")
public class GenreController extends BaseControllerWithErrorHandling {

    @Autowired
    GenreSerializer genreSerializer;

    @GetMapping(value = "/readAll")
    public @ResponseBody
    String readGenres() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Genre> genres = genreService.findAll();
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
    public @ResponseBody
    String readAliases(@PathVariable("genreInput") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(Genre.class, genreSerializer);
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        if (input.length() <= 3) {
            //again if genre is short like EDM, we just look for exact value in databsae and return
            Optional<Genre> genre = genreService.findByGenreName(input);
            if (genre.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(Collections.singleton(genre.get()));
        } else {
            List<Genre> genreList = genreService.findByGenreNameContains(input);
            if (genreList == null) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(genreList);
        }
    }

}