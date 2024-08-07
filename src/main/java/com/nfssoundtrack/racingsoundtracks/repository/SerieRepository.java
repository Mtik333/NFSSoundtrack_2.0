package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SerieRepository extends JpaRepository<Serie, Integer> {

    @EntityGraph(value = "Serie.games")
    List<Serie> findByIdNotNull();

    @Cacheable("series")
    @EntityGraph(attributePaths = {"games"})
    List<Serie> findAll(Sort sort);

}
