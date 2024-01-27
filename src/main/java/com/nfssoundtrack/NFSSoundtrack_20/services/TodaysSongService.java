package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Country;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.TodaysSong;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorAliasRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import com.nfssoundtrack.NFSSoundtrack_20.repository.TodaysSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TodaysSongService {

    @Autowired
    TodaysSongRepository todaysSongRepository;

    @Autowired
    SongSubgroupRepository songSubgroupRepository;

    public List<TodaysSong> findAll() {
        return todaysSongRepository.findAll();
    }

    public TodaysSong getTodaysSong() throws ResourceNotFoundException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate localDate = LocalDate.now();
        String dateAsString = dtf.format(localDate);
        Date dbDate = Date.valueOf(dateAsString);
        Optional<TodaysSong> foundSong = todaysSongRepository.findByDate(dbDate);
        if (foundSong.isPresent()){
            return foundSong.get();
        } else {
            Long biggestId = songSubgroupRepository.findTopByOrderByIdDesc().getId();
            int nextSongId = ThreadLocalRandom.current().nextInt(1, Math.toIntExact(biggestId));
            SongSubgroup targetSong = songSubgroupRepository.findById(nextSongId).orElseThrow(() ->
                    new ResourceNotFoundException("no songsubgroup found with id " + nextSongId));
            TodaysSong todaysSong = new TodaysSong();
            todaysSong.setSongSubgroup(targetSong);
            todaysSong.setDate(dbDate);
            todaysSong = todaysSongRepository.save(todaysSong);
            return todaysSong;
        }
    }

    public List<TodaysSong> findAllFromLast30Days(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        LocalDate localDate = LocalDate.now();
        String dateAsString = dtf.format(localDate);
        Date todayDate = Date.valueOf(dateAsString);
        LocalDate monthAgo = localDate.minusDays(30);
        String oldDateAsString = dtf.format(monthAgo);
        Date monthAgoDate = Date.valueOf(oldDateAsString);
        List<TodaysSong> todaysSongs = todaysSongRepository.findByDateBetween(monthAgoDate,todayDate, Sort.by(Sort.Direction.DESC, "date"));
        return todaysSongs;
    }
}
