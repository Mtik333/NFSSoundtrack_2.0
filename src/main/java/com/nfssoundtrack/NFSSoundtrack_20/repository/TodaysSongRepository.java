package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.TodaysSong;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TodaysSongRepository extends JpaRepository<TodaysSong, Long> {

    Optional<TodaysSong> findByDate(Date date);

    List<TodaysSong> findByDateBetween(Date startDate, Date endDate, Sort sort);

    List<TodaysSong> findBySongSubgroup(SongSubgroup songSubgroup);
}
