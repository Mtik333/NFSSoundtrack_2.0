package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Subgroup;
import com.nfssoundtrack.racingsoundtracks.repository.SubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubgroupService {

    @Autowired
    SubgroupRepository subgroupRepository;

    public Optional<Subgroup> findById(int id) {
        return subgroupRepository.findById(id);
    }

    public Subgroup save(Subgroup subgroup) {
        return subgroupRepository.save(subgroup);
    }

    public void deleteAllInBatch(List<Subgroup> songSubgroupList) {
        subgroupRepository.deleteAllInBatch(songSubgroupList);
    }

}
