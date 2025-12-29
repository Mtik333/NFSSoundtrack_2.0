package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.others.lyrics.Lyrics;
import com.nfssoundtrack.racingsoundtracks.others.lyrics.LyricsClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * controller used to merge songs and find exact song when creating a new one
 */
@RestController
@RequestMapping(path = "/song")
public class SongController  {

    private final BaseControllerWithErrorHandling baseController;

    public SongController(BaseControllerWithErrorHandling baseController) {
        this.baseController = baseController;
    }

    /**
     * method used to merge one song with another
     * used in mergeSong.js when you click on 'save' in 'merge songs' module
     *
     * @param formData consists of ids of song to merge and target song to be merged into
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String mergeSongs(@RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        //so we are supposed to put id of two songs, target song being the one that remains after merge
        String idToMerge = (String) mergeInfo.get("songToMergeId");
        String targetId = (String) mergeInfo.get("targetSongId");
        //maybe we want to merge songs but keep the ingame title intact because that's how it is in the game
        Boolean addInGameTitle = (Boolean) mergeInfo.get("pushIngameTitle");
        Song songToMerge =
                baseController.getSongService().findById(Integer.valueOf(idToMerge)).orElseThrow(
                        () -> new ResourceNotFoundException("No song with id " +
                                "found " + idToMerge));
        Song targetSong = baseController.getSongService().findById(Integer.valueOf(targetId)).orElseThrow(
                () -> new ResourceNotFoundException("No song with id " +
                        "found " + idToMerge));
        String message = "Merging song " + songToMerge.toAnotherChangeLogString()
                + " into song " + targetSong.toAnotherChangeLogString();
        //aside from the songs found, we need to check the genres of both songs
        List<SongGenre> songGenresFromMerge = baseController.getSongGenreService().findBySong(songToMerge);
        List<SongGenre> songGenresFromTarget = baseController.getSongGenreService().findBySong(targetSong);
        List<SongGenre> songGenresToDelete = new ArrayList<>();
        for (SongGenre songGenre : songGenresFromMerge) {
            //we dont want to duplicate genres per song after the merge
            //so we would keep them in list and get rid of them later
            Optional<SongGenre> songGenreDuplicate = songGenresFromTarget.stream().filter(songGenre1
                    -> songGenre1.getGenre().equals(songGenre.getGenre())).findFirst();
            if (songGenreDuplicate.isPresent()) {
                songGenresToDelete.add(songGenre);
            } else {
                //for genres that are on to-merge song, we have to associate them with target song now
                songGenre.setSong(targetSong);
            }
        }
        baseController.getSongGenreService().saveAll(songGenresFromMerge);
        baseController.getSongGenreService().deleteAll(songGenresToDelete);
        List<SongSubgroup> songSubgroupList = baseController.getSongSubgroupService().findBySong(songToMerge);
        //now for each usage of song in a subgroup we will change association to the target song
        //and change the links too, inherit from target
        for (SongSubgroup songSubgroup : songSubgroupList) {
            songSubgroup.setSong(targetSong);
            if (Boolean.TRUE.equals(addInGameTitle)) {
                songSubgroup.setIngameDisplayTitle(songToMerge.getOfficialDisplayTitle());
            }
            songSubgroup.setSpotifyId(targetSong.getSpotifyId());
            songSubgroup.setDeezerId(targetSong.getDeezerId());
            songSubgroup.setTidalLink(targetSong.getTidalLink());
            songSubgroup.setSoundcloudLink(targetSong.getSoundcloudLink());
            songSubgroup.setItunesLink(targetSong.getItunesLink());
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            //as we merge song in a game, we clear cached info about songs in that game
            //so that when acessing the page, songs list will get updated
            baseController.removeCacheEntry(gameShort);
        }
        //now we are safe to update song-subgroup associatiosn and get rid of song completely
        baseController.getSongSubgroupService().saveAll(songSubgroupList);
        List<AuthorSong> authorSongs = baseController.getAuthorSongService().findBySong(songToMerge);
        //song can be associated with 1 or more composers so we delete associations too
        baseController.getAuthorSongService().deleteAll(authorSongs);
        baseController.getSongService().delete(songToMerge);
        baseController.sendMessageToChannel(EntityType.SONG, "update", message,
                EntityUrl.SONG, targetSong.toAnotherChangeLogString(), String.valueOf(targetSong.getId()));
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method used to find song that exists already in the database
     * used in songsMgmt.js when you type in on 'official title' when creating new song via 'manage songs'
     * when editing song globally, 'existing song id' will be there already
     * so you can imagine that we have a new game which suddenly uses song
     * that was in the database already, so we dont want to duplicate it
     * and this method is supposed to find such song and give back the id of song
     *
     * @param formData is just the name of artist / band and title of the song
     * @return json with song if such was found
     * @throws JsonProcessingException
     */
    @PutMapping(value = "/findExact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String findSong(@RequestBody String formData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        if (formData.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        String band = (String) mergeInfo.get("band");
        String title = (String) mergeInfo.get("title");
        //made this dedicated query to look for such song there
        //i dont think there would be 2 identical songs but maybe that could happen?
        List<Song> matchingSongs = baseController.getSongService().findByOfficialDisplayBandAndOfficialDisplayTitleContains(band, title);
        if (!matchingSongs.isEmpty()) {
            return objectMapper.writeValueAsString(matchingSongs.get(0));
        } else {
            return objectMapper.writeValueAsString(null);
        }
    }

    @GetMapping(value = "/lyrics/{songId}")
    public String fetchLyrics(@PathVariable("songId") int songId) throws ResourceNotFoundException, ExecutionException, InterruptedException {
        Song song = baseController.getSongService().findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("No song " + "found with id " + songId));
        String songInfo = song.toAnotherChangeLogString();
        String encodedSongInfo = URLEncoder.encode(songInfo, StandardCharsets.UTF_8);
        String lrcLibUrl = "https://lrclib.net/api/get?";
        String lrcLibSongInfo = "artist_name="+song.getOfficialDisplayBand()
                +"&track_name="+song.getOfficialDisplayTitle();
        String lrcLibSearch = lrcLibUrl+lrcLibSongInfo;
        LyricsClient client;
        Lyrics lyrics;
        //we keep trying by various services
        client = new LyricsClient();
        lyrics = client.getLrcLibLyrics(encodedSongInfo,lrcLibSearch).get();
        if (lyrics!=null){
            String content = lyrics.getContent();
            content = content.replace("\n\n\n","<br><br>").replace("\n\n","<br>");
            return content;
        }
//        client = new LyricsClient("Spotify");
//        lyrics = client.getLyricsNoSearch(encodedSongInfo,
//                "https://open.spotify.com/track/"+song.getSpotifyId().substring(14), "Spotify").get();
//        if (lyrics!=null){
//            String content = lyrics.getContent();
//            content = content.replace("\n\n\n","<br><br>").replace("\n\n","<br>");
//            return content;
//        }
        client = new LyricsClient();
        lyrics = client.getLyrics(encodedSongInfo).get();
        if (lyrics!=null){
            String content = lyrics.getContent();
            content = content.replace("\n\n\n","<br><br>").replace("\n\n","<br>");
            return content;
        }
        client = new LyricsClient("Genius");
        lyrics = client.getLyrics(encodedSongInfo).get();
        if (lyrics!=null){
            String content = lyrics.getContent();
            content = content.replace("\n\n\n","<br><br>").replace("\n\n","<br>");
            return content;
        }
        return "";
    }

}
