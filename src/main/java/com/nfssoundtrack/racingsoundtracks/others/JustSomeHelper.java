package com.nfssoundtrack.racingsoundtracks.others;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.services.CorrectionService;
import com.nfssoundtrack.racingsoundtracks.services.SongSubgroupService;
import com.nfssoundtrack.racingsoundtracks.services.TodaysSongService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class JustSomeHelper {

    private JustSomeHelper() {
    }

    /**
     * always struggling with these damn nulls here and there
     *
     * @param value
     * @return
     */
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

    /**
     * method to change song association with correction to another song-subgroup
     *
     * @param correctionService db service
     * @param songSubgroup      song-subgroup to unlink correction from
     * @param correctValue      value of correction (we're mostly writing song title in such info)
     */
    public static void unlinkSongWithCorrection(CorrectionService correctionService, SongSubgroup songSubgroup,
                                                String correctValue) {
        List<Correction> relatedCorrections = correctionService.findBySongSubgroup(songSubgroup);
        for (Correction correction : relatedCorrections) {
            correction.setSongSubgroup(null);
            correction.setCorrectValue(correction.getCorrectValue() + correctValue);
            correctionService.save(correction);
        }
    }

    /**
     * this is a bit more complicated to unlink song with today's song
     *
     * @param todaysSongService   service of today's song table in db
     * @param songSubgroup        song-subgroup we want to unlink
     * @param song                helps finding other example of same song used
     * @param songSubgroupService just the database service to handle various requests
     */
    public static void unlinkSongWithTodaysSong(TodaysSongService todaysSongService, SongSubgroup songSubgroup,
                                                Song song, SongSubgroupService songSubgroupService) {
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

    /**
     * using object mapper so many times that we can move these 5 lines to one place
     *
     * @param classToUse     class used for this purpose of serialization like game.java
     * @param jsonSerializer type of serializer used to serialize the data
     * @return objectmapper to use further in controllers
     */
    public static ObjectMapper registerSerializerForObjectMapper(Class<?> classToUse, JsonSerializer jsonSerializer) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(classToUse, jsonSerializer);
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    /**
     * using object mapper so many times that we can move these 5 lines to one place
     *
     * @param classToUse       class used for this purpose of serialization like game.java
     * @param jsonDeserializer type of deserializer used to deserialize the data
     * @return objectmapper to use further in controllers
     */
    public static ObjectMapper registerDeserializerForObjectMapper(Class<?> classToUse, JsonDeserializer jsonDeserializer) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(classToUse, jsonDeserializer);
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    /**
     * i just copied that from somewhere
     *
     * @param keyExtractor probably attribute we filter by
     * @param <T>          just generic type?
     * @return predicate used to get distinct objects from stream
     */
    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
