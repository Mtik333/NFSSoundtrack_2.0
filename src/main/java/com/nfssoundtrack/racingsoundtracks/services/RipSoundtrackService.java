package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.repository.GamePlatformRepository;
import com.nfssoundtrack.racingsoundtracks.repository.GenreRepository;
import com.nfssoundtrack.racingsoundtracks.repository.RipSoundtrackRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RipSoundtrackService {

    private final RipSoundtrackRepository ripSoundtrackRepository;
    private final GamePlatformRepository gamePlatformRepository;

    public RipSoundtrackService(RipSoundtrackRepository ripSoundtrackRepository, GamePlatformRepository gamePlatformRepository) {
        this.ripSoundtrackRepository = ripSoundtrackRepository;
        this.gamePlatformRepository = gamePlatformRepository;
    }

    public List<RipSoundtrack> findByGamePlatform(GamePlatform gamePlatform) {
        return ripSoundtrackRepository.findByGamePlatform(gamePlatform);
    }

    public List<RipSoundtrack> findByGame(Game game) {
        List<GamePlatform> gamePlatforms = gamePlatformRepository.findByGame(game);
        List<RipSoundtrack> ripSoundtracks = new ArrayList<>();
        for (GamePlatform gamePlatform : gamePlatforms){
            ripSoundtracks.addAll(ripSoundtrackRepository.findByGamePlatform(gamePlatform));
        }
        return ripSoundtracks;
    }

    public java.util.Optional<RipSoundtrack> findById(Long id) {
        return ripSoundtrackRepository.findById(id.intValue());
    }

    public RipSoundtrack save(RipSoundtrack ripSoundtrack) {
        return ripSoundtrackRepository.save(ripSoundtrack);
    }

    public void deleteById(Long id) {
        ripSoundtrackRepository.deleteById(id.intValue());
    }
}
