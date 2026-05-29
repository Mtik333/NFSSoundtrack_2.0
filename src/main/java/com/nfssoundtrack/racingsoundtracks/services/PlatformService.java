package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Platform;
import com.nfssoundtrack.racingsoundtracks.repository.PlatformRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public Optional<Platform> findByPlatform(String platform) {
        return platformRepository.findByPlatform(platform);
    }

    public List<Platform> findAll() {
        return platformRepository.findAll();
    }
}
