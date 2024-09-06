package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorAlias;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorSong;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorSongRepository extends JpaRepository<AuthorSong, Integer> {

    List<AuthorSong> findByAuthorAlias(AuthorAlias authorAlias);

    List<AuthorSong> findBySong(Song song);

    Optional<AuthorSong> findByAuthorAliasAndSong(AuthorAlias authorAlias, Song song);

    List<AuthorSong> findMultipleByAuthorAliasAndSong(AuthorAlias authorAlias, Song song);
}
