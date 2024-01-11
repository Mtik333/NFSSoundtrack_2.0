package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorAliasRepository extends JpaRepository<AuthorAlias, Integer> {

    List<AuthorAlias> findByAuthor(Author author);

    Optional<AuthorAlias> findByAlias(String alias);

    List<AuthorAlias> findByAliasContains(String alias);
}
