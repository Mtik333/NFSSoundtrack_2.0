package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Country;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.CustomTheme;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.repository.CustomThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomThemeService {

    @Autowired
    CustomThemeRepository customThemeRepository;

    public Optional<CustomTheme> findByGame(Game game) {
        return customThemeRepository.findByGame(game);
    }

}
