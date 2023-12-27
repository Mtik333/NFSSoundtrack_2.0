package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/genre")
public class GenreController {

    @GetMapping(value = "/read/{genreId}")
    public @ResponseBody String readGenreInfo(Model model, @PathVariable("genreId") String genreId) throws JsonProcessingException {
        return "OK";
    }
}