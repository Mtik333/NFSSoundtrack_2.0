package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

    Optional<Author> findByName(String name);

    List<Author> findAllByName(String name);

    List<Author> findByNameContains(String name);

    List<Author> findByNameStartingWith(String name);
}
