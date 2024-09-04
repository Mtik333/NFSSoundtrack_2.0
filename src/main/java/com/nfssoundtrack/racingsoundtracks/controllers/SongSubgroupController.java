package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.deserializers.SongDeserializer;
import com.nfssoundtrack.racingsoundtracks.deserializers.SongSubgroupDeserializer;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * controller used for probably most complicated stuff on the website
 * song-subgroup associations
 * used on songsMgmt.js and subgroupMgmt.js
 */
@Controller
@RequestMapping("/songSubgroup")
public class SongSubgroupController extends BaseControllerWithErrorHandling {

    @Autowired
    SongSubgroupDeserializer songSubgroupDeserializer;

    @Autowired
    SongDeserializer songDeserializer;

    /**
     * method used to update positions of songs in a subgroup
     * used in subgroupMgmt.js when you click on "update positions in db" after select subgroup from song
     *
     * @param subgroupId id of subgroup we want to update
     * @param formData   consists of id of song-subgroup references and their new positions
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/positions/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSubgroupPositions(@PathVariable("subgroupId") int subgroupId,
                                @RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        SongSubgroup firstSongSubgroup = null;
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            long songSubgroupId = Long.parseLong(String.valueOf(linkedHashMap.get("songSubgroupId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            SongSubgroup songSubgroup =
                    songSubgroupService.findById(Math.toIntExact(songSubgroupId)).orElseThrow(() -> new ResourceNotFoundException("no" +
                            " songsubgroup found with id " + subgroupId));
            //we just find the song-subgroup based on id and just change position
            songSubgroup.setPosition(position);
            songSubgroupService.save(songSubgroup);
            firstSongSubgroup = songSubgroup;
        }
        if (!objectMapper.isEmpty()) {
            if (firstSongSubgroup != null) {
                String gameShort = firstSongSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
                //we want to immediately update the game's page once songs order got changed
                removeCacheEntry(gameShort);
            }
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method used to update songsubgroup, in songsMgmt.js
     * you can see once you go to 'manage songs' and try to 'save song' in subgroup
     *
     * @param songSubgroupId id of song-subgroup id
     * @param formData       basically all the fields of song-subgroup entity
     * @return OK if successful
     * @throws JsonProcessingException
     */
    @PutMapping(value = "/put/{songSubgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSongSubgroup(@PathVariable("songSubgroupId") int songSubgroupId, @RequestBody String formData)
            throws JsonProcessingException {
        try {
            //first let's find this song-subgroup
            SongSubgroup songSubgroup = songSubgroupService.findById(songSubgroupId).orElseThrow(() -> new ResourceNotFoundException(
                    "No songsubgroup found with id " + songSubgroupId));
            ObjectMapper objectMapper = JustSomeHelper.registerDeserializerForObjectMapper(SongSubgroup.class, songSubgroupDeserializer);
            //readForUpdating is capable of opening entity to be updated based on incoming data, somehow
            songSubgroup = objectMapper.readerForUpdating(songSubgroup).readValue(formData, SongSubgroup.class);
            Map<String, String> localObjectMapper = new ObjectMapper().readValue(formData,
                    TypeFactory.defaultInstance().constructMapType(Map.class, String.class, String.class));
            //as for saving song we might have some additional info on songs like feats, subcomposers
            boolean feat = Boolean.parseBoolean(localObjectMapper.get("feat"));
            boolean subcomposer = Boolean.parseBoolean(localObjectMapper.get("subcomposer"));
            boolean remix = Boolean.parseBoolean(localObjectMapper.get("remix"));
            //this will propagate youtube / spotify / other links to songsubgroup-entity from song entity
            boolean propagate = Boolean.parseBoolean(localObjectMapper.get("propagate"));
            //we can set song as remix of other song
            String remixOf = localObjectMapper.get("remixOf");
            //possible that composer uses different alias for a specific song
            String mainAliasId = localObjectMapper.get("aliasId");
            //similarly by creating song, we might have a new author in database
            String authorId = localObjectMapper.get("authorId");
            Song relatedSong = songSubgroup.getSong();
            Author mainComposer;
            AuthorAlias composerAlias;
            if (authorId != null && authorId.startsWith("NEW")) {
                String newAuthor = authorId.replace("NEW-", "");
                //if that's a new author in DB, we create entity
                mainComposer = new Author();
                mainComposer.setName(newAuthor);
                mainComposer = authorService.save(mainComposer);
                composerAlias = new AuthorAlias(mainComposer, newAuthor);
                authorAliasService.save(composerAlias);
            } else {
                //otherwise we look up the existing author
                Author author =
                        authorService.findById(Integer.parseInt(authorId)).orElseThrow(() -> new ResourceNotFoundException("No author" +
                                " find id found " + authorId));
                //if there's a new alias of him we will create new alias obviously
                if (mainAliasId.startsWith("NEW")) {
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias(author, newAlias);
                    composerAlias = authorAliasService.save(composerAlias);
                } else {
                    composerAlias = authorAliasService.findById(Integer.parseInt(mainAliasId)).orElseThrow(
                            () -> new ResourceNotFoundException("No author" +
                                    " find id found " + mainAliasId));
                }
                //as this song exists already we just fetch existing authors of song
                List<AuthorSong> authorSongList = relatedSong.getAuthorSongList();
                for (AuthorSong authorSong : authorSongList) {
                    if (Role.COMPOSER.equals(authorSong.getRole())) {
                        Author persistedAuthor = authorSong.getAuthorAlias().getAuthor();
                        //so we change song and we might try to change author entirely
                        if (persistedAuthor.equals(author)) {
                            //if that's not the case, then we just associate new alias of author with this song
                            AuthorAlias persistedAuthorAlias = authorSong.getAuthorAlias();
                            if (!persistedAuthorAlias.equals(composerAlias)) {
                                authorSong.setAuthorAlias(composerAlias);
                                authorSongService.save(authorSong);
                            }
                        } else {
                            AuthorSong newAuthorSong = new AuthorSong(composerAlias, relatedSong, Role.COMPOSER);
                            authorSongService.save(newAuthorSong);
                            //case when we completely change the author
                            authorSongService.delete(authorSong);
                        }
                    }
                }
            }
            //three below are supposed to update feat / subcomposer / remix roles in the song
            if (subcomposer) {
                songSubgroupService.updateFeat(localObjectMapper, "subcomposerSelect",
                        "subcomposerConcatInput", songSubgroup, Role.SUBCOMPOSER, relatedSong, propagate);
            }
            if (feat) {
                songSubgroupService.updateFeat(localObjectMapper, "featSelect",
                        "featConcatInput", songSubgroup, Role.FEAT, relatedSong, propagate);
            }
            if (remix) {
                songSubgroupService.updateFeat(localObjectMapper, "remixSelect",
                        "remixConcatInput", songSubgroup, Role.REMIX, relatedSong, propagate);
            }
            //here we will associate song (if is now considered as remix) with the 'main' song
            //this isn't used that much but maybe it would at some point in the future
            if (!remixOf.isEmpty()) {
                Integer existingSongId = Integer.parseInt(remixOf);
                Optional<Song> existingSong = songService.findById(existingSongId);
                if (existingSong.isPresent()) {
                    songSubgroup.getSong().setBaseSong(existingSong.get());
                    songSubgroup.setRemix(Remix.YES);
                }
            }
            //as song was updated, we will cleanup cache of game's entry too to refresh UI
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            removeCacheEntry(gameShort);
            songSubgroupService.save(songSubgroup);
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Exception exp) {
            return new ObjectMapper().writeValueAsString(exp);
        }
    }

    /**
     * method usd to get song-subgroup entity from subgroup id
     * used in songsMgmt.js, you can see it when clicking on "edit song" (locally or globally)
     *
     * @param songSubgroupId id of song-subgroup
     * @return json of song-subgroup if id was found
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @GetMapping(value = "/read/{songSubgroupId}")
    public @ResponseBody
    String readAllSubgroupManage(@PathVariable("songSubgroupId") int songSubgroupId)
            throws JsonProcessingException, ResourceNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        SongSubgroup songSubgroup =
                songSubgroupService.findById(songSubgroupId).orElseThrow(() ->
                        new ResourceNotFoundException("No subgroup with id found " + songSubgroupId));
        return objectMapper.writeValueAsString(songSubgroup);
    }

    /**
     * method used to delete song from subgroup
     * used in songsMgmt.js when you click on "delete from subgroup" in "manage subgroups" on game's edit page
     *
     * @param songSubgroupId id of song-subgroup to delete
     * @return OK if successful
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @DeleteMapping(value = "/delete/{songSubgroupId}")
    public @ResponseBody
    String deleteSongSubgroup(@PathVariable("songSubgroupId") int songSubgroupId) throws JsonProcessingException, ResourceNotFoundException {
        SongSubgroup songSubgroup =
                songSubgroupService.findById(songSubgroupId).orElseThrow(() -> new ResourceNotFoundException("No subgroup found " +
                        "with id " + songSubgroupId));
        String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
        //as the song gets deleted, we need to fetch cache key earlier
        Song song = songSubgroup.getSong();
        List<SongSubgroup> allSongSubgroupEntries = songSubgroupService.findBySong(song);
        if (allSongSubgroupEntries.size() == 1) {
            //means we basically have to delete song entirely to avoid orphans
            List<SongGenre> songGenresToDelete = song.getSongGenreList();
            List<Genre> genresToDelete = new ArrayList<>();
            for (SongGenre songGenre : songGenresToDelete) {
                //so we do song cleanup: first we detect if song was the only one using specific genre
                Genre genre = songGenre.getGenre();
                List<SongGenre> allUsagesOfGenre = songGenreService.findByGenre(genre);
                if (allUsagesOfGenre.size() == 1) {
                    //then we will need to delete orphaned genre too
                    genresToDelete.add(genre);
                }
            }
            List<AuthorSong> authorsOfSong = song.getAuthorSongList();
            List<AuthorAlias> authorAliasToDelete = new ArrayList<>();
            List<Author> authorsToDelete = new ArrayList<>();
            List<AuthorCountry> authorCountriesToDelete = new ArrayList<>();
            for (AuthorSong authorSong : authorsOfSong) {
                //we get all authors of this song (including subcomposers / feat/ remix)
                Author author = authorSong.getAuthorAlias().getAuthor();
                List<AuthorSong> allUsagesOfAlias = authorSongService.findByAuthorAlias(
                        authorSong.getAuthorAlias());
                //in case of single usage of this alias overall, means that we can also get rid of orphaned alias
                if (allUsagesOfAlias.size() == 1) {
                    authorAliasToDelete.add(authorSong.getAuthorAlias());
                    List<AuthorAlias> authorAliases = authorAliasService.findByAuthor(author);
                    //if this was the only time author existed in database, means we basically get rid of this author
                    //and countries he's associated with
                    if (authorAliases.size() == 1) {
                        authorsToDelete.add(author);
                        authorCountriesToDelete.addAll(author.getAuthorCountries());
                        //potentially there could be some orphaned countries but let's ignroe that
                    }
                }
            }
            //so here we can safely remove association between song and author
            authorSongService.deleteAll(authorsOfSong);
            if (!authorsToDelete.isEmpty()) {
                //then, if that's the case, delete the author and his associations too
                authorCountryService.deleteAll(authorCountriesToDelete);
                authorAliasService.deleteAll(authorAliasToDelete);
                authorService.deleteAll(authorsToDelete);
            }
            //now we can delete genre-song associations
            songGenreService.deleteAll(songGenresToDelete);
            if (!genresToDelete.isEmpty()) {
                //and in case of orphaned entry we will delete genre too
                genreService.deleteAll(genresToDelete);
            }
            //if we remove song from subgroup but this specific one was used as today song
            //then either we link other usage of this song or just replace it with some other song
            JustSomeHelper.unlinkSongWithTodaysSong(todaysSongService, songSubgroup, song, songSubgroupService);
            JustSomeHelper.unlinkSongWithCorrection(correctionService, songSubgroup,
                    "; deleted song-subgroup: " + songSubgroup.toCorrectionString());
            //finally we get rid of song-subgroup association and the song in the end
            songSubgroupService.delete(songSubgroup);
            songService.delete(song);
        } else {
            //song used more than once in database, so we just make sure it is not associated with any today-song or correction
            JustSomeHelper.unlinkSongWithTodaysSong(todaysSongService, songSubgroup, song, songSubgroupService);
            JustSomeHelper.unlinkSongWithCorrection(correctionService, songSubgroup,
                    "; deleted song-subgroup: " + songSubgroup.toCorrectionString());
            songSubgroupService.delete(songSubgroup);
        }
        removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * method used to modify song "globally", used in songsMgmt.js
     * you can see it when going to 'manage songs' and button 'edit globally'
     *
     * @param songSubgroupId id of song-subgroup to update
     * @param formData       similar data as in normal PUT method
     * @return OK if successful
     * @throws JsonProcessingException
     */
    @PutMapping(value = "/putGlobally/{songSubgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putGlobally(@PathVariable("songSubgroupId") int songSubgroupId,
                       @RequestBody String formData) throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup = songSubgroupService.findById(songSubgroupId).orElseThrow(() -> new ResourceNotFoundException(
                    "No song subgroup with id found " + songSubgroupId));
            //so this time we are going to go for main song as we're going to modify it
            Song relatedSong = songSubgroup.getSong();
            ObjectMapper objectMapper = JustSomeHelper.registerDeserializerForObjectMapper(Song.class, songDeserializer);
            relatedSong = objectMapper.readerForUpdating(relatedSong).readValue(formData, Song.class);
            Map<String, String> localObjectMapper = new ObjectMapper().readValue(formData,
                    TypeFactory.defaultInstance().constructMapType(Map.class, String.class, String.class));
            //as genres are in JSON we will just try to check if there are any new genres added to song
            List<String> comingGenres = localObjectMapper.keySet().stream().filter(
                    o -> o.contains("genreSelect")).toList();
            for (String comingGenre : comingGenres) {
                String genreValue = localObjectMapper.get(comingGenre);
                if (genreValue.startsWith("NEW")) {
                    //so we just create genres based on incoming info - used plus button and that's a totally new genre
                    String actualGenreValue = genreValue.replace("NEW-", "");
                    Genre genre = new Genre();
                    genre.setGenreName(actualGenreValue);
                    genre = genreService.save(genre);
                    //and we associate genre with song
                    SongGenre songGenre = new SongGenre(songSubgroup.getSong(), genre);
                    songGenreService.save(songGenre);
                } else if (genreValue.startsWith("DELETE")) {
                    //if delete is expected (clicked minus red button), we're going to remove genre association
                    String deleteGenreId = genreValue.replace("DELETE-", "");
                    Genre genre = genreService.findById(Integer.parseInt(deleteGenreId))
                            .orElseThrow(() -> new ResourceNotFoundException("No genre found with id " + deleteGenreId));
                    List<SongGenre> existingGenres = songSubgroup.getSong().getSongGenreList();
                    for (SongGenre songGenre : existingGenres) {
                        if (songGenre.getGenre().equals(genre)) {
                            songGenreService.delete(songGenre);
                            break;
                        }
                    }
                } else {
                    //case when we just associate existing in database genre with this song for the first time
                    songService.saveNewAssignmentOfExistingGenre(genreValue, songSubgroup.getSong());
                }
            }
            //so now we can save genres
            relatedSong.setSongGenreList(songGenreService.findBySong(relatedSong));
            songService.save(relatedSong);
            //so for editing song globally, we always come from a subgroup entry so we also update related links
            //for both global song entry and this single local subgroup entry too
            if (Boolean.parseBoolean(localObjectMapper.get("propagate"))) {
                songSubgroup.setSrcId(relatedSong.getSrcId());
                songSubgroup.setSpotifyId(relatedSong.getSpotifyId());
                songSubgroup.setDeezerId(relatedSong.getDeezerId());
                songSubgroup.setItunesLink(relatedSong.getItunesLink());
                songSubgroup.setSoundcloudLink(relatedSong.getSoundcloudLink());
                songSubgroup.setTidalLink(relatedSong.getTidalLink());
                songSubgroupService.save(songSubgroup);
            }
            //we updated song globally so we again clear game from which this edit comes
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            removeCacheEntry(gameShort);
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Exception exp) {
            return new ObjectMapper().writeValueAsString(exp);
        }
    }

    /**
     * method used to create song (song-subgroup) in the database
     * you just go to 'manage songs' in game and click on 'new song'
     *
     * @param subgroupId id of subgroup where we create this song in
     * @param formData   all the song info that is submitted to database
     * @return OK if successful
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/post/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String postNewSong(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData) throws ResourceNotFoundException, JsonProcessingException {
        Subgroup subgroup = subgroupService.findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No song " +
                "subgroup found with id " + subgroupId));
        ObjectMapper songSubgroupObjectMapper = JustSomeHelper.registerDeserializerForObjectMapper(SongSubgroup.class, songSubgroupDeserializer);
        SongSubgroup songSubgroup = songSubgroupObjectMapper.readValue(formData, SongSubgroup.class);
        //so we can briefly prepare song-subgroup entry at this point and set the subgroup reference
        songSubgroup.setSubgroup(subgroup);
        Map<String, String> objectMapper = new ObjectMapper().readValue(formData,
                TypeFactory.defaultInstance().constructMapType(Map.class, String.class, String.class));
        //fetching alias and author id to determine if we need new entitites in such tables
        String mainAliasId = objectMapper.get("aliasId");
        String authorId = objectMapper.get("authorId");
        if (songSubgroup.getSong() != null) {
            //this is the case when we just use existing song in a new game (new subgroup)
            songSubgroupService.save(songSubgroup);
        } else {
            ObjectMapper songObjectMapper = JustSomeHelper.registerDeserializerForObjectMapper(Song.class, songDeserializer);
            //form data is so similar in song and song-subgroup that we can prepare song this way and save it to db
            Song song = songObjectMapper.readValue(formData, Song.class);
            song = songService.save(song);
            //we can now associate song with song-subgroup
            songSubgroup.setSong(song);
            Author mainComposer;
            AuthorAlias composerAlias;
            if (authorId.startsWith("NEW")) {
                //we will create new author with alias being equal to this author
                String newAuthor = authorId.replace("NEW-", "");
                mainComposer = new Author();
                mainComposer.setName(newAuthor);
                mainComposer = authorService.save(mainComposer);
                composerAlias = new AuthorAlias(mainComposer, newAuthor);
                composerAlias = authorAliasService.save(composerAlias);
            } else {
                //then we just have this author in the system already but maybe that is a new alias?
                Author author = authorService.findById(Integer.parseInt(authorId))
                        .orElseThrow(() -> new ResourceNotFoundException("No author with id found " + authorId));
                if (mainAliasId.startsWith("NEW")) {
                    //if so, then we create new alias in system
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias(author, newAlias);
                    composerAlias = authorAliasService.save(composerAlias);
                } else {
                    //otherwise we use existing alias
                    composerAlias = authorAliasService.findById(Integer.parseInt(mainAliasId))
                            .orElseThrow(() -> new ResourceNotFoundException("No alias found with id " + mainAliasId));
                }
            }
            //so we create author-song association, this is going to be composer
            AuthorSong authorSong = new AuthorSong(composerAlias, songSubgroup.getSong(), Role.COMPOSER);
            authorSongService.save(authorSong);
            //booleans help us determine if we need to create feat / subcomposer / remix
            boolean feat = Boolean.parseBoolean(objectMapper.get("feat"));
            boolean subcomposer = Boolean.parseBoolean(objectMapper.get("subcomposer"));
            boolean remix = Boolean.parseBoolean(objectMapper.get("remix"));
            if (subcomposer) {
                songSubgroupService.updateFeat(objectMapper, "subcomposerSelect", "subcomposerConcatInput",
                        songSubgroup, Role.SUBCOMPOSER, song, false);
            }
            if (feat) {
                songSubgroupService.updateFeat(objectMapper, "featSelect", "featConcatInput",
                        songSubgroup, Role.FEAT, song, false);
            }
            if (remix) {
                songSubgroupService.updateFeat(objectMapper, "remixSelect", "remixConcatInput",
                        songSubgroup, Role.REMIX, song, false);
            }
            //similarly as in other place, we will create new genre and make song-genre association
            List<String> comingGenres = objectMapper.keySet().stream().filter(
                    o -> o.contains("genreSelect")).toList();
            for (String comingGenre : comingGenres) {
                String genreValue = objectMapper.get(comingGenre);
                if (genreValue.startsWith("NEW")) {
                    String actualGenreValue = genreValue.replace("NEW-", "");
                    Genre genre = new Genre();
                    genre.setGenreName(actualGenreValue);
                    genre = genreService.save(genre);
                    SongGenre songGenre = new SongGenre(song, genre);
                    songGenreService.save(songGenre);
                } else {
                    songService.saveNewAssignmentOfExistingGenre(genreValue, songSubgroup.getSong());
                }
            }
            songSubgroupService.save(songSubgroup);
        }
        //again new song means we have to clean game cache
        String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
        removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * used to get other music links based on youtube id (we're calling service called Odesli.co)
     * used in songsMgmt.js, when you paste youtube link in ingame youtube link / official youtube link
     * it will automatically trigger this call
     *
     * @param youtubeId youtube id of video
     * @return json of music links
     * @throws IOException
     */
    @GetMapping(value = "/links/{songSubgroup}")
    public @ResponseBody
    String getLinksFromYoutubeId(@PathVariable("songSubgroup") String youtubeId)
            throws IOException {
        //todo show how the answer from odesli.co looks like
        StringBuilder content = new StringBuilder();
        URL url = new URL("https://odesli.co/embed?url=" + "https://www.youtube.com/watch?v=" + youtubeId);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line).append("\n");
        }
        bufferedReader.close();
        String valueToCheck = content.toString();
        SongSubgroup songSubgroup = new SongSubgroup();
        //we just do some stupif substring crap to get necessary links and build them properly
        int beginPositionITunes = valueToCheck.indexOf("https://geo.music.apple.com");
        if (beginPositionITunes > -1) {
            int endPosition = valueToCheck.indexOf("0026mt=1");
            String iTunesLink = valueToCheck.substring(beginPositionITunes, endPosition - 2);
            songSubgroup.setItunesLink(iTunesLink);
        }
        int beginPositionTidal = valueToCheck.indexOf("https://listen.tidal.com");
        if (beginPositionTidal > -1) {
            int endPosition = beginPositionTidal + 40;
            String tidalLink = valueToCheck.substring(beginPositionTidal, endPosition);
            tidalLink = tidalLink.replace("\"", "").replace("}", "").replace(",", "");
            songSubgroup.setTidalLink(tidalLink);
        }
        int beginPositionDeezer = valueToCheck.indexOf("www.deezer.com");
        if (beginPositionDeezer > -1) {
            int endPosition = beginPositionDeezer + 30;
            String deezerId = "deezer://" + valueToCheck.substring(beginPositionDeezer, endPosition);
            deezerId = deezerId.replace("\"", "").replace("}", "").replace(",", "");
            songSubgroup.setDeezerId(deezerId);
        }
        int beginPositionSoundcloud = valueToCheck.indexOf("\"https://soundcloud.com");
        if (beginPositionSoundcloud > -1) {
            int endPosition = valueToCheck.indexOf("\"", beginPositionSoundcloud + 1);
            String soundcloudLink = valueToCheck.substring(beginPositionSoundcloud + 1, endPosition);
            songSubgroup.setSoundcloudLink(soundcloudLink);
        }
        int beginPositionSpotify = valueToCheck.indexOf("https://open.spotify.com/track/");
        if (beginPositionSpotify > -1) {
            int endPosition = beginPositionSpotify + 53;
            String spotifyLink = "spotify:track:" + valueToCheck.substring(beginPositionSpotify + 31, endPosition);
            songSubgroup.setSpotifyId(spotifyLink);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(songSubgroup);
    }

}