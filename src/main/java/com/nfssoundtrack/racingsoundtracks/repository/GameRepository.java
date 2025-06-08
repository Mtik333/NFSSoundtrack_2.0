package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {

    @Cacheable("findByGameShort")
    @EntityGraph(value = "Game.mainGroups")
    Game findByGameShort(String gameShort);

    @Override
    @EntityGraph(value = "Game.mainGroups")
    Optional<Game> findById(Integer integer);

    @EntityGraph(value = "Game.mainGroups")
    List<Game> findAll();

    @Cacheable("gamesAlpha")
    List<Game> findAll(Sort sort);

}
