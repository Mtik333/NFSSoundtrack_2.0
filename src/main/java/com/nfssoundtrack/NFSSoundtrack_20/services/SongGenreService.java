package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongGenre;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongGenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongGenreService {

	@Autowired
	SongGenreRepository songGenreRepository;

	public List<SongGenre> findByGenre(Genre genre){
		return songGenreRepository.findByGenre(genre);
	}

	public List<SongGenre> findByGenre(Genre genre, int page){
		return songGenreRepository.findByGenre(genre, Pageable.ofSize(page));
	}
}
