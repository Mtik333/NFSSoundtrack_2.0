package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.GenreSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(path = "/genre")
public class GenreController extends BaseControllerWithErrorHandling {

	private static final Logger logger = LoggerFactory.getLogger(GenreController.class);

	@Autowired
	GenreSerializer genreSerializer;

	@GetMapping(value = "/genreName/{input}")
	public @ResponseBody
	String readAliases(@PathVariable("input") String input) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		if (input.isEmpty()) {
			return objectMapper.writeValueAsString(null);
		}
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Genre.class, genreSerializer);
		objectMapper.registerModule(simpleModule);
		if (input.length() <= 3) {
			Optional<Genre> genre = genreService.findByGenreName(input);
			if (genre.isEmpty()) {
				return objectMapper.writeValueAsString(null);
			}
			String result = objectMapper.writeValueAsString(Collections.singleton(genre.get()));
			return result;
		} else {
			List<Genre> genreList = genreService.findByGenreNameContains(input);
			if (genreList == null) {
				return objectMapper.writeValueAsString(null);
			}
			String result = objectMapper.writeValueAsString(genreList);
			return result;
		}
	}

}