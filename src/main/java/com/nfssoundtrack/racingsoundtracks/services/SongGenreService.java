package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Genre;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongGenre;
import com.nfssoundtrack.racingsoundtracks.repository.SongGenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongGenreService {

    @Autowired
    SongGenreRepository songGenreRepository;

    public List<SongGenre> findByGenre(Genre genre) {
        return songGenreRepository.findByGenre(genre);
    }

    public List<SongGenre> findByGenre(Genre genre, int page) {
        return songGenreRepository.findByGenre(genre, Pageable.ofSize(page));
    }

    public List<SongGenre> findBySong(Song song) {
        return songGenreRepository.findBySong(song);
    }

    public SongGenre findByGenreAndSong(Genre genre, Song song) {
        return songGenreRepository.findByGenreAndSong(genre, song);
    }

    public SongGenre save(SongGenre songGenre) {
        return songGenreRepository.save(songGenre);
    }

    public void delete(SongGenre songGenre) {
        songGenreRepository.delete(songGenre);
    }

    public void saveAll(List<SongGenre> songGenres) {
        songGenreRepository.saveAll(songGenres);
    }

    public void deleteAll(List<SongGenre> songGenres) {
        songGenreRepository.deleteAll(songGenres);
    }
}
