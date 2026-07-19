package com.nfssoundtrack.racingsoundtracks.controllers;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.radioserializers.RadioSerieSerializer;
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
            @RequestParam(defaultValue = "70") int preferredRatio,
            @RequestParam(defaultValue = "") String exclude) {

        List<Long> excludeIds = parseIds(exclude);
        List<Long> serieIds = parseIds(preferredSeries);
        List<Long> gameIds = parseIds(preferredGames);
        List<Long> genreIds = parseIds(preferredGenres);

        // Sentinel so NOT IN clause never sees empty list
        if (excludeIds.isEmpty()) excludeIds = List.of(-1L);

        boolean hasGames = !gameIds.isEmpty();
        boolean hasSeries = !serieIds.isEmpty();
        boolean hasGenres = !genreIds.isEmpty();
        boolean hasPreferred = hasGames || hasSeries || hasGenres;

        Optional<SongSubgroup> result = Optional.empty();
        var svc = baseController.getSongSubgroupService();

        if (hasPreferred && random.nextInt(100) < preferredRatio) {
            if (hasGames && hasGenres) {
                // Try intersection first, then relax each constraint
                result = svc.findRandomFromGamesAndGenres(excludeIds, gameIds, genreIds);
                if (result.isEmpty()) result = svc.findRandomFromGames(excludeIds, gameIds);
                if (result.isEmpty()) result = svc.findRandomFromGenres(excludeIds, genreIds);
            } else if (hasSeries && hasGenres) {
                result = svc.findRandomFromSeriesAndGenres(excludeIds, serieIds, genreIds);
                if (result.isEmpty()) result = svc.findRandomFromSeries(excludeIds, serieIds);
                if (result.isEmpty()) result = svc.findRandomFromGenres(excludeIds, genreIds);
            } else if (hasGames) {
                result = svc.findRandomFromGames(excludeIds, gameIds);
            } else if (hasSeries) {
                result = svc.findRandomFromSeries(excludeIds, serieIds);
            } else {
                result = svc.findRandomFromGenres(excludeIds, genreIds);
            }
        }

        if (result.isEmpty()) {
            result = svc.findRandom(excludeIds);
        }

        return result.map(this::toDto).orElse(null);
    }

    @GetMapping(value = "/count")
    public Map<String, Object> getPreferenceCount(
            @RequestParam(defaultValue = "") String preferredSeries,
            @RequestParam(defaultValue = "") String preferredGames,
            @RequestParam(defaultValue = "") String preferredGenres) {

        List<Long> serieIds = parseIds(preferredSeries);
        List<Long> gameIds = parseIds(preferredGames);
        List<Long> genreIds = parseIds(preferredGenres);
        var svc = baseController.getSongSubgroupService();

        long count;
        if (!gameIds.isEmpty() && !genreIds.isEmpty()) {
            count = svc.countFromGamesAndGenres(gameIds, genreIds);
        } else if (!serieIds.isEmpty() && !genreIds.isEmpty()) {
            count = svc.countFromSeriesAndGenres(serieIds, genreIds);
        } else if (!gameIds.isEmpty()) {
            count = svc.countFromGames(gameIds);
        } else if (!serieIds.isEmpty()) {
            count = svc.countFromSeries(serieIds);
        } else if (!genreIds.isEmpty()) {
            count = svc.countFromGenres(genreIds);
        } else {
            count = svc.countAll();
        }

        return Map.of("count", count);
    }

    @GetMapping(value = "/search")
    public List<Map<String, Object>> searchSongs(
            @RequestParam(defaultValue = "") String band,
            @RequestParam(defaultValue = "") String title) {

        if (band.isBlank() && title.isBlank()) return List.of();

        List<SongSubgroup> results = band.isBlank()
                ? baseController.getSongSubgroupService().searchByTitle(title)
                : baseController.getSongSubgroupService().searchByBandAndTitle(band, title);

        return results.stream().map(this::toDto).collect(Collectors.toList());
    }

    private Map<String, Object> toDto(SongSubgroup ss) {
        String artist = ss.getIngameDisplayBand() != null
                ? ss.getIngameDisplayBand() : ss.getSong().getOfficialDisplayBand();
        String title = ss.getIngameDisplayTitle() != null
                ? ss.getIngameDisplayTitle() : ss.getSong().getOfficialDisplayTitle();
        String srcId = ss.getSrcId() != null ? ss.getSrcId() : ss.getSong().getSrcId();
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

    private List<Long> parseIds(String param) {
        if (param == null || param.isBlank()) return new ArrayList<>();
        return Arrays.stream(param.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
