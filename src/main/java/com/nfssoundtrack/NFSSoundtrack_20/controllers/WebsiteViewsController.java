package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.ContentRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GameRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SerieRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller

public class WebsiteViewsController implements ErrorController {

    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @RequestMapping(value = "/")
    public String mainPage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
        return "redirect:/content/home";
    }

    @RequestMapping(value = "/manage")
    public String manage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
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
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
        return "login";
    }

    @RequestMapping(value = "/content/{value}")
    public String topMenuEntry(Model model, @PathVariable("value") String value, HttpServletResponse response) throws IOException {
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("htmlToInject",
                contentRepository.findByContentShort(value).getContentData());
        model.addAttribute("songSubgroups", null);
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
        model.addAttribute("appName", value + " - " + appName);
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
        model.addAttribute("endpoint", "/game/" + gameshort);
        model.addAttribute("game", game);
        model.addAttribute("appName", game.getDisplayTitle() + " soundtrack at NFSSoundtrack.com");
        model.addAttribute("gamegroups", game.getMainGroups());
        model.addAttribute("songSubgroups", allSongs);
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
        model.addAttribute("customPlaylist", null);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        //model.addAttribute("games", gameRepository.findAll());
        return "index";
    }

    @PostMapping(value = "/custom/playlist")
    public String getCustomPlaylist(Model model, @RequestBody String customPlaylist) throws JsonProcessingException, UnsupportedEncodingException {
        List<SongSubgroup> songSubgroupList = new ArrayList<>();
        if (customPlaylist == null || customPlaylist.isEmpty()) {

        } else {
            String result = URLDecoder.decode(customPlaylist, StandardCharsets.UTF_8.name());
            String basicallyArray = result.replace("customPlaylist=", "");
            List<String> finalList = new ObjectMapper().readValue(basicallyArray, List.class);
            for (String songSubgroupId : finalList) {
                SongSubgroup songSubgroup = songSubgroupRepository.findById(Integer.valueOf(songSubgroupId)).get();
                songSubgroupList.add(songSubgroup);
            }
        }
        model.addAttribute("appName", "Custom playlist - NFSSoundtrack.com");
        model.addAttribute("gamegroups", null);
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
        model.addAttribute("search", null);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("customPlaylist", songSubgroupList);
        return "index";
    }

    @RequestMapping(value = "/{otherval}")
    public String nonExistingPagee(Model model, @PathVariable("otherval") String otherval) throws Exception {
        throw new Exception("Tried to access non-existing page: " + otherval);
    }

    @ExceptionHandler
    @ResponseBody
    public ModelAndView handleException(HttpServletRequest req, Exception ex) {
//        logger.error("Request: " + req.getRequestURL() + " raised " + ex);
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("stacktrace", ex.getStackTrace());
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        mav.addObject("appName", "Error NFSSoundtrack.com");
        mav.addObject("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        return mav;
    }
}