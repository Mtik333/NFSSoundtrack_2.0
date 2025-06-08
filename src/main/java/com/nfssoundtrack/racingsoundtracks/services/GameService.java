package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.GameStatus;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.repository.GameRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;

    private final SerieService serieService;

    public GameService(GameRepository gameRepository, SerieService serieService) {
        this.gameRepository = gameRepository;
        this.serieService = serieService;
    }

    private static final int RECENT_DAYS_THRESHOLD = 7;

    public Game findByGameShort(String gameShort) {
        return gameRepository.findByGameShort(gameShort);
    }

    public List<Game> findAllSortedByDisplayTitleAsc() {
        return gameRepository.findAll(Sort.by(Sort.Direction.ASC, "displayTitle"));
    }

    public Optional<Game> findById(int id) {
        return gameRepository.findById(id);
    }

    public Game save(Game game) {
        return gameRepository.save(game);
    }

    // Enhanced save method that updates Serie too
    public Game saveWithSerieUpdate(Game game) {
        // Mark game as recently updated
        game.setLastUpdated(LocalDateTime.now());
        game.setIsRecentlyUpdated(true);
        // Save game first
        Game savedGame = gameRepository.save(game);
        // Update the parent Serie
        if (savedGame.getSerie() != null) {
            updateSerieRecentFlags(savedGame.getSerie());
        }
        return savedGame;
    }

    // Update Serie based on its games
    private void updateSerieRecentFlags(Serie serie) {
        serie.setLastUpdated(LocalDateTime.now());
        serie.setHasRecentUpdates(true);
        serieService.save(serie);
    }

    // Batch method to refresh all "recent" flags
    public void refreshRecentFlags() {
        LocalDateTime threshold =
                LocalDateTime.now().minusDays(RECENT_DAYS_THRESHOLD);

        // Update games
        List<Game> allGames = gameRepository.findAll();
        for (Game game : allGames) {
            boolean isRecent = game.getLastUpdated() != null &&
                    game.getLastUpdated().isAfter(threshold);
            game.setIsRecentlyUpdated(isRecent);
        }
        gameRepository.saveAll(allGames);

        // Update series
        List<Serie> allSeries = serieService.findAll();
        for (Serie serie : allSeries) {
            boolean hasRecentGames = serie.getGames().stream()
                    .anyMatch(Game::getIsRecentlyUpdated);
            serie.setHasRecentUpdates(hasRecentGames);
            
            boolean hasUpcomingGames = serie.getGames().stream()
                    .anyMatch(game -> game.getGameStatus() == GameStatus.UNRELEASED);
            serie.setHasUpcomingGames(hasUpcomingGames);
        }
        serieService.saveAll(allSeries);
    }

}
