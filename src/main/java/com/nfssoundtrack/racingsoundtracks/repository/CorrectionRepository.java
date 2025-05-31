package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Correction;
import com.nfssoundtrack.racingsoundtracks.dbmodel.CorrectionStatus;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrectionRepository extends JpaRepository<Correction, Integer> {

    List<Correction> findBySongSubgroup(SongSubgroup songSubgroup);

    List<Correction> findByCorrectionStatus(CorrectionStatus correctionStatus);

    Page<Correction> findAll(Pageable pageable);
}
