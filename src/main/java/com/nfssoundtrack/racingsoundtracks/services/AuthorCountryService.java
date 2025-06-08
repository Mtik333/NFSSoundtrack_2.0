package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorCountry;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Country;
import com.nfssoundtrack.racingsoundtracks.repository.AuthorCountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorCountryService {

    private final AuthorCountryRepository authorCountryRepository;

    public AuthorCountryService(AuthorCountryRepository authorCountryRepository) {
        this.authorCountryRepository = authorCountryRepository;
    }


    public void deleteAll(List<AuthorCountry> authorCountryList) {
        authorCountryRepository.deleteAll(authorCountryList);
    }

    public void saveAll(List<AuthorCountry> authorCountryList) {
        authorCountryRepository.saveAll(authorCountryList);
    }

    public List<AuthorCountry> findByCountry(Country country) {
        return authorCountryRepository.findByCountry(country);
    }

}
