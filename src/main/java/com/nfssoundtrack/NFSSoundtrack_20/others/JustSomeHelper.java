package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JustSomeHelper {

    public static String returnProperValueToDb(String value) {
        if ("".equals(value) || "null".equals(value) || "undefined".equals(value)) {
            return null;
        }
        return value;
    }

    public static void fillMapForArtistDisplay(AuthorAlias authorAlias, AuthorSong authorSong, Role role,
                                               Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsComposer,
                                               List<SongSubgroup> songSubgroupList) {
        if (role.equals(authorSong.getRole())) {
            if (songsAsComposer.get(authorAlias) == null) {
                Map<Song, List<SongSubgroup>> songsPerSubgroup = new TreeMap<>(Comparator.comparing(Song::getOfficialDisplayTitle));
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
