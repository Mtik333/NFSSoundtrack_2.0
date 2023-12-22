package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.ContentRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.MainGroupRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    @Autowired
    private MainGroupRepository mainGroupRepository;

    @RequestMapping(value="/")
    public String index(Model model){
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        return "index";
    }

    @RequestMapping(value="/{value}")
    public String topMenuEntry(Model model, @PathVariable String value, HttpServletResponse response) throws IOException {
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("htmlToInject",
                contentRepository.findByContentShort(value).getContentData());
//        response.sendRedirect("index");
        return "index";
    }

    @RequestMapping(value="/game/{gameshort}")
    public String game(Model model, @PathVariable String gameshort){
        Game game = gameRepository.findById(Integer.valueOf(gameshort)).get();
        List<SongSubgroup> allSongs = new ArrayList<>();
        for (MainGroup mainGroup : game.getMainGroups()){
            for (Subgroup subgroup : mainGroup.getSubgroups()){
                allSongs.addAll(subgroup.getSongSubgroupList());
            }
        }
        model.addAttribute("gamegroups", mainGroupRepository.findByGameId(Long.valueOf(gameshort)));
        model.addAttribute("songSubgroups", allSongs);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        return "index";
    }


}
