package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Correction;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.CorrectionStatus;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.CorrectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorrectionService {

    @Autowired
    CorrectionRepository correctionRepository;

    public Correction save(Correction correction) {
        return correctionRepository.save(correction);
    }

    public List<Correction> findBySongSubgroup(SongSubgroup songSubgroup) {
        return correctionRepository.findBySongSubgroup(songSubgroup);
    }

    public List<Correction> findByCorrectionStatus(CorrectionStatus correctionStatus) {
        return correctionRepository.findByCorrectionStatus(correctionStatus);
    }


}
