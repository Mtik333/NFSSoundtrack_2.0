package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Country;
import com.nfssoundtrack.NFSSoundtrack_20.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

	@Autowired
	CountryRepository countryRepository;

	public Optional<Country> findById(int id) {
		return countryRepository.findById(id);
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
