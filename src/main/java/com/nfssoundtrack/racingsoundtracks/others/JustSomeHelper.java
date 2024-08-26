package com.nfssoundtrack.racingsoundtracks.others;

import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorAlias;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorSong;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Correction;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Role;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.TodaysSong;
import com.nfssoundtrack.racingsoundtracks.services.CorrectionService;
import com.nfssoundtrack.racingsoundtracks.services.SongSubgroupService;
import com.nfssoundtrack.racingsoundtracks.services.TodaysSongService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JustSomeHelper {

    private JustSomeHelper() {
    }

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

    public static void unlinkSongWithCorrection(CorrectionService correctionService, SongSubgroup songSubgroup,
                                                String correctValue){
        List<Correction> relatedCorrections = correctionService.findBySongSubgroup(songSubgroup);
        for (Correction correction : relatedCorrections) {
            correction.setSongSubgroup(null);
            correction.setCorrectValue(correction.getCorrectValue()+correctValue);
            correctionService.save(correction);
        }
    }

    public static void unlinkSongWithTodaysSong(TodaysSongService todaysSongService, SongSubgroup songSubgroup,
                                                Song song, SongSubgroupService songSubgroupService){
        List<TodaysSong> todaysSongs = todaysSongService.findAllBySongSubgroup(songSubgroup);
        if (todaysSongs.size() == 1) {
            List<SongSubgroup> allSongSubgroups = songSubgroupService.findBySong(song);
            if (allSongSubgroups.size() > 1) {
                SongSubgroup replacementSongSubgroup = allSongSubgroups.stream().filter(songSubgroup1 -> !songSubgroup1.getId().equals(songSubgroup.getId())).findFirst().get();
                TodaysSong todaysSong = todaysSongs.get(0);
                todaysSong.setSongSubgroup(replacementSongSubgroup);
                todaysSongService.save(todaysSong);
            } else {
                List<SongSubgroup> songSubgroupList = songSubgroup.getSubgroup().getSongSubgroupList();
                SongSubgroup replacementSongSubgroup = songSubgroupList.get(Math.abs(songSubgroupList.indexOf(songSubgroup) - 1));
                TodaysSong todaysSong = todaysSongs.get(0);
                todaysSong.setSongSubgroup(replacementSongSubgroup);
                todaysSongService.save(todaysSong);
            }
        }
    }
}
