package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Correction;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.CorrectionStatus;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrectionRepository extends JpaRepository<Correction, Integer> {

    List<Correction> findBySongSubgroup(SongSubgroup songSubgroup);

    List<Correction> findByCorrectionStatus(CorrectionStatus correctionStatus);
}
