package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorAlias;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorSong;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.repository.AuthorSongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorSongService {

    private final AuthorSongRepository authorSongRepository;

    public AuthorSongService(AuthorSongRepository authorSongRepository) {
        this.authorSongRepository = authorSongRepository;
    }

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

    public List<AuthorSong> findMultipleByAuthorAliasAndSong(AuthorAlias authorAlias, Song song) {
        return authorSongRepository.findMultipleByAuthorAliasAndSong(authorAlias, song);
    }

    public AuthorSong save(AuthorSong authorSong) {
        return authorSongRepository.save(authorSong);
    }

    public void delete(AuthorSong authorSong) {
        authorSongRepository.delete(authorSong);
    }

    public void saveAll(List<AuthorSong> authorSongs) {
        authorSongRepository.saveAll(authorSongs);
    }

    public void deleteAll(List<AuthorSong> authorSongs) {
        authorSongRepository.deleteAll(authorSongs);
    }
}
