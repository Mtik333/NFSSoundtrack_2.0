package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNullApi;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {

    @EntityGraph(value = "Game.mainGroups")
    Game findByGameShort(String gameShort);

    @Override
    @EntityGraph(value = "Game.mainGroups")
    Optional<Game> findById(Integer integer);

    @EntityGraph(value = "Game.mainGroups")
    List<Game> findAll();

    @EntityGraph(value = "Game.mainGroups")
    List<Game> findByIdNotNull();
}
