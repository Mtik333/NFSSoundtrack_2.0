package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Country;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    @Cacheable("findByCountryName")
    Optional<Country> findByCountryName(String countryName);

    @Cacheable("findByCountryNameContains")
    List<Country> findByCountryNameContains(String countryName);
}
