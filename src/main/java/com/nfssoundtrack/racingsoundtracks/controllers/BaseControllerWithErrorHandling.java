package com.nfssoundtrack.racingsoundtracks.controllers;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Changelog;
import com.nfssoundtrack.racingsoundtracks.dbmodel.EntityType;
import com.nfssoundtrack.racingsoundtracks.dbmodel.EntityUrl;
import com.nfssoundtrack.racingsoundtracks.services.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.*;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * just a wannabe-controller to keep track on various services across other controllers
 */
@Service
public class BaseControllerWithErrorHandling implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(BaseControllerWithErrorHandling.class);

    @Value("${bot.token}")
    private String botSecret;

    @Value("${changelog.id}")
    private String changeLogId;

    private TextChannel channelForChangelog;
    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final CacheManager cacheManager;
    private final SerieService serieService;
    private final ContentService contentService;
    private final GameService gameService;
    private final GenreService genreService;
    private final SongSubgroupService songSubgroupService;
    private final SongService songService;
    private final AuthorService authorService;
    private final AuthorAliasService authorAliasService;
    private final AuthorSongService authorSongService;
    private final SongGenreService songGenreService;
    private final CountryService countryService;
    private final AuthorCountryService authorCountryService;
    private final MainGroupService mainGroupService;
    private final SubgroupService subgroupService;
    private final TodaysSongService todaysSongService;
    private final CorrectionService correctionService;
    private final CustomThemeService customThemeService;
    private final AuthorMemberService authorMemberService;
    private final ChangelogService changelogService;
    private final MessageSource messageSource;

    public BaseControllerWithErrorHandling(CacheManager cacheManager, SerieService serieService,
                                           ContentService contentService, GameService gameService,
                                           GenreService genreService, SongSubgroupService songSubgroupService,
                                           SongService songService, AuthorService authorService,
                                           AuthorAliasService authorAliasService, AuthorSongService authorSongService,
                                           SongGenreService songGenreService, CountryService countryService,
                                           AuthorCountryService authorCountryService, MainGroupService mainGroupService,
                                           SubgroupService subgroupService, TodaysSongService todaysSongService,
                                           CorrectionService correctionService, CustomThemeService customThemeService,
                                           AuthorMemberService authorMemberService, ChangelogService changelogService,
                                           MessageSource messageSource) {
        this.cacheManager = cacheManager;
        this.serieService = serieService;
        this.contentService = contentService;
        this.gameService = gameService;
        this.genreService = genreService;
        this.songSubgroupService = songSubgroupService;
        this.songService = songService;
        this.authorService = authorService;
        this.authorAliasService = authorAliasService;
        this.authorSongService = authorSongService;
        this.songGenreService = songGenreService;
        this.countryService = countryService;
        this.authorCountryService = authorCountryService;
        this.mainGroupService = mainGroupService;
        this.subgroupService = subgroupService;
        this.todaysSongService = todaysSongService;
        this.correctionService = correctionService;
        this.customThemeService = customThemeService;
        this.authorMemberService = authorMemberService;
        this.changelogService = changelogService;
        this.messageSource = messageSource;
    }


    /**
     * when trying to access page (game) that does not really exist on website
     *
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
     *
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
     *
     * @param gameShort url of game
     */
    void removeCacheEntry(String gameShort) {
        Cache cache = cacheManager.getCache("findByGameShort");
        if (cache != null) {
            cache.evict(gameShort);
        }
    }

    public void setupChannelForChangelog(String channelId) throws InterruptedException {
        JDA jda = WebsiteViewsController.rebuildJda(botSecret);
        channelForChangelog = jda.getTextChannelById(channelId);
    }

    public void sendMessageToChannel(EntityType entityType, String operationType, String text,
                                     EntityUrl entityUrl, String urlLabel, String urlValue) {
        try {
            LocalDateTime localDate = LocalDateTime.now();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(entityType.value() + " - " + operationType + " at " + localDate.format(TIME_FORMATTER));
            eb.setThumbnail("https://racingsoundtracks.com/images/racingsoundtracks.png");
            eb.setColor(Color.red);
            eb.setDescription(text);
            MessageEmbed embed = eb.build();
            if (channelForChangelog == null) {
                setupChannelForChangelog(changeLogId);
            }
            channelForChangelog.sendMessageEmbeds(embed).queue();
            saveLog(entityType, operationType, text,
                    entityUrl, urlLabel, urlValue, Timestamp.valueOf(localDate));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("exception in sending message: {}", e.getMessage());
        }
    }

    private void saveLog(EntityType entityType, String operationType, String logText,
                         EntityUrl entityUrl, String urlLabel, String urlValue,
                         Timestamp timestamp) {
        Changelog changelog = new Changelog(logText, operationType, entityType,
                entityUrl, urlLabel, urlValue, timestamp);
        changelogService.save(changelog);
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public SerieService getSerieService() {
        return serieService;
    }

    public ContentService getContentService() {
        return contentService;
    }

    public GameService getGameService() {
        return gameService;
    }

    public GenreService getGenreService() {
        return genreService;
    }

    public SongSubgroupService getSongSubgroupService() {
        return songSubgroupService;
    }

    public SongService getSongService() {
        return songService;
    }

    public AuthorService getAuthorService() {
        return authorService;
    }

    public AuthorAliasService getAuthorAliasService() {
        return authorAliasService;
    }

    public AuthorSongService getAuthorSongService() {
        return authorSongService;
    }

    public SongGenreService getSongGenreService() {
        return songGenreService;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public AuthorCountryService getAuthorCountryService() {
        return authorCountryService;
    }

    public MainGroupService getMainGroupService() {
        return mainGroupService;
    }

    public SubgroupService getSubgroupService() {
        return subgroupService;
    }

    public TodaysSongService getTodaysSongService() {
        return todaysSongService;
    }

    public CorrectionService getCorrectionService() {
        return correctionService;
    }

    public CustomThemeService getCustomThemeService() {
        return customThemeService;
    }

    public AuthorMemberService getAuthorMemberService() {
        return authorMemberService;
    }

    public ChangelogService getChangelogService() {
        return changelogService;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }
}
