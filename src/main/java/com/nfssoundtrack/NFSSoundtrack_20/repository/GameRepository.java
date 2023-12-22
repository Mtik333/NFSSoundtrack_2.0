package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Content;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game,Integer> {

    @Cacheable("games")
    List<Game> findAll(Sort sort);
    Game findByGameShort(String gameShort);
}
