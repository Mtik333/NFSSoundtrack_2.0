package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Genre;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongGenre;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {

    private final SongRepository songRepository;

    private final GenreService genreService;

    private final SongGenreService songGenreService;

    public SongService(SongRepository songRepository, GenreService genreService, SongGenreService songGenreService) {
        this.songRepository = songRepository;
        this.genreService = genreService;
        this.songGenreService = songGenreService;
    }

    public Optional<Song> findById(Integer id) {
        return songRepository.findById(id);
    }

    public List<Song> findByOfficialDisplayTitle(String officialDisplayTitle) {
        return songRepository.findByOfficialDisplayTitle(officialDisplayTitle);
    }

    public List<Song> findByOfficialDisplayTitleContains(String officialDisplayTitle) {
        return songRepository.findByOfficialDisplayTitleContains(officialDisplayTitle);
    }

    public List<Song> findByOfficialDisplayBandAndOfficialDisplayTitle(String officialDisplayBand, String officialDisplayTitle) {
        return songRepository.findByOfficialDisplayBandAndOfficialDisplayTitle(officialDisplayBand,
                officialDisplayTitle);
    }

    public List<Song> findByOfficialDisplayBandAndOfficialDisplayTitleContains(String officialDisplayBand,
                                                                               String officialDisplayTitle) {
        return songRepository.findByOfficialDisplayBandAndOfficialDisplayTitleContains(officialDisplayBand,
                officialDisplayTitle);
    }

    public List<Song> findByLyrics(String lyrics) {
        return songRepository.findByLyrics(lyrics);
    }

    public List<Song> findByLyricsContains(String lyrics) {
        return songRepository.findByLyricsContains(lyrics);
    }

    public void delete(Song song) {
        songRepository.delete(song);
    }

    public Song save(Song song) {
        return songRepository.save(song);
    }

    /**
     * i think i struggled a bit when trying to change genres
     * so this method double checks if genre not only exists in db already, but also
     * whether it is already assigned to the song
     *
     * @param genreValue name of genre
     * @param song       song to edit
     * @throws ResourceNotFoundException
     */
    public boolean saveNewAssignmentOfExistingGenre(String genreValue, Song song, Genre genre) throws ResourceNotFoundException {
        if (genre==null){
            genre = genreService.findById(Integer.parseInt(genreValue)).orElseThrow(() -> new ResourceNotFoundException("No genre " +
                    "found with id " + genreValue));
        }
        boolean alreadyAssigned = false;
        for (SongGenre songGenre : song.getSongGenreList()) {
            if (songGenre.getGenre().equals(genre)) {
                alreadyAssigned = true;
                break;
            }
        }
        if (!alreadyAssigned) {
            SongGenre songGenre = new SongGenre(song, genre);
            songGenreService.save(songGenre);
        }
        return alreadyAssigned;
    }
}
