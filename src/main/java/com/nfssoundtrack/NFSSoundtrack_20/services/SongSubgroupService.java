package com.nfssoundtrack.NFSSoundtrack_20.services;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Role;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import com.nfssoundtrack.NFSSoundtrack_20.repository.SongSubgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SongSubgroupService {

	@Autowired
	AuthorAliasService authorAliasService;

	@Autowired
	AuthorSongService authorSongService;

	@Autowired
	AuthorService authorService;

	@Autowired
	SongSubgroupRepository songSubgroupRepository;


	public Optional<SongSubgroup> findById(Integer id) {
		return songSubgroupRepository.findById(id);
	}

	public List<SongSubgroup> findBySong(Song song) {
		return songSubgroupRepository.findBySong(song);
	}

	public List<SongSubgroup> findBySongInSortedByIdAsc(List<Song> songs) {
		return songSubgroupRepository.findBySongIn(songs, Sort.by(Sort.Direction.ASC, "id"));
	}

	public void deleteAllInBatch(List<SongSubgroup> songSubgroupList) {
		songSubgroupRepository.deleteAllInBatch(songSubgroupList);
	}

	public void delete(SongSubgroup songSubgroup) {
		songSubgroupRepository.delete(songSubgroup);
	}

	public List<SongSubgroup> saveAll(List<SongSubgroup> songSubgroupList) {
		return songSubgroupRepository.saveAll(songSubgroupList);
	}

	public SongSubgroup save(SongSubgroup songSubgroup) {
		return songSubgroupRepository.save(songSubgroup);
	}

	public List<SongSubgroup> fetchFromGame(Game game) {
		List<SongSubgroup> allSongs = new ArrayList<>();
		for (MainGroup mainGroup : game.getMainGroups()) {
			for (Subgroup subgroup : mainGroup.getSubgroups()) {
				allSongs.addAll(subgroup.getSongSubgroupList());
			}
		}
		return allSongs;
	}

	public void updateFeat(Map<String, String> objectMapper, String comingInput, String comingConcatInput,
						   SongSubgroup songSubgroup, Role role, Song relatedSong) throws Exception {
		List<String> comingFeats = objectMapper.keySet().stream().filter(
				o -> o.contains(comingInput)).toList();
		Iterator<String> comingConcats = objectMapper.keySet().stream().filter(
				o -> o.contains(comingConcatInput)).toList().iterator();
		for (String comingFeat : comingFeats) {
			String concatVal = null;
			if (comingConcats.hasNext()) {
				concatVal = objectMapper.get(comingConcats.next());
			}
			String keyFeat = comingFeat;
			String featValue = objectMapper.get(keyFeat);
			if (featValue.startsWith("NEW")) {
				String actualFeatValue = featValue.replace("NEW-", "");
				saveNewFeatOrRemixer(actualFeatValue, songSubgroup, role, concatVal);
			} else if (featValue.startsWith("DELETE")) {
				String deleteFeatId = featValue.replace("DELETE-", "");
				AuthorAlias authorAlias = authorAliasService.findById(Integer.parseInt(deleteFeatId))
						.orElseThrow(() -> new Exception("No authoralias found with id " + deleteFeatId));
				AuthorSong authorSong = authorSongService.findByAuthorAliasAndSong(authorAlias, relatedSong)
						.orElseThrow(() -> new Exception("No authorsong found"));
				authorSongService.delete(authorSong);
			} else {
				saveNewAssignmentOfExistingFeatRemixer(featValue, songSubgroup, role, concatVal);
			}
		}
	}

	private void saveNewFeatOrRemixer(String actualRemixValue, SongSubgroup songSubgroup, Role role, String concatValue) {
		Author author = new Author();
		author.setName(actualRemixValue);
		author = authorService.save(author);
		AuthorAlias authorAlias = new AuthorAlias(author, actualRemixValue);
		authorAlias = authorAliasService.save(authorAlias);
		AuthorSong authorSong = new AuthorSong(authorAlias, songSubgroup.getSong(), role);
		if (role.equals(Role.REMIX)) {
			authorSong.setRemixConcat(concatValue);
		}
		if (role.equals(Role.FEAT)) {
			authorSong.setFeatConcat(concatValue);
		}
		if (role.equals(Role.SUBCOMPOSER)) {
			authorSong.setSubcomposerConcat(concatValue);
		}
		authorSongService.save(authorSong);
	}


	private void saveNewAssignmentOfExistingFeatRemixer(String remixValue, SongSubgroup songSubgroup, Role role, String concatValue) {
		AuthorAlias authorAlias = authorAliasService.findById(Integer.parseInt(remixValue)).get();
		boolean alreadyAssigned = false;
		for (AuthorSong authorSong : songSubgroup.getSong().getAuthorSongList()) {
			if (authorSong.getAuthorAlias().equals(authorAlias)) {
				alreadyAssigned = true;
				break;
			}
		}
		if (!alreadyAssigned) {
			AuthorSong authorSong = new AuthorSong(authorAlias, songSubgroup.getSong(), role);
			if (role.equals(Role.REMIX)) {
				authorSong.setRemixConcat(concatValue);
			}
			if (role.equals(Role.FEAT)) {
				authorSong.setFeatConcat(concatValue);
			}
			if (role.equals(Role.SUBCOMPOSER)) {
				authorSong.setSubcomposerConcat(concatValue);
			}
			authorSongService.save(authorSong);
		}
	}
}
