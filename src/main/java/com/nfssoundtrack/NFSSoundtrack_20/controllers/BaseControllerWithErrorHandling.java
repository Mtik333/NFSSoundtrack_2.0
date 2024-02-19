package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.nfssoundtrack.NFSSoundtrack_20.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileNotFoundException;

public class BaseControllerWithErrorHandling implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(BaseControllerWithErrorHandling.class);

    @Autowired
    SerieService serieService;

    @Autowired
    ContentService contentService;

    @Autowired
    GameService gameService;

    @Autowired
    GenreService genreService;

    @Autowired
    SongSubgroupService songSubgroupService;

    @Autowired
    SongService songService;

    @Autowired
    AuthorService authorService;

    @Autowired
    AuthorAliasService authorAliasService;

    @Autowired
    AuthorSongService authorSongService;

    @Autowired
    SongGenreService songGenreService;

    @Autowired
    CountryService countryService;

    @Autowired
    AuthorCountryService authorCountryService;

    @Autowired
    MainGroupService mainGroupService;

    @Autowired
    SubgroupService subgroupService;

    @Autowired
    TodaysSongService todaysSongService;

    @Autowired
    CorrectionService correctionService;
    /**
     *
     * @param otherval invalid input endpoint
     * @return error page
     * @throws FileNotFoundException exception that indicates wrong endpoint visited
     */
    @RequestMapping(value = "/{otherval}")
    public String nonExistingPage(@PathVariable("otherval") String otherval) throws FileNotFoundException {
        logger.error("otherval " + otherval);
        throw new FileNotFoundException("Tried to access non-existing page: " + otherval);
    }

}
