package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.GamePlaylist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamePlaylistRepository extends JpaRepository<GamePlaylist, Long> {
    List<GamePlaylist> findByGame(Game game);
}
