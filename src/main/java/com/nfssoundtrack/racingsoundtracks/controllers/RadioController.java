package com.nfssoundtrack.racingsoundtracks.controllers;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SubgroupType;
import com.nfssoundtrack.racingsoundtracks.radioserializers.RadioSerieSerializer;
import com.nfssoundtrack.racingsoundtracks.services.SongSubgroupService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/radio")
public class RadioController {

    private final BaseControllerWithErrorHandling baseController;
    private final RadioSerieSerializer radioSerieSerializer;
    private final Random random = new Random();

    public RadioController(BaseControllerWithErrorHandling baseController, RadioSerieSerializer radioSerieSerializer) {
        this.baseController = baseController;
        this.radioSerieSerializer = radioSerieSerializer;
    }

    @GetMapping(value = "/series")
    public String getAllSeries() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Serie> seriesList = baseController.getSerieService().findAll();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Serie.class, radioSerieSerializer);
        objectMapper.registerModule(simpleModule);
        return objectMapper.writeValueAsString(seriesList);
    }

    @GetMapping(value = "/genres")
    public List<Map<String, Object>> getGenres() {
        return baseController.getGenreService().findAll().stream()
                .sorted(Comparator.comparing(g -> g.getGenreName()))
                .map(g -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", g.getId());
                    m.put("name", g.getGenreName());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/authors/search")
    public List<Map<String, Object>> searchAuthors(@RequestParam(defaultValue = "") String query) {
        if (query.isBlank()) return List.of();
        return baseController.getAuthorService().searchByName(query).stream()
                .map(a -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", a.getId());
                    m.put("name", a.getName());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/series-with-games")
    public List<Map<String, Object>> getSeriesWithGames() {
        List<Serie> series = baseController.getSerieService().findAllSortedByPositionAsc();
        return series.stream().map(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", s.getId());
            m.put("name", s.getName());
            List<Map<String, Object>> games = s.getGames().stream()
                    .sorted(Comparator.comparing(Game::getDisplayTitle))
                    .map(g -> {
                        Map<String, Object> gm = new LinkedHashMap<>();
                        gm.put("id", g.getId());
                        gm.put("title", g.getDisplayTitle());
                        return gm;
                    })
                    .collect(Collectors.toList());
            m.put("games", games);
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping(value = "/next")
    public Map<String, Object> getNextSong(
            @RequestParam(defaultValue = "") String preferredSeries,
            @RequestParam(defaultValue = "") String preferredGames,
            @RequestParam(defaultValue = "") String preferredGenres,
            @RequestParam(defaultValue = "") String preferredAuthors,
            @RequestParam(defaultValue = "70") int preferredRatio,
            @RequestParam(defaultValue = "") String exclude,
            @RequestParam(defaultValue = "") String blockedSeries,
            @RequestParam(defaultValue = "") String blockedGames,
            @RequestParam(defaultValue = "") String blockedGenres,
            @RequestParam(defaultValue = "") String blockedAuthors,
            @RequestParam(defaultValue = "") String blockedSongs,
            @RequestParam(defaultValue = "true") boolean preferOfficialTrailerMusic) {

        List<Long> serieIds = parseIds(preferredSeries);
        List<Long> gameIds = parseIds(preferredGames);
        List<Long> genreIds = parseIds(preferredGenres);
        List<Long> authorIds = parseIds(preferredAuthors);

        var svc = baseController.getSongSubgroupService();
        List<Long> excludeIds = resolveExcludeIds(svc, exclude, blockedSeries, blockedGames, blockedGenres, blockedAuthors, blockedSongs);

        boolean hasGames = !gameIds.isEmpty();
        boolean hasSeries = !serieIds.isEmpty();
        boolean hasGenres = !genreIds.isEmpty();
        boolean hasAuthors = !authorIds.isEmpty();
        boolean hasPreferred = hasGames || hasSeries || hasGenres || hasAuthors;

        Optional<SongSubgroup> result = Optional.empty();

        if (hasPreferred && random.nextInt(100) < preferredRatio) {
            if (hasGames && hasAuthors) {
                // Try intersection first, then relax each constraint
                result = svc.findRandomFromGamesAndAuthors(excludeIds, gameIds, authorIds);
                if (result.isEmpty()) result = svc.findRandomFromGames(excludeIds, gameIds);
                if (result.isEmpty()) result = svc.findRandomFromAuthors(excludeIds, authorIds);
            } else if (hasGames && hasGenres) {
                result = svc.findRandomFromGamesAndGenres(excludeIds, gameIds, genreIds);
                if (result.isEmpty()) result = svc.findRandomFromGames(excludeIds, gameIds);
                if (result.isEmpty()) result = svc.findRandomFromGenres(excludeIds, genreIds);
            } else if (hasSeries && hasAuthors) {
                result = svc.findRandomFromSeriesAndAuthors(excludeIds, serieIds, authorIds);
                if (result.isEmpty()) result = svc.findRandomFromSeries(excludeIds, serieIds);
                if (result.isEmpty()) result = svc.findRandomFromAuthors(excludeIds, authorIds);
            } else if (hasSeries && hasGenres) {
                result = svc.findRandomFromSeriesAndGenres(excludeIds, serieIds, genreIds);
                if (result.isEmpty()) result = svc.findRandomFromSeries(excludeIds, serieIds);
                if (result.isEmpty()) result = svc.findRandomFromGenres(excludeIds, genreIds);
            } else if (hasGames) {
                result = svc.findRandomFromGames(excludeIds, gameIds);
            } else if (hasSeries) {
                result = svc.findRandomFromSeries(excludeIds, serieIds);
            } else if (hasAuthors) {
                result = svc.findRandomFromAuthors(excludeIds, authorIds);
            } else {
                result = svc.findRandomFromGenres(excludeIds, genreIds);
            }
        }

        if (result.isEmpty()) {
            result = svc.findRandom(excludeIds);
        }

        return result.map(ss -> toDto(ss, preferOfficialTrailerMusic)).orElse(null);
    }

    @GetMapping(value = "/count")
    public Map<String, Object> getPreferenceCount(
            @RequestParam(defaultValue = "") String preferredSeries,
            @RequestParam(defaultValue = "") String preferredGames,
            @RequestParam(defaultValue = "") String preferredGenres,
            @RequestParam(defaultValue = "") String preferredAuthors,
            @RequestParam(defaultValue = "") String blockedSeries,
            @RequestParam(defaultValue = "") String blockedGames,
            @RequestParam(defaultValue = "") String blockedGenres,
            @RequestParam(defaultValue = "") String blockedAuthors,
            @RequestParam(defaultValue = "") String blockedSongs) {

        List<Long> serieIds = parseIds(preferredSeries);
        List<Long> gameIds = parseIds(preferredGames);
        List<Long> genreIds = parseIds(preferredGenres);
        List<Long> authorIds = parseIds(preferredAuthors);
        var svc = baseController.getSongSubgroupService();
        List<Long> excludeIds = resolveExcludeIds(svc, "", blockedSeries, blockedGames, blockedGenres, blockedAuthors, blockedSongs);

        long count;
        if (!gameIds.isEmpty() && !authorIds.isEmpty()) {
            count = svc.countFromGamesAndAuthors(gameIds, authorIds, excludeIds);
        } else if (!gameIds.isEmpty() && !genreIds.isEmpty()) {
            count = svc.countFromGamesAndGenres(gameIds, genreIds, excludeIds);
        } else if (!serieIds.isEmpty() && !authorIds.isEmpty()) {
            count = svc.countFromSeriesAndAuthors(serieIds, authorIds, excludeIds);
        } else if (!serieIds.isEmpty() && !genreIds.isEmpty()) {
            count = svc.countFromSeriesAndGenres(serieIds, genreIds, excludeIds);
        } else if (!gameIds.isEmpty()) {
            count = svc.countFromGames(gameIds, excludeIds);
        } else if (!serieIds.isEmpty()) {
            count = svc.countFromSeries(serieIds, excludeIds);
        } else if (!authorIds.isEmpty()) {
            count = svc.countFromAuthors(authorIds, excludeIds);
        } else if (!genreIds.isEmpty()) {
            count = svc.countFromGenres(genreIds, excludeIds);
        } else {
            count = svc.countAll(excludeIds);
        }

        return Map.of("count", count);
    }

    @GetMapping(value = "/search")
    public List<Map<String, Object>> searchSongs(
            @RequestParam(defaultValue = "") String band,
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "true") boolean preferOfficialTrailerMusic) {

        if (band.isBlank() && title.isBlank()) return List.of();

        List<SongSubgroup> results = band.isBlank()
                ? baseController.getSongSubgroupService().searchByTitle(title)
                : baseController.getSongSubgroupService().searchByBandAndTitle(band, title);

        return results.stream().map(ss -> toDto(ss, preferOfficialTrailerMusic)).collect(Collectors.toList());
    }

    private Map<String, Object> toDto(SongSubgroup ss, boolean preferOfficialTrailerMusic) {
        String artist = ss.getIngameDisplayBand() != null
                ? ss.getIngameDisplayBand() : ss.getSong().getOfficialDisplayBand();
        String title = ss.getIngameDisplayTitle() != null
                ? ss.getIngameDisplayTitle() : ss.getSong().getOfficialDisplayTitle();

        boolean isTrailer = ss.getSubgroup().getSubgroupType() == SubgroupType.TRAILERS;
        String officialSrcId = ss.getSong().getSrcId();
        String srcId = (isTrailer && preferOfficialTrailerMusic && officialSrcId != null && !officialSrcId.isBlank())
                ? officialSrcId
                : (ss.getSrcId() != null ? ss.getSrcId() : officialSrcId);

        String game = ss.getSubgroup().getMainGroup().getGame().getDisplayTitle();
        String gameShort = ss.getSubgroup().getMainGroup().getGame().getGameShort();
        String mainGroup = ss.getSubgroup().getMainGroup().getGroupName();
        String subgroupName = ss.getSubgroup().getSubgroupName();
        Long authorId = ss.getSong().getAuthorSongList().stream()
                .findFirst()
                .map(as -> as.getAuthorAlias().getAuthor().getId())
                .orElse(null);
        String subgroupType = ss.getSubgroup().getSubgroupType() != null
                ? ss.getSubgroup().getSubgroupType().getDisplayLabel() : "";

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", ss.getId());
        dto.put("srcId", srcId);
        dto.put("artist", artist);
        dto.put("title", title);
        dto.put("game", game);
        dto.put("gameShort", gameShort);
        dto.put("authorId", authorId);
        dto.put("mainGroup", mainGroup);
        dto.put("subgroupName", subgroupName);
        dto.put("subgroupType", subgroupType);
        return dto;
    }

    /**
     * Merges recently-played exclusions with all blocked-preference dimensions (series/games/genres/authors are
     * resolved to their matching song-subgroup ids; blocked songs are already song-subgroup ids) into a single
     * NOT-IN id set, so blocking works uniformly across every existing find/count query without needing dedicated
     * "blocked" combo queries.
     */
    private List<Long> resolveExcludeIds(SongSubgroupService svc, String exclude, String blockedSeries,
                                          String blockedGames, String blockedGenres, String blockedAuthors, String blockedSongs) {
        Set<Long> excludeIds = new LinkedHashSet<>(parseIds(exclude));
        excludeIds.addAll(parseIds(blockedSongs));

        List<Long> blockedSerieIds = parseIds(blockedSeries);
        if (!blockedSerieIds.isEmpty()) excludeIds.addAll(svc.findIdsBySeries(blockedSerieIds));

        List<Long> blockedGameIds = parseIds(blockedGames);
        if (!blockedGameIds.isEmpty()) excludeIds.addAll(svc.findIdsByGames(blockedGameIds));

        List<Long> blockedGenreIds = parseIds(blockedGenres);
        if (!blockedGenreIds.isEmpty()) excludeIds.addAll(svc.findIdsByGenres(blockedGenreIds));

        List<Long> blockedAuthorIds = parseIds(blockedAuthors);
        if (!blockedAuthorIds.isEmpty()) excludeIds.addAll(svc.findIdsByAuthors(blockedAuthorIds));

        // Sentinel so NOT IN clause never sees empty list
        if (excludeIds.isEmpty()) excludeIds.add(-1L);

        return new ArrayList<>(excludeIds);
    }

    private List<Long> parseIds(String param) {
        if (param == null || param.isBlank()) return new ArrayList<>();
        return Arrays.stream(param.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
