package com.nfssoundtrack.racingsoundtracks.services;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Correction;
import com.nfssoundtrack.racingsoundtracks.dbmodel.CorrectionStatus;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.repository.CorrectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorrectionService {

    private final CorrectionRepository correctionRepository;

    public CorrectionService(CorrectionRepository correctionRepository) {
        this.correctionRepository = correctionRepository;
    }

    public Correction save(Correction correction) {
        return correctionRepository.save(correction);
    }

    public List<Correction> findAll() {
        return correctionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Page<Correction> findAll(int page) {
        return correctionRepository.findAll(
                PageRequest.of(page, 50, Sort.by(Sort.Direction.DESC, "id")));
    }

    public List<Correction> findBySongSubgroup(SongSubgroup songSubgroup) {
        return correctionRepository.findBySongSubgroup(songSubgroup);
    }

    public List<Correction> findByCorrectionStatus(CorrectionStatus correctionStatus) {
        return correctionRepository.findByCorrectionStatus(correctionStatus);
    }


}
