package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.*;

@Controller

public class MainController {

    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ContentRepository contentRepository;



    @RequestMapping(value="/")
    public String index(Model model){
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
//        model.addAttribute("games", gameRepository.findAll());
        return "index";
    }

    @RequestMapping(value="/manage")
    public String manage(Model model){
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        model.addAttribute("games", gameRepository.findAll());
        return "manage";
    }

    @RequestMapping(value="/{value}")
    public String topMenuEntry(Model model, @PathVariable("value") String value, HttpServletResponse response) throws IOException {
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        if (!value.isEmpty() && !"manage".equals(value)){
            model.addAttribute("htmlToInject",
                contentRepository.findByContentShort(value).getContentData());
        }
//        response.sendRedirect("index");
        return "index";
    }

    @RequestMapping(value="/game/{gameshort}")
    public String game(Model model, @PathVariable("gameshort") String gameshort){
        Game game = gameRepository.findByGameShort(gameshort);
        List<SongSubgroup> allSongs = new ArrayList<>();
        for (MainGroup mainGroup : game.getMainGroups()){
            for (Subgroup subgroup : mainGroup.getSubgroups()){
                allSongs.addAll(subgroup.getSongSubgroupList());
            }
        }
        model.addAttribute("gamegroups", game.getMainGroups());
        model.addAttribute("songSubgroups", allSongs);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        return "index";
    }

}
