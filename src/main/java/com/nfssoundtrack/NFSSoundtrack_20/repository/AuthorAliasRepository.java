package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorAliasRepository extends JpaRepository<AuthorAlias,Integer> {

    List<AuthorAlias> findByAuthor(Author author);
    AuthorAlias findByAlias(String alias);
}
