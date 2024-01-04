package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Author findByName(String name);

    List<Author> findByNameContains(String name);

    List<Author> findAll();

    @EntityGraph(value = "Author.authorCountries")
    List<Author> findByIdNotNull();
}
