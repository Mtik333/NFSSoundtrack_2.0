package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.CustomTheme;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomThemeRepository extends JpaRepository<CustomTheme, Integer> {

    @Cacheable("CustomThemefindByGame")
    Optional<CustomTheme> findByGame(Game game);

}
