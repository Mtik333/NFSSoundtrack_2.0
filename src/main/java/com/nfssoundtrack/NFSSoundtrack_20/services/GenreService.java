package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;
import com.nfssoundtrack.NFSSoundtrack_20.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

	@Autowired
	GenreRepository genreRepository;

	public Optional<Genre> findById(int id) {
		return genreRepository.findById(id);
	}

	public Optional<Genre> findByGenreName(String genreName) {
		return genreRepository.findByGenreName(genreName);
	}

	public List<Genre> findByGenreNameContains(String genreName) {
		return genreRepository.findByGenreNameContains(genreName);
	}

	public void deleteAll(List<Genre> genres) {
		genreRepository.deleteAll();
	}

	public Genre save(Genre genre) {
		return genreRepository.save(genre);
	}
}
