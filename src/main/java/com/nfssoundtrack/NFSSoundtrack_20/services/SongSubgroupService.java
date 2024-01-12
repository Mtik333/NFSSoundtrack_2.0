package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SongSubgroupService {

	@Autowired
	SongSubgroupRepository songSubgroupRepository;

	public Optional<SongSubgroup> findById(Integer id){
		return songSubgroupRepository.findById(id);
	}

	public List<SongSubgroup> findBySong(Song song){
		return songSubgroupRepository.findBySong(song);
	}

	public List<SongSubgroup> findBySongInSortedByIdAsc(List<Song> songs){
		return songSubgroupRepository.findBySongIn(songs, Sort.by(Sort.Direction.ASC, "id"));
	}

	public void deleteAllInBatch(List<SongSubgroup> songSubgroupList){
		songSubgroupRepository.deleteAllInBatch(songSubgroupList);
	}

	public List<SongSubgroup> fetchFromGame(Game game){
		List<SongSubgroup> allSongs = new ArrayList<>();
		for (MainGroup mainGroup : game.getMainGroups()) {
			for (Subgroup subgroup : mainGroup.getSubgroups()) {
				allSongs.addAll(subgroup.getSongSubgroupList());
			}
		}
		return allSongs;
	}
}
