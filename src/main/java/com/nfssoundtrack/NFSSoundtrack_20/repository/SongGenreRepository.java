package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongGenreRepository extends JpaRepository<SongGenre,Integer> {

    SongGenre findBySong(Song id);
}
