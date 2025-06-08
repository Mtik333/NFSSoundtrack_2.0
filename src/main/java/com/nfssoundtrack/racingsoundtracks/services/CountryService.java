package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Country;
import com.nfssoundtrack.racingsoundtracks.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Optional<Country> findById(int id) {
        return countryRepository.findById(id);
    }

    public Country save(Country country) {
        return countryRepository.save(country);
    }

    public Country saveUpdate(Country country) {
        return countryRepository.save(country);
    }

    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    public Optional<Country> findByCountryName(String countryName) {
        return countryRepository.findByCountryName(countryName);
    }

    public List<Country> findByCountryNameContains(String countryName) {
        return countryRepository.findByCountryNameContains(countryName);
    }
}
