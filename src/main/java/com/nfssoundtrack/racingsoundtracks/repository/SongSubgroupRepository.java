package com.nfssoundtrack.racingsoundtracks.repository;

import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Subgroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongSubgroupRepository extends JpaRepository<SongSubgroup, Integer> {

    List<SongSubgroup> findBySong(Song song);

    List<SongSubgroup> findByFilename(String filename);

    List<SongSubgroup> findByFilenameStartsWith(String filename);

    List<SongSubgroup> findBySongIn(List<Song> songs, Sort sort);

    SongSubgroup findTopByOrderByIdDesc();
}

