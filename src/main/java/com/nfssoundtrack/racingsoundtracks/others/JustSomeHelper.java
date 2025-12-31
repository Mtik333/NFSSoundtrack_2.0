package com.nfssoundtrack.racingsoundtracks.others;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.lyrics.Lyrics;
import com.nfssoundtrack.racingsoundtracks.services.CorrectionService;
import com.nfssoundtrack.racingsoundtracks.services.SongSubgroupService;
import com.nfssoundtrack.racingsoundtracks.services.TodaysSongService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Map.entry;

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
                Optional<SongSubgroup> optionalReplacementSongSubgroup = allSongSubgroups.stream().filter(songSubgroup1 -> !songSubgroup1.getId().equals(songSubgroup.getId())).findFirst();
                if (optionalReplacementSongSubgroup.isEmpty()){
                    return;
                }
                SongSubgroup replacementSongSubgroup = optionalReplacementSongSubgroup.get();
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
    public static <T>ObjectMapper registerSerializerForObjectMapper(Class<T> classToUse, JsonSerializer<T> jsonSerializer) {
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
    public static <T>ObjectMapper registerDeserializerForObjectMapper(Class<T> classToUse, JsonDeserializer<T> jsonDeserializer) {
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

    public static Map<String,String> getInvalidCharsForTitle(){
        return Map.ofEntries(
                entry("'",""),
                entry("!",""),
                entry("#",""),
                entry("$","S"),
                entry("&","and"),
                entry("(",""),
                entry(")",""),
                entry("-",""),
                entry("+",""),
                entry("=",""),
                entry(":",""),
                entry(",",""),
                entry(".",""),
                entry("/",""),
                entry("?",""),
                entry("\"","")
        );
    }

    public static Map<String,String> getInvalidCharsForBand(){
        return Map.ofEntries(
                entry("'",""),
                entry("!",""),
                entry("#",""),
                entry("$","S"),
                entry("&","and"),
                entry("(",""),
                entry(")",""),
                entry("+",""),
                entry("=",""),
                entry(":",""),
                entry(",",""),
                entry(".",""),
                entry("/",""),
                entry("?",""),
                entry("\"","")
        );
    }

    public static String removeVariousSpecialCharactersFromString(String entry, Map<String,String> chars){
        for (Map.Entry<String,String> singleChar : chars.entrySet()){
            entry = entry.replace(singleChar.getKey(),singleChar.getValue());
        }
        return entry;
    }

    public static Lyrics getLrcLibLyrics(Song song) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String lrcLibUrl = "https://lrclib.net/api/search";
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.add("user-agent", "RacingSoundtracks v1.0 (https://racingsoundtracks.com)");
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(lrcLibUrl)
                    .queryParam("track_name", URLEncoder.encode(
                            JustSomeHelper.removeVariousSpecialCharactersFromString(
                                    song.getOfficialDisplayTitle(),JustSomeHelper.getInvalidCharsForTitle()),
                            StandardCharsets.UTF_8))
                    .queryParam("artist_name", URLEncoder.encode(
                            JustSomeHelper.removeVariousSpecialCharactersFromString(
                                    song.getOfficialDisplayBand(),JustSomeHelper.getInvalidCharsForBand()),
                            StandardCharsets.UTF_8));
            HttpEntity<String> stringHttpEntity = restTemplate.exchange(
                    builder.toUriString(), HttpMethod.GET, entity, String.class);
            String body = stringHttpEntity.getBody();
            JSONArray listOfFoundEntries = new JSONArray(body);
            if (listOfFoundEntries.isEmpty()) {
                return null;
            }
            JSONObject json = listOfFoundEntries.getJSONObject(0);
            String title = json.getString("trackName");
            String artist = json.getString("artistName");
            String songLyrics = json.optString("plainLyrics");
            String songUrl = json.optString("url");
            if (songLyrics.isEmpty()){
                songLyrics = "Instrumental";
            }
            return new Lyrics(title, artist, songLyrics, songUrl, "LrcLib");
        } catch (NullPointerException | JSONException ex) {
            return null;
        }
    }
}
