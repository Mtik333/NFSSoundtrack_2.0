package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.GamePlaylist;
import com.nfssoundtrack.racingsoundtracks.repository.GamePlaylistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GamePlaylistService {

    private final GamePlaylistRepository gamePlaylistRepository;

    public GamePlaylistService(GamePlaylistRepository gamePlaylistRepository) {
        this.gamePlaylistRepository = gamePlaylistRepository;
    }

    public List<GamePlaylist> findByGame(Game game) {
        return gamePlaylistRepository.findByGame(game);
    }

    public java.util.Optional<GamePlaylist> findById(Long id) {
        return gamePlaylistRepository.findById(id);
    }

    public GamePlaylist save(GamePlaylist gamePlaylist) {
        return gamePlaylistRepository.save(gamePlaylist);
    }

    public void deleteById(Long id) {
        gamePlaylistRepository.deleteById(id);
    }
}
