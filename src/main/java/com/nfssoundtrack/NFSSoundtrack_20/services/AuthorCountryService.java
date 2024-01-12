package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorCountry;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorCountryRepository;
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
