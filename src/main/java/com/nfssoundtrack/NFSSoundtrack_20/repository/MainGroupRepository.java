package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainGroupRepository extends JpaRepository<MainGroup,Integer> {

//    List<MainGroup> findByGameId(Long game);

    List<MainGroup> findAll();

    @EntityGraph(value="MainGroup.subgroups")
    List<MainGroup> findByIdNotNull();
}
