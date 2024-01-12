package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SerieRepository extends JpaRepository<Serie, Integer> {

	List<Serie> findAll();

	@EntityGraph(value = "Serie.games")
	List<Serie> findByIdNotNull();

	@Cacheable("series")
	@EntityGraph(attributePaths = {"games"})
	List<Serie> findAll(Sort sort);
//
//    @EntityGraph(attributePaths = {"games"})
//    List<Serie> findAll();
//    Serie findById(Long id);
//
//    List<Serie> findByName(String name);
}
