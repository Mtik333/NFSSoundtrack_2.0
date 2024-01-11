package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.GenreSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GenreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(path = "/genre")
public class GenreController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(GenreController.class);

    @Autowired
    GenreRepository genreRepository;

    @GetMapping(value = "/genreName/{input}")
    public @ResponseBody String readAliases(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString("[]");
        }
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Genre.class, new GenreSerializer(Genre.class));
        objectMapper.registerModule(simpleModule);
        if (input.length() <= 3) {
            Genre genre = genreRepository.findByGenreName(input);
            if (genre == null) {
                return objectMapper.writeValueAsString("[]");
            }
            String result = objectMapper.writeValueAsString(Collections.singleton(genre));
            return result;
        } else {
            List<Genre> genreList = genreRepository.findByGenreNameContains(input);
            if (genreList == null) {
                return objectMapper.writeValueAsString("[]");
            }
            String result = objectMapper.writeValueAsString(genreList);
            return result;
        }
    }

}