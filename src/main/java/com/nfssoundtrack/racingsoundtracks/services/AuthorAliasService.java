package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorAlias;
import com.nfssoundtrack.racingsoundtracks.repository.AuthorAliasRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorAliasService {

    private final AuthorAliasRepository authorAliasRepository;

    public AuthorAliasService(AuthorAliasRepository authorAliasRepository) {
        this.authorAliasRepository = authorAliasRepository;
    }

    public Optional<AuthorAlias> findById(int id) {
        return authorAliasRepository.findById(id);
    }

    public List<AuthorAlias> findByAuthor(Author author) {
        return authorAliasRepository.findByAuthor(author);
    }

    public Optional<AuthorAlias> findByAlias(String alias) {
        return authorAliasRepository.findByAlias(alias);
    }

    public List<AuthorAlias> findByAliasContains(String alias) {
        return authorAliasRepository.findByAliasContains(alias);
    }

    public AuthorAlias save(AuthorAlias authorAlias) {
        return authorAliasRepository.save(authorAlias);
    }

    public AuthorAlias saveUpdate(AuthorAlias authorAlias) {
        return authorAliasRepository.save(authorAlias);
    }

    public void delete(AuthorAlias authorAlias) {
        authorAliasRepository.delete(authorAlias);
    }

    public void deleteAll(List<AuthorAlias> authorAliases) {
        authorAliasRepository.deleteAll(authorAliases);
    }
}
