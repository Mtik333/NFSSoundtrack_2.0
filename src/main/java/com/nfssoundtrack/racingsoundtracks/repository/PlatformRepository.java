package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Integer> {

    Optional<Platform> findByPlatform(String platform);

}
