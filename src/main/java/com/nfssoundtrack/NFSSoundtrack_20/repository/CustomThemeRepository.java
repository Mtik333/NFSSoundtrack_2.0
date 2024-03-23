package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Country;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.CustomTheme;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomThemeRepository extends JpaRepository<CustomTheme, Integer> {

    Optional<CustomTheme> findByGame(Game game);

}
