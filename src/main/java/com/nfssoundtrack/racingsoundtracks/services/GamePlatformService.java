package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.GamePlatform;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Platform;
import com.nfssoundtrack.racingsoundtracks.repository.GamePlatformRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GamePlatformService {

    private final GamePlatformRepository gamePlatformRepository;

    public GamePlatformService(GamePlatformRepository gamePlatformRepository) {
        this.gamePlatformRepository = gamePlatformRepository;
    }

    public List<GamePlatform> findByPlatform(Platform platform) {
        return gamePlatformRepository.findByPlatform(platform);
    }

    public List<GamePlatform> findByGame(Game game) {
        return gamePlatformRepository.findByGame(game);
    }

    public java.util.Optional<GamePlatform> findById(int id) {
        return gamePlatformRepository.findById(id);
    }

    public GamePlatform save(GamePlatform gamePlatform) {
        return gamePlatformRepository.save(gamePlatform);
    }

    public void deleteById(int id) {
        gamePlatformRepository.deleteById(id);
    }

}
