package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Optional<Author> findByName(String name);

    List<Author> findAllByName(String name);
    List<Author> findByNameContains(String name);

    @EntityGraph(value = "Author.authorCountries")
    List<Author> findByIdNotNull();
}
