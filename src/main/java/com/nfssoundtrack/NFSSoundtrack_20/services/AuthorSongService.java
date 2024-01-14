package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorSongService {

    @Autowired
    AuthorSongRepository authorSongRepository;

    public List<AuthorSong> findByAuthorAlias(AuthorAlias authorAlias) {
        return authorSongRepository.findByAuthorAlias(authorAlias);
    }

    public List<AuthorSong> findBySong(Song song) {
        return authorSongRepository.findBySong(song);
    }

    public Optional<AuthorSong> findById(int id) {
        return authorSongRepository.findById(id);
    }

    public Optional<AuthorSong> findByAuthorAliasAndSong(AuthorAlias authorAlias, Song song) {
        return authorSongRepository.findByAuthorAliasAndSong(authorAlias, song);
    }

    public AuthorSong save(AuthorSong authorSong) {
        return authorSongRepository.save(authorSong);
    }

    public void delete(AuthorSong authorSong) {
        authorSongRepository.delete(authorSong);
    }

    public List<AuthorSong> saveAll(List<AuthorSong> authorSongs) {
        return authorSongRepository.saveAll(authorSongs);
    }

    public void deleteAll(List<AuthorSong> authorSongs) {
        authorSongRepository.deleteAll(authorSongs);
    }
}
