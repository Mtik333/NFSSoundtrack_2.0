package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.MainGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MainGroupService {

	@Autowired
	MainGroupRepository mainGroupRepository;

	public MainGroup save(MainGroup mainGroup) {
		return mainGroupRepository.save(mainGroup);
	}

	public Optional<MainGroup> findById(int id) {
		return mainGroupRepository.findById(id);
	}

	public void delete(MainGroup mainGroup) {
		mainGroupRepository.delete(mainGroup);
	}
}
