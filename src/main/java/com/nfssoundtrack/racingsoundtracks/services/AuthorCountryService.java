package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorCountry;
import com.nfssoundtrack.racingsoundtracks.repository.AuthorCountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthorCountryService {

    @Autowired
    AuthorCountryRepository authorCountryRepository;

    public void deleteAll(List<AuthorCountry> authorCountryList) {
        authorCountryRepository.deleteAll(authorCountryList);
    }

    public void deleteAll(Set<AuthorCountry> authorCountryList) {
        authorCountryRepository.deleteAll(authorCountryList);
    }

    public List<AuthorCountry> saveAll(List<AuthorCountry> authorCountryList) {
        return authorCountryRepository.saveAll(authorCountryList);
    }

}
