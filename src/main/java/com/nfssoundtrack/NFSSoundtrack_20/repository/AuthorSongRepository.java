package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Role;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorSongRepository extends JpaRepository<AuthorSong, Integer> {

    List<AuthorSong> findByAuthorAlias(AuthorAlias authorAlias);

    List<AuthorSong> findBySong(Song song);

    List<AuthorSong> findByAuthorAliasAndRole(AuthorAlias authorAlias, Role role);

    Optional<AuthorSong> findByAuthorAliasAndSong(AuthorAlias authorAlias, Song song);
}
