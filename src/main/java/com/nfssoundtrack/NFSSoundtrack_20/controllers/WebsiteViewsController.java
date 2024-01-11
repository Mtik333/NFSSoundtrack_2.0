package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.SongSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller

public class WebsiteViewsController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteViewsController.class);
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

    @Autowired
    private SongRepository songRepository;

    @RequestMapping(value = "/")
    public String mainPage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
        return "redirect:/content/home";
    }

    @RequestMapping(value = "/manage/manage")
    public String manage(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
//        model.addAttribute("games", gameRepository.findAll());
        return "manage";
    }

    @RequestMapping(value = "/login")
    public String login(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
//        model.addAttribute("games", gameRepository.findAll());
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
        model.addAttribute("home", value.contains("home"));
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
//        model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("customPlaylist", songSubgroupList);
        return "index";
    }

//    @RequestMapping(value = "/{otherval}")
//    public String nonExistingPagee(Model model, @PathVariable("otherval") String otherval) throws Exception {
////        throw new Exception("Tried to access non-existing page: " + otherval);
//        return "redirect:/content/home";
//    }

    @RequestMapping(value = "/songInfo/{songId}")
    public @ResponseBody String provideSongModalInfo(Model model, @PathVariable("songId") String songId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = songRepository.findById(Integer.valueOf(songId)).get();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Song.class, new SongSerializer(Song.class));
        objectMapper.registerModule(simpleModule);
        String result = objectMapper.writeValueAsString(song);
        return result;
    }

    @RequestMapping(value = "/song/{songId}")
    public String provideSongInfo(Model model, @PathVariable("songId") String songId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = songRepository.findById(Integer.valueOf(songId)).get();
        List<SongSubgroup> usages = songSubgroupRepository.findBySong(song);
        model.addAttribute("appName", "Custom playlist - NFSSoundtrack.com");
        model.addAttribute("gamegroups", null);
        model.addAttribute("author", null);
        model.addAttribute("genre", null);
        model.addAttribute("search", null);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
//        model.addAttribute("games", gameRepository.findAll());
        model.addAttribute("customPlaylist", null);
        model.addAttribute("songToCheck", song);
        model.addAttribute("songUsages", usages);
        return "index";
    }

//    @ExceptionHandler
//    @ResponseBody
//    public ModelAndView handleException(HttpServletRequest req, Exception ex) {
////        logger.error("Request: " + req.getRequestURL() + " raised " + ex);
//        ModelAndView mav = new ModelAndView();
//        mav.addObject("exception", ex);
//        mav.addObject("stacktrace", ex.getStackTrace());
//        mav.addObject("url", req.getRequestURL());
//        mav.setViewName("error");
//        mav.addObject("appName", "Error NFSSoundtrack.com");
//        mav.addObject("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
//        return mav;
//    }
}
