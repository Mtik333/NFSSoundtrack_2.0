package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {

	@Autowired
	SongRepository songRepository;

	@Autowired
	GenreService genreService;

	@Autowired
	SongGenreService songGenreService;

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

	public void saveNewAssignmentOfExistingGenre(String genreValue, Song song) throws Exception {
		Genre genre = genreService.findById(Integer.parseInt(genreValue)).orElseThrow(() -> new Exception("No genre " +
				"found with id " + genreValue));
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
	}
}
