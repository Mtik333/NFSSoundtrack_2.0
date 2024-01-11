package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Role;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JustSomeHelper {

	public static void fillMapForArtistDisplay(AuthorAlias authorAlias, AuthorSong authorSong, Role role,
										 Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsComposer,
										 List<SongSubgroup> songSubgroupList) {
		if (role.equals(authorSong.getRole())) {
			if (songsAsComposer.get(authorAlias) == null) {
				Map<Song, List<SongSubgroup>> songsPerSubgroup = new HashMap<>();
				songsPerSubgroup.put(authorSong.getSong(), songSubgroupList);
				songsAsComposer.put(authorAlias, songsPerSubgroup);
			} else {
				Map<Song, List<SongSubgroup>> songsPerSubgroup = songsAsComposer.get(authorAlias);
				if (songsPerSubgroup.get(authorSong.getSong()) != null) {
					songsPerSubgroup.get(authorSong.getSong()).addAll(songSubgroupList);
				} else {
					songsPerSubgroup.put(authorSong.getSong(), songSubgroupList);
				}
				songsAsComposer.put(authorAlias, songsPerSubgroup);
			}
		}
	}

}
