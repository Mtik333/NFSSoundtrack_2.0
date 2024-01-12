package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubgroupService {

    @Autowired
    SubgroupRepository subgroupRepository;

    public Subgroup save(Subgroup subgroup){
        return subgroupRepository.save(subgroup);
    }

    public void deleteAllInBatch(List<Subgroup> songSubgroupList){
        subgroupRepository.deleteAllInBatch(songSubgroupList);
    }

}
