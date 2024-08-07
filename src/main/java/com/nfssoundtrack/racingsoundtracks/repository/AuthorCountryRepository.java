package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorCountry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorCountryRepository extends JpaRepository<AuthorCountry, Integer> {
}
