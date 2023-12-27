package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorSongRepository extends JpaRepository<AuthorSong,Integer> {

    AuthorSong findByAuthorAliasAndSong(AuthorAlias authorAlias, Song song);
}
