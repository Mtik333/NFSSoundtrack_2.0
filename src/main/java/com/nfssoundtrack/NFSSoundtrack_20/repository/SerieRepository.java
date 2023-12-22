package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SerieRepository extends JpaRepository<Serie,Integer> {
    @Cacheable("series")
    List<Serie> findAll(Sort sort);

    Serie findById(Long id);

    List<Serie> findByName(String name);
}
