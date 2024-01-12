package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubgroupRepository extends JpaRepository<Subgroup, Integer> {

	List<Subgroup> findAll();

	@EntityGraph(value = "Subgroup.songSubgroupList")
	List<Subgroup> findByIdNotNull();
}
