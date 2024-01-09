package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongGenreRepository extends JpaRepository<SongGenre, Integer> {

    List<SongGenre> findBySong(Song id);

    List<SongGenre> findByGenre(Genre genre);

    List<SongGenre> findByGenre(Genre genre, Pageable pageable);
}
