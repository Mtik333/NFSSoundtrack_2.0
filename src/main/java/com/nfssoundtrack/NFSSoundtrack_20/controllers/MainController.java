package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.ContentRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
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


    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("appName", appName);
        //List<Serie> series = serieRepository.findAll();
        //model.addAttribute("series", serieRepository.findAll());
        //serieRepository.findByIdNotNull();
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
//        model.addAttribute("songSubgroups",null);
//        model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("author",null);
        model.addAttribute("genre",null);
        return "redirect:/content/home";
    }

    @RequestMapping(value = "/manage")
    public String manage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("author",null);
        model.addAttribute("genre",null);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        model.addAttribute("games", gameRepository.findAll());
        return "manage";
    }

    @RequestMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("login", true);
        model.addAttribute("author",null);
        model.addAttribute("genre",null);
        return "login";
    }

    @RequestMapping(value = "/loginMe")
    public String loginMe(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("login", true);
        model.addAttribute("author",null);
        model.addAttribute("genre",null);
        return "login";
    }
    @RequestMapping(value = "/content/{value}")
    public String topMenuEntry(Model model, @PathVariable("value") String value, HttpServletResponse response) throws IOException {
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("htmlToInject",
                    contentRepository.findByContentShort(value).getContentData());
        model.addAttribute("songSubgroups",null);
        model.addAttribute("author",null);
        model.addAttribute("genre",null);
//        response.sendRedirect("index");
        return "index";
    }

    @RequestMapping(value = "/game/{gameshort}")
    public String game(Model model, @PathVariable("gameshort") String gameshort) {
        Game game = gameRepository.findByGameShort(gameshort);
        List<SongSubgroup> allSongs = new ArrayList<>();
        for (MainGroup mainGroup : game.getMainGroups()) {
            for (Subgroup subgroup : mainGroup.getSubgroups()) {
                allSongs.addAll(subgroup.getSongSubgroupList());
            }
        }
        model.addAttribute("endpoint", "/game/"+gameshort);
        model.addAttribute("game", game);
        model.addAttribute("gamegroups", game.getMainGroups());
        model.addAttribute("songSubgroups", allSongs);
        model.addAttribute("author",null);
        model.addAttribute("genre",null);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        return "index";
    }

}
