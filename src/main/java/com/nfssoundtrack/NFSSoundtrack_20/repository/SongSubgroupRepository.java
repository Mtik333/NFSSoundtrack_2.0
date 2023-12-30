package com.nfssoundtrack.NFSSoundtrack_20.repository;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongSubgroupRepository extends JpaRepository<SongSubgroup,Integer> {

    List<SongSubgroup> findBySong(Song song);
    List<SongSubgroup> findBySongIn(List<Song> songs, Sort sort);
    SongSubgroup findBySongAndSubgroup(Song song, Subgroup subgroup);
    List<SongSubgroup> findBySubgroupIdIn(List<Subgroup> subgroups);
}
