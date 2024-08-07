package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.TodaysSong;
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
