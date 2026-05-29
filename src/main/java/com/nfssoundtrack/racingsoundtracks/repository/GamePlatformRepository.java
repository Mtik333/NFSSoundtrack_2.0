package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.GamePlatform;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamePlatformRepository extends JpaRepository<GamePlatform, Integer> {

    List<GamePlatform> findByPlatform(Platform platform);

    List<GamePlatform> findByGame(Game game);

}
