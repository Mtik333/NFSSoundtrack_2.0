package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.CustomTheme;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.repository.CustomThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomThemeService {

    @Autowired
    CustomThemeRepository customThemeRepository;

    public Optional<CustomTheme> findById(Integer id){
        return customThemeRepository.findById(id);
    }

    public Optional<CustomTheme> findByGame(Game game) {
        return customThemeRepository.findByGame(game);
    }

}
