package com.nfssoundtrack.racingsoundtracks.controllers;

import com.nfssoundtrack.racingsoundtracks.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * just a wannabe-controller to keep track on various services across other controllers
 */
public class BaseControllerWithErrorHandling implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(BaseControllerWithErrorHandling.class);

    @Autowired
    CacheManager cacheManager;

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

    @Autowired
    CustomThemeService customThemeService;

    @Autowired
    private MessageSource messageSource;
    /**
     * when trying to access page (game) that does not really exist on website
     * @param otherval invalid input endpoint
     * @return error page
     * @throws FileNotFoundException exception that indicates wrong endpoint visited
     */
    @RequestMapping(value = "/{otherval}")
    public String nonExistingPage(@PathVariable("otherval") String otherval) throws FileNotFoundException {
        logger.error("otherval {}", otherval);
        throw new FileNotFoundException("Tried to access non-existing page: " + otherval);
    }

    /**
     * used to show title bar page in specified user language
     * @param translationKey key that you can find in message.properties
     * @return value of title bar
     */
    String getLocalizedMessage(String translationKey, String[] params) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(translationKey, params, locale);
    }

    /**
     * cleaning the cache when game or its content was modified
     * otherwise you would not see the difference in UI until application is restarted
     * @param gameShort url of game
     */
    void removeCacheEntry(String gameShort){
        Cache cache = cacheManager.getCache("findByGameShort");
        if (cache!=null){
            cache.evict(gameShort);
        }
    }
}
