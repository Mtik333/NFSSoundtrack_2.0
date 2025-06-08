package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.MainGroup;
import com.nfssoundtrack.racingsoundtracks.repository.MainGroupRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MainGroupService {

    private final MainGroupRepository mainGroupRepository;

    public MainGroupService(MainGroupRepository mainGroupRepository) {
        this.mainGroupRepository = mainGroupRepository;
    }

    public MainGroup save(MainGroup mainGroup) {
        return mainGroupRepository.save(mainGroup);
    }

    public MainGroup saveUpdate(MainGroup mainGroup) {
        return mainGroupRepository.save(mainGroup);
    }

    public Optional<MainGroup> findById(int id) {
        return mainGroupRepository.findById(id);
    }

    public void delete(MainGroup mainGroup) {
        mainGroupRepository.delete(mainGroup);
    }
}
