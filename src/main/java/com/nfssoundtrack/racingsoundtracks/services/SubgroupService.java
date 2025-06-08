package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Subgroup;
import com.nfssoundtrack.racingsoundtracks.repository.SubgroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubgroupService {

    private final SubgroupRepository subgroupRepository;

    public SubgroupService(SubgroupRepository subgroupRepository) {
        this.subgroupRepository = subgroupRepository;
    }

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
