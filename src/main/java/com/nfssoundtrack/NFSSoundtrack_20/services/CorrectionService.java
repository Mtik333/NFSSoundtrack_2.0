package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Correction;
import com.nfssoundtrack.NFSSoundtrack_20.repository.CorrectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorrectionService {

    @Autowired
    CorrectionRepository correctionRepository;

    public Correction save(Correction correction) {
        return correctionRepository.save(correction);
    }

}
