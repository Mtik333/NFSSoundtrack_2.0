package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorAliasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorAliasService {

	@Autowired
	AuthorAliasRepository authorAliasRepository;

	public List<AuthorAlias> findByAuthor(Author author){
		return authorAliasRepository.findByAuthor(author);
	}

	public Optional<AuthorAlias> findByAlias(String alias){
		return authorAliasRepository.findByAlias(alias);
	}

	public List<AuthorAlias> findByAliasContains(String alias){
		return authorAliasRepository.findByAliasContains(alias);
	}
}
