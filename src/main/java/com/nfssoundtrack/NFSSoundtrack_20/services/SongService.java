package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongService {

	@Autowired
	SongRepository songRepository;

	public Optional<Song> findById(Integer id){
		return songRepository.findById(id);
	}

	public List<Song> findByOfficialDisplayTitle(String officialDisplayTitle){
		return songRepository.findByOfficialDisplayTitle(officialDisplayTitle);
	}

	public List<Song> findByOfficialDisplayTitleContains(String officialDisplayTitle){
		return songRepository.findByOfficialDisplayTitleContains(officialDisplayTitle);
	}

	public List<Song> findByOfficialDisplayBandAndOfficialDisplayTitle(String officialDisplayBand, String officialDisplayTitle){
		return songRepository.findByOfficialDisplayBandAndOfficialDisplayTitle(officialDisplayBand,officialDisplayTitle);
	}

	public List<Song> findByLyrics(String lyrics){
		return songRepository.findByLyrics(lyrics);
	}

	public List<Song> findByLyricsContains(String lyrics){
		return songRepository.findByLyricsContains(lyrics);
	}
}
