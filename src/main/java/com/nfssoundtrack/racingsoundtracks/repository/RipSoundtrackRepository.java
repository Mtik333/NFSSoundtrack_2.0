package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.GamePlatform;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Genre;
import com.nfssoundtrack.racingsoundtracks.dbmodel.RipSoundtrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RipSoundtrackRepository extends JpaRepository<RipSoundtrack, Integer> {

    List<RipSoundtrack> findByGamePlatform(GamePlatform gamePlatform);

}
