package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

	@Autowired
	AuthorRepository authorRepository;

	public Optional<Author> findById(int authorId){
		return authorRepository.findById(authorId);
	}

	public Optional<Author> findByName(String name){
		return authorRepository.findByName(name);
	}

	public List<Author> findByNameContains(String name){
		return authorRepository.findByNameContains(name);
	}

	public void delete(Author author){
		 authorRepository.delete(author);
	}

	public Author save(Author author){
		return authorRepository.save(author);
	}
}
