package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Content;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game,Integer> {

    Game findByGameShort(String gameShort);
}
