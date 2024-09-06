package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorCountry;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorCountryRepository extends JpaRepository<AuthorCountry, Integer> {

    List<AuthorCountry> findByCountry(Country country);

}
