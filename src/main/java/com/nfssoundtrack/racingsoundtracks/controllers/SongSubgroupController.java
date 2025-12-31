package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.deserializers.SongDeserializer;
import com.nfssoundtrack.racingsoundtracks.deserializers.SongSubgroupDeserializer;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.others.lyrics.Lyrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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
@RestController
@RequestMapping("/songSubgroup")
public class SongSubgroupController  {

    private static final Logger logger = LoggerFactory.getLogger(SongSubgroupController.class);

    private final SongSubgroupDeserializer songSubgroupDeserializer;
    private final SongDeserializer songDeserializer;
    private final BaseControllerWithErrorHandling baseController;

    public SongSubgroupController(SongSubgroupDeserializer songSubgroupDeserializer, SongDeserializer songDeserializer, BaseControllerWithErrorHandling baseController) {
        this.songSubgroupDeserializer = songSubgroupDeserializer;
        this.songDeserializer = songDeserializer;
        this.baseController = baseController;
    }

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
    public String putSubgroupPositions(@PathVariable("subgroupId") int subgroupId,
                                @RequestBody String formData) throws JsonProcessingException, ResourceNotFoundException {
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        SongSubgroup firstSongSubgroup = null;
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            long songSubgroupId = Long.parseLong(String.valueOf(linkedHashMap.get("songSubgroupId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            SongSubgroup songSubgroup =
                    baseController.getSongSubgroupService().findById(Math.toIntExact(songSubgroupId)).orElseThrow(() -> new ResourceNotFoundException("no" +
                            " songsubgroup found with id " + subgroupId));
            if (!position.equals(songSubgroup.getPosition()) && position % 10 != 0) {
                String message = "Set position of song " + songSubgroup.getSong().toAnotherChangeLogString()
                        + " in subgroup " + songSubgroup.getSubgroup().getSubgroupName()
                        + " in group " + songSubgroup.getSubgroup().getMainGroup().getGroupName()
                        + " in game " + songSubgroup.getSubgroup().getMainGroup().getGame().getDisplayTitle()
                        + " to " + position;
                baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "update", message,
                        EntityUrl.SONG, songSubgroup.getSong().toAnotherChangeLogString(), String.valueOf(songSubgroup.getSong().getId()));
            }
            //we just find the song-subgroup based on id and just change position
            songSubgroup.setPosition(position);
            baseController.getSongSubgroupService().save(songSubgroup);
            firstSongSubgroup = songSubgroup;
        }
        if (!objectMapper.isEmpty()) {
            if (firstSongSubgroup != null) {
                String gameShort = firstSongSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
                //we want to immediately update the game's page once songs order got changed
                baseController.removeCacheEntry(gameShort);
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
    public String putSongSubgroup(@PathVariable("songSubgroupId") int songSubgroupId, @RequestBody String formData)
            throws JsonProcessingException {
        try {
            //first let's find this song-subgroup
            SongSubgroup songSubgroup = baseController.getSongSubgroupService().findById(songSubgroupId).orElseThrow(() -> new ResourceNotFoundException(
                    "No songsubgroup found with id " + songSubgroupId));
            String message = "Updating song " + songSubgroup.getSong().toAnotherChangeLogString()
                    + " in subgroup " + songSubgroup.getSubgroup().getSubgroupName()
                    + " in group " + songSubgroup.getSubgroup().getMainGroup().getGroupName()
                    + " in game " + songSubgroup.getSubgroup().getMainGroup().getGame().getDisplayTitle();
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
                String localMessage = "Creating new author " + newAuthor;
                mainComposer = baseController.getAuthorService().save(mainComposer);
                baseController.sendMessageToChannel(EntityType.AUTHOR, "create", localMessage,
                        EntityUrl.AUTHOR, mainComposer.getName(), String.valueOf(mainComposer.getId()));
                composerAlias = new AuthorAlias(mainComposer, newAuthor);
                baseController.getAuthorAliasService().save(composerAlias);
            } else {
                //otherwise we look up the existing author
                Author author =
                        baseController.getAuthorService().findById(Integer.parseInt(authorId)).orElseThrow(() -> new ResourceNotFoundException("No author" +
                                " find id found " + authorId));
                //if there's a new alias of him we will create new alias obviously
                if (mainAliasId.startsWith("NEW")) {
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias(author, newAlias);
                    String localMessage = "Creating new alias " + newAlias + " of author " + author.getName();
                    composerAlias = baseController.getAuthorAliasService().save(composerAlias);
                    baseController.sendMessageToChannel(EntityType.AUTHOR_ALIAS, "create", localMessage,
                            EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
                } else {
                    composerAlias = baseController.getAuthorAliasService().findById(Integer.parseInt(mainAliasId)).orElseThrow(
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
                                String localMessage = "Associating author " + author.getName()
                                        + " by new alias " + composerAlias + " with song "
                                        + authorSong.getSong().toAnotherChangeLogString();
                                baseController.getAuthorSongService().save(authorSong);
                                baseController.sendMessageToChannel(EntityType.AUTHOR_SONG, "create", localMessage,
                                        EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
                            }
                        } else {
                            AuthorSong newAuthorSong = new AuthorSong(composerAlias, relatedSong, Role.COMPOSER);
                            String localMessage = "Associating author " + author.getName()
                                    + " by alias " + composerAlias + " with song "
                                    + authorSong.getSong().toAnotherChangeLogString();
                            baseController.getAuthorSongService().save(newAuthorSong);
                            baseController.sendMessageToChannel(EntityType.AUTHOR_SONG, "create", localMessage,
                                    EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
                            //case when we completely change the author
                            baseController.getAuthorSongService().delete(authorSong);
                        }
                    }
                }
            }
            //three below are supposed to update feat / subcomposer / remix roles in the song
            if (subcomposer) {
                updateSubcomposersFeat(localObjectMapper, "subcomposerSelect",
                        "subcomposerConcatInput", songSubgroup, Role.SUBCOMPOSER, relatedSong, propagate);
            }
            if (feat) {
                updateSubcomposersFeat(localObjectMapper, "featSelect",
                        "featConcatInput", songSubgroup, Role.FEAT, relatedSong, propagate);
            }
            if (remix) {
                updateSubcomposersFeat(localObjectMapper, "remixSelect",
                        "remixConcatInput", songSubgroup, Role.REMIX, relatedSong, propagate);
            }
            //here we will associate song (if is now considered as remix) with the 'main' song
            //this isn't used that much but maybe it would at some point in the future
            if (!remixOf.isEmpty()) {
                Integer existingSongId = Integer.parseInt(remixOf);
                Optional<Song> existingSong = baseController.getSongService().findById(existingSongId);
                if (existingSong.isPresent()) {
                    songSubgroup.getSong().setBaseSong(existingSong.get());
                    songSubgroup.setRemix(Remix.YES);
                }
            }
            //as song was updated, we will cleanup cache of game's entry too to refresh UI
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            baseController.removeCacheEntry(gameShort);
            baseController.getSongSubgroupService().save(songSubgroup);
            baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "update", message,
                    EntityUrl.SONG, songSubgroup.getSong().toAnotherChangeLogString(), String.valueOf(songSubgroup.getSong().getId()));
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
    public String readAllSubgroupManage(@PathVariable("songSubgroupId") int songSubgroupId)
            throws JsonProcessingException, ResourceNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        SongSubgroup songSubgroup =
                baseController.getSongSubgroupService().findById(songSubgroupId).orElseThrow(() ->
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
    public String deleteSongSubgroup(@PathVariable("songSubgroupId") int songSubgroupId) throws JsonProcessingException, ResourceNotFoundException {
        SongSubgroup songSubgroup =
                baseController.getSongSubgroupService().findById(songSubgroupId).orElseThrow(() -> new ResourceNotFoundException("No subgroup found " +
                        "with id " + songSubgroupId));
        String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
        //as the song gets deleted, we need to fetch cache key earlier
        Song song = songSubgroup.getSong();
        List<SongSubgroup> allSongSubgroupEntries = baseController.getSongSubgroupService().findBySong(song);
        if (allSongSubgroupEntries.size() == 1) {
            //means we basically have to delete song entirely to avoid orphans
            String message = "Deleting song " + song.toAnotherChangeLogString()
                    + " from database completely";
            List<SongGenre> songGenresToDelete = song.getSongGenreList();
            List<Genre> genresToDelete = new ArrayList<>();
            for (SongGenre songGenre : songGenresToDelete) {
                //so we do song cleanup: first we detect if song was the only one using specific genre
                Genre genre = songGenre.getGenre();
                List<SongGenre> allUsagesOfGenre = baseController.getSongGenreService().findByGenre(genre);
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
                List<AuthorSong> allUsagesOfAlias = baseController.getAuthorSongService().findByAuthorAlias(
                        authorSong.getAuthorAlias());
                //in case of single usage of this alias overall, means that we can also get rid of orphaned alias
                if (allUsagesOfAlias.size() == 1) {
                    authorAliasToDelete.add(authorSong.getAuthorAlias());
                    List<AuthorAlias> authorAliases = baseController.getAuthorAliasService().findByAuthor(author);
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
            baseController.getAuthorSongService().deleteAll(authorsOfSong);
            if (!authorsToDelete.isEmpty()) {
                for (Author authorToDelete : authorsToDelete) {
                    String localMessage = "Deleting author " + authorToDelete.getName()
                            + " from database completely";
                    baseController.sendMessageToChannel(EntityType.AUTHOR, "delete", localMessage,
                            EntityUrl.AUTHOR, authorToDelete.getName(), String.valueOf(authorToDelete.getId()));
                }
                //then, if that's the case, delete the author and his associations too
                baseController.getAuthorCountryService().deleteAll(authorCountriesToDelete);
                baseController.getAuthorAliasService().deleteAll(authorAliasToDelete);
                baseController.getAuthorService().deleteAll(authorsToDelete);
            }
            //now we can delete genre-song associations
            baseController.getSongGenreService().deleteAll(songGenresToDelete);
            if (!genresToDelete.isEmpty()) {
                for (Genre genre : genresToDelete) {
                    String localMessage = "Deleting genre " + genre.getGenreName()
                            + " from database completely";
                    baseController.sendMessageToChannel(EntityType.GENRE, "delete", localMessage,
                            EntityUrl.GENRE, genre.getGenreName(), String.valueOf(genre.getId()));
                }
                //and in case of orphaned entry we will delete genre too
                baseController.getGenreService().deleteAll(genresToDelete);
            }
            //if we remove song from subgroup but this specific one was used as today song
            //then either we link other usage of this song or just replace it with some other song
            JustSomeHelper.unlinkSongWithTodaysSong(baseController.getTodaysSongService(), songSubgroup, song, baseController.getSongSubgroupService());
            JustSomeHelper.unlinkSongWithCorrection(baseController.getCorrectionService(), songSubgroup,
                    "; deleted song-subgroup: " + songSubgroup.toCorrectionString());
            //finally we get rid of song-subgroup association and the song in the end
            baseController.getSongSubgroupService().delete(songSubgroup);
            baseController.getSongService().delete(song);
            baseController.sendMessageToChannel(EntityType.SONG, "delete", message,
                    EntityUrl.SONG, song.toAnotherChangeLogString(), String.valueOf(song.getId()));
        } else {
            String message = "Deleting song " + song.toAnotherChangeLogString()
                    + " from subgroup " + songSubgroup.getSubgroup().getSubgroupName()
                    + " from group " + songSubgroup.getSubgroup().getMainGroup().getGroupName()
                    + " from game " + songSubgroup.getSubgroup().getMainGroup().getGame().getDisplayTitle();
            //song used more than once in database, so we just make sure it is not associated with any today-song or correction
            JustSomeHelper.unlinkSongWithTodaysSong(baseController.getTodaysSongService(), songSubgroup, song, baseController.getSongSubgroupService());
            JustSomeHelper.unlinkSongWithCorrection(baseController.getCorrectionService(), songSubgroup,
                    "; deleted song-subgroup: " + songSubgroup.toCorrectionString());
            baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "delete", message,
                    EntityUrl.SONG, song.toAnotherChangeLogString(), String.valueOf(song.getId()));
            baseController.getSongSubgroupService().delete(songSubgroup);
        }
        baseController.removeCacheEntry(gameShort);
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
    public String putGlobally(@PathVariable("songSubgroupId") int songSubgroupId,
                       @RequestBody String formData) throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup = baseController.getSongSubgroupService().findById(songSubgroupId).orElseThrow(() -> new ResourceNotFoundException(
                    "No song subgroup with id found " + songSubgroupId));
            String message = "Updating official info of the song " + songSubgroup.getSong().toAnotherChangeLogString()
                    + " for all games";
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
                    String localMessage = "Creating new genre " + genre.getGenreName()
                            + " used for the song " + songSubgroup.getSong().toAnotherChangeLogString();
                    genre = baseController.getGenreService().save(genre);
                    //and we associate genre with song
                    SongGenre songGenre = new SongGenre(songSubgroup.getSong(), genre);
                    baseController.getSongGenreService().save(songGenre);
                    baseController.sendMessageToChannel(EntityType.GENRE, "create", localMessage,
                            EntityUrl.GENRE, genre.getGenreName(), String.valueOf(genre.getId()));
                } else if (genreValue.startsWith("DELETE")) {
                    //if delete is expected (clicked minus red button), we're going to remove genre association
                    String deleteGenreId = genreValue.replace("DELETE-", "");
                    Genre genre = baseController.getGenreService().findById(Integer.parseInt(deleteGenreId))
                            .orElseThrow(() -> new ResourceNotFoundException("No genre found with id " + deleteGenreId));
                    List<SongGenre> existingGenres = songSubgroup.getSong().getSongGenreList();
                    for (SongGenre songGenre : existingGenres) {
                        if (songGenre.getGenre().equals(genre)) {
                            String localMessage = "Unlinking genre " + genre.getGenreName()
                                    + " from the song " + songGenre.getSong().toAnotherChangeLogString();
                            baseController.getSongGenreService().delete(songGenre);
                            baseController.sendMessageToChannel(EntityType.GENRE, "create", localMessage,
                                    EntityUrl.GENRE, genre.getGenreName(), String.valueOf(genre.getId()));
                            break;
                        }
                    }
                } else {
                    Genre genre = baseController.getGenreService().findById(Integer.parseInt(genreValue)).orElseThrow(() -> new ResourceNotFoundException("No genre " +
                            "found with id " + genreValue));
                    //case when we just associate existing in database genre with this song for the first time
                    boolean alreadyAssigned=baseController.getSongService().saveNewAssignmentOfExistingGenre(genreValue, songSubgroup.getSong(), genre);
                    if (!alreadyAssigned){
                        String localMessage = "Linking genre " + genre.getGenreName()
                            + " to the song " + songSubgroup.getSong().toAnotherChangeLogString();
                        baseController.sendMessageToChannel(EntityType.GENRE, "create", localMessage,
                            EntityUrl.GENRE, genre.getGenreName(), String.valueOf(genre.getId()));
                    }
                }
            }
            //so now we can save genres
            relatedSong.setSongGenreList(baseController.getSongGenreService().findBySong(relatedSong));
            baseController.sendMessageToChannel(EntityType.SONG, "update", message,
                    EntityUrl.SONG, relatedSong.toAnotherChangeLogString(), String.valueOf(relatedSong.getId()));
            baseController.getSongService().save(relatedSong);
            //so for editing song globally, we always come from a subgroup entry so we also update related links
            //for both global song entry and this single local subgroup entry too
            if (Boolean.parseBoolean(localObjectMapper.get("propagate"))) {
                String localMessage = "Updating music services links for song "
                        + songSubgroup.getSong().toAnotherChangeLogString()
                        + " in game " + songSubgroup.getSubgroup().getMainGroup().getGame().getDisplayTitle();
                songSubgroup.setSrcId(relatedSong.getSrcId());
                songSubgroup.setSpotifyId(relatedSong.getSpotifyId());
                songSubgroup.setDeezerId(relatedSong.getDeezerId());
                songSubgroup.setItunesLink(relatedSong.getItunesLink());
                songSubgroup.setSoundcloudLink(relatedSong.getSoundcloudLink());
                songSubgroup.setTidalLink(relatedSong.getTidalLink());
                baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "update", localMessage,
                        EntityUrl.SONG, relatedSong.toAnotherChangeLogString(), String.valueOf(relatedSong.getId()));
                baseController.getSongSubgroupService().save(songSubgroup);
            } else if (Boolean.parseBoolean(localObjectMapper.get("propagateAll"))) {
                Game game = songSubgroup.getSubgroup().getMainGroup().getGame();
                String localMessage = "Updating music services links for song "
                        + songSubgroup.getSong().toAnotherChangeLogString() + " in all games";
                List<SongSubgroup> allSongSubgroups = baseController.getSongSubgroupService().findBySong(relatedSong);
                allSongSubgroups = allSongSubgroups.stream().filter(localSongSubgroup ->
                        localSongSubgroup.getSubgroup().getMainGroup().getGame().equals(game)).toList();
                for (SongSubgroup gameSongSubgroup : allSongSubgroups) {
                    gameSongSubgroup.setSrcId(relatedSong.getSrcId());
                    gameSongSubgroup.setSpotifyId(relatedSong.getSpotifyId());
                    gameSongSubgroup.setDeezerId(relatedSong.getDeezerId());
                    gameSongSubgroup.setItunesLink(relatedSong.getItunesLink());
                    gameSongSubgroup.setSoundcloudLink(relatedSong.getSoundcloudLink());
                    gameSongSubgroup.setTidalLink(relatedSong.getTidalLink());
                    baseController.getSongSubgroupService().save(gameSongSubgroup);
                }
                baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "update", localMessage,
                        EntityUrl.SONG, relatedSong.toAnotherChangeLogString(), String.valueOf(relatedSong.getId()));
            }
            //we updated song globally so we again clear game from which this edit comes
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            baseController.removeCacheEntry(gameShort);
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
    public String postNewSong(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData) throws ResourceNotFoundException, JsonProcessingException {
        Subgroup subgroup = baseController.getSubgroupService().findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No song " +
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
        String message;
        if (songSubgroup.getSong() != null) {
            message = "Adding song " + songSubgroup.getSong().toAnotherChangeLogString()
                    + " to subgroup " + subgroup.getSubgroupName()
                    + " to group " + subgroup.getMainGroup().getGroupName()
                    + " to game " + subgroup.getMainGroup().getGame().getDisplayTitle();
            //this is the case when we just use existing song in a new game (new subgroup)
            baseController.getSongSubgroupService().save(songSubgroup);
        } else {
            ObjectMapper songObjectMapper = JustSomeHelper.registerDeserializerForObjectMapper(Song.class, songDeserializer);
            //form data is so similar in song and song-subgroup that we can prepare song this way and save it to db
            Song song = songObjectMapper.readValue(formData, Song.class);
            message = "Creating song " + song.toAnotherChangeLogString()
                    + " to subgroup " + subgroup.getSubgroupName()
                    + " to group " + subgroup.getMainGroup().getGroupName()
                    + " to game " + subgroup.getMainGroup().getGame().getDisplayTitle();
            song = baseController.getSongService().save(song);
            //we can now associate song with song-subgroup
            songSubgroup.setSong(song);
            Author mainComposer;
            AuthorAlias composerAlias;
            if (authorId.startsWith("NEW")) {
                //we will create new author with alias being equal to this author
                String newAuthor = authorId.replace("NEW-", "");
                mainComposer = new Author();
                mainComposer.setName(newAuthor);
                String localMessage = "Creating new author " + newAuthor;
                mainComposer = baseController.getAuthorService().save(mainComposer);
                composerAlias = new AuthorAlias(mainComposer, newAuthor);
                composerAlias = baseController.getAuthorAliasService().save(composerAlias);
                baseController.sendMessageToChannel(EntityType.AUTHOR, "create", localMessage,
                        EntityUrl.AUTHOR, mainComposer.getName(), String.valueOf(mainComposer.getId()));
            } else {
                //then we just have this author in the system already but maybe that is a new alias?
                Author author = baseController.getAuthorService().findById(Integer.parseInt(authorId))
                        .orElseThrow(() -> new ResourceNotFoundException("No author with id found " + authorId));
                if (mainAliasId.startsWith("NEW")) {
                    //if so, then we create new alias in system
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias(author, newAlias);
                    String localMessage = "Creating new alias " + newAlias
                            + " for author " + author.getName();
                    composerAlias = baseController.getAuthorAliasService().save(composerAlias);
                    baseController.sendMessageToChannel(EntityType.AUTHOR_ALIAS, "create", localMessage,
                            EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
                } else {
                    //otherwise we use existing alias
                    composerAlias = baseController.getAuthorAliasService().findById(Integer.parseInt(mainAliasId))
                            .orElseThrow(() -> new ResourceNotFoundException("No alias found with id " + mainAliasId));
                }
            }
            //so we create author-song association, this is going to be composer
            AuthorSong authorSong = new AuthorSong(composerAlias, songSubgroup.getSong(), Role.COMPOSER);
            baseController.getAuthorSongService().save(authorSong);
            //booleans help us determine if we need to create feat / subcomposer / remix
            boolean feat = Boolean.parseBoolean(objectMapper.get("feat"));
            boolean subcomposer = Boolean.parseBoolean(objectMapper.get("subcomposer"));
            boolean remix = Boolean.parseBoolean(objectMapper.get("remix"));
            if (subcomposer) {
                updateSubcomposersFeat(objectMapper, "subcomposerSelect", "subcomposerConcatInput",
                        songSubgroup, Role.SUBCOMPOSER, song, false);
            }
            if (feat) {
                updateSubcomposersFeat(objectMapper, "featSelect", "featConcatInput",
                        songSubgroup, Role.FEAT, song, false);
            }
            if (remix) {
                updateSubcomposersFeat(objectMapper, "remixSelect", "remixConcatInput",
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
                    String localMessage = "Creating new genre " + genre.getGenreName()
                            + " and linking to song " + song.toAnotherChangeLogString();
                    genre = baseController.getGenreService().save(genre);
                    SongGenre songGenre = new SongGenre(song, genre);
                    baseController.getSongGenreService().save(songGenre);
                    baseController.sendMessageToChannel(EntityType.SONG_GENRE, "create", localMessage,
                            EntityUrl.GENRE, genre.getGenreName(), String.valueOf(genre.getId()));
                } else {
                    Genre genre = baseController.getGenreService().findById(Integer.parseInt(genreValue)).orElseThrow(() -> new ResourceNotFoundException("No genre " +
                            "found with id " + genreValue));
                    boolean alreadyAssigned=baseController.getSongService().saveNewAssignmentOfExistingGenre(genreValue, songSubgroup.getSong(), genre);
                    if (!alreadyAssigned){
                        String localMessage = "Linking existing genre " + genre.getGenreName()
                            + " to song " + song.toAnotherChangeLogString();
                        baseController.sendMessageToChannel(EntityType.SONG_GENRE, "create", localMessage,
                            EntityUrl.SONG, songSubgroup.getSong().toAnotherChangeLogString(), String.valueOf(songSubgroup.getSong().getId()));
                    }
                }
            }
            baseController.getSongSubgroupService().save(songSubgroup);
        }
        //again new song means we have to clean game cache
        String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
        baseController.removeCacheEntry(gameShort);
        baseController.sendMessageToChannel(EntityType.SONG_SUBGROUP, "create", message,
                EntityUrl.GAME, subgroup.getMainGroup().getGame().getDisplayTitle(),
                subgroup.getMainGroup().getGame().getGameShort());
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
    @GetMapping(value = "/linksYt/{youtubeId}")
    public String getLinksFromYoutubeId(@PathVariable("youtubeId") String youtubeId)
            throws IOException {
        //todo show how the answer from odesli.co looks like
        SongSubgroup songSubgroup = new SongSubgroup();
        setLinksFromOdesliCoUsingYt(youtubeId, songSubgroup, false);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(songSubgroup);
    }

    @GetMapping(value = "/linksSp/{spotifyId}")
    public String getLinksFromSpotifyId(@PathVariable("spotifyId") String spotifyId)
            throws IOException {
        //todo show how the answer from odesli.co looks like
        SongSubgroup songSubgroup = new SongSubgroup();
        setLinksFromOdesliCoUsingSpotify(spotifyId, songSubgroup, false);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(songSubgroup);
    }

    @GetMapping(value = "/copyLinks/{subgroupId}")
    public String copyLinksFromGlobalSongToSubgroupEntries(@PathVariable("subgroupId") int subgroupId)
            throws IOException, ResourceNotFoundException {
        Subgroup subgroup = baseController.getSubgroupService().findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No song " +
                "subgroup found with id " + subgroupId));
        //fetching all songs from subgroup
        List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
        for (SongSubgroup songSubgroup : songSubgroupList) {
            Song song = songSubgroup.getSong();
            //and just copy links from official song to this entry
            //this is because when you propagate links from official song entry
            //to local entry, it will only consider song from that subgroup you accessed global song from
            songSubgroup.setLinks(song);
            baseController.getSongSubgroupService().save(songSubgroup);
        }
        String gameShort = subgroup.getMainGroup().getGame().getGameShort();
        //as we updated links, it's worth also unloading game cache
        baseController.removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/removeNotes/{subgroupId}")
    public String removeNotesFromSongs(@PathVariable("subgroupId") int subgroupId)
            throws IOException, ResourceNotFoundException {
        Subgroup subgroup = baseController.getSubgroupService().findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No " +
                "subgroup found with id " + subgroupId));
        //fetching all songs from subgroup
        List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
        for (SongSubgroup songSubgroup : songSubgroupList) {
            //we clear all the notes from each song in a subgroup
            songSubgroup.setInfo(null);
            baseController.getSongSubgroupService().save(songSubgroup);
        }
        String gameShort = subgroup.getMainGroup().getGame().getGameShort();
        //it's worth also unloading game cache
        baseController.removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/getLyrics/{subgroupId}")
    public String getLyricsForSongs(@PathVariable("subgroupId") int subgroupId)
            throws IOException, ResourceNotFoundException {
        Subgroup subgroup = baseController.getSubgroupService().findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No " +
                "subgroup found with id " + subgroupId));
        //fetching all songs from subgroup
        List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
        for (SongSubgroup songSubgroup : songSubgroupList) {
            Song song = songSubgroup.getSong();
            logger.error("getting lyrics for: {}", song.toAnotherChangeLogString());
            if (song.getLyrics()!=null){
                logger.error("lyrics already there");
                continue;
            }
            Lyrics lyrics = JustSomeHelper.getLrcLibLyrics(song);
            if (lyrics==null){
                logger.error("no lyrics found");
                continue;
            }
            song.setLyrics(lyrics.getContent());
            //we clear all the notes from each song in a subgroup
            baseController.getSongService().save(song);
        }
        String gameShort = subgroup.getMainGroup().getGame().getGameShort();
        //it's worth also unloading game cache
        baseController.removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/obtainLinks/{subgroupId}")
    public String obtainMusicLinksFromOdesliCo(@PathVariable("subgroupId") int subgroupId)
            throws IOException, ResourceNotFoundException {
        Subgroup subgroup = baseController.getSubgroupService().findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No song " +
                "subgroup found with id " + subgroupId));
        //fetching all songs from subgroup
        List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
        for (SongSubgroup songSubgroup : songSubgroupList) {
            Song song = songSubgroup.getSong();
            //this time doing other way around - as i created method earlier
            //first we set links on subgroup, then on main song
            if (song.getSrcId() != null) {
                setLinksFromOdesliCoUsingYt(song.getSrcId(), songSubgroup, true);
            }
            if (song.getSpotifyId()!=null){
                setLinksFromOdesliCoUsingSpotify(song.getSpotifyId(), songSubgroup, true);
            }
            song.setLinks(songSubgroup);
            baseController.getSongSubgroupService().save(songSubgroup);
            baseController.getSongService().save(song);
        }
        String gameShort = subgroup.getMainGroup().getGame().getGameShort();
        //as we updated links, it's worth also unloading game cache
        baseController.removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/setMultiSongGenre/{subgroupId}")
    public String setGenresOnMultipleSongs(@RequestBody String formData,
                                    @PathVariable("subgroupId") int subgroupId)
            throws IOException, ResourceNotFoundException {
        Subgroup subgroup = baseController.getSubgroupService().findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No song " +
                "subgroup found with id " + subgroupId));
        LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) new ObjectMapper().readValue(formData, Map.class);
        int genreId = Integer.parseInt(String.valueOf(linkedHashMap.get("genreId")));
        Genre genre = baseController.getGenreService().findById(genreId).orElseThrow(() -> new ResourceNotFoundException("No genre " +
                "subgroup found with id " + genreId));
        List<SongSubgroup> songSubgroupList = subgroup.getSongSubgroupList();
        for (SongSubgroup songSubgroup : songSubgroupList) {
            Song song = songSubgroup.getSong();
            SongGenre existingSongGenre = baseController.getSongGenreService().findByGenreAndSong(genre, song);
            if (existingSongGenre == null) {
                SongGenre songGenre = new SongGenre();
                songGenre.setGenre(genre);
                songGenre.setSong(song);
                baseController.getSongGenreService().save(songGenre);
            }
        }
        String gameShort = subgroup.getMainGroup().getGame().getGameShort();
        //as we updated links, it's worth also unloading game cache
        baseController.removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");
    }

    private void setLinksFromOdesliCoUsingYt(String youtubeId, SongSubgroup songSubgroup, boolean setSong){
        setLinksFromOdesliCo(youtubeId,"https://www.youtube.com/watch?v=",songSubgroup,setSong);
    }

    private void setLinksFromOdesliCoUsingSpotify(String spotifyId, SongSubgroup songSubgroup, boolean setSong){
        setLinksFromOdesliCo(spotifyId, "", songSubgroup, setSong);
    }

    private void setLinksFromOdesliCo(String srcId, String urlParam, SongSubgroup songSubgroup, boolean setSong) {
        try {
            StringBuilder content = new StringBuilder();
            URL url = new URL("https://odesli.co/embed?url=" + urlParam + srcId);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
            String valueToCheck = content.toString();
            //we just do some stupid substring crap to get necessary links and build them properly
            int beginPositionITunes = valueToCheck.indexOf("https://geo.music.apple.com");
            if (beginPositionITunes > -1) {
                int endPosition = valueToCheck.indexOf("0026mt=1");
                String iTunesLink = valueToCheck.substring(beginPositionITunes, endPosition - 2) + "&at=11lJZP";
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
            if (setSong) {
                songSubgroup.getSong().setLinks(songSubgroup);
            }
        } catch (IOException e) {
            logger.error("unknown error, maybe API limit violated? {}", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * method used to update info about feat / subcomposer
     *
     * @param objectMapper      just ootb mapper
     * @param comingInput       type of role really
     * @param comingConcatInput text between composers of this role
     * @param songSubgroup      entity of song-subgroup
     * @param role              type of role used for specific song
     * @param relatedSong       the song itself for some reason
     * @param propagate         used to propagate concat between feat / remix to song-subgroup entry
     * @throws ResourceNotFoundException
     */
    private void updateSubcomposersFeat(Map<String, String> objectMapper, String comingInput, String comingConcatInput,
                                        SongSubgroup songSubgroup, Role role, Song relatedSong, Boolean propagate) throws ResourceNotFoundException {
        List<String> comingFeats = objectMapper.keySet().stream().filter(
                o -> o.contains(comingInput)).toList();
        Iterator<String> comingConcats = objectMapper.keySet().stream().filter(
                o -> o.contains(comingConcatInput)).toList().iterator();
        //so we have list of feat-artists and concat like & or , to render pretty display
        for (String comingFeat : comingFeats) {
            String concatVal = null;
            if (comingConcats.hasNext()) {
                concatVal = objectMapper.get(comingConcats.next());
            }
            String featValue = objectMapper.get(comingFeat);
            //we can either create totally new artist connected to song
            if (featValue.startsWith("NEW")) {
                //thing after minus is going to be name of this new artist
                String actualFeatValue = featValue.replace("NEW-", "");
                saveNewFeatOrRemixer(actualFeatValue, songSubgroup, role, concatVal, propagate);
            } else if (featValue.startsWith("DELETE")) {
                //or we want to remove association between author and song
                String deleteFeatId = featValue.replace("DELETE-", "");
                //here however after delete we have id of artist
                AuthorAlias authorAlias = baseController.getAuthorAliasService().findById(Integer.parseInt(deleteFeatId))
                        .orElseThrow(() -> new ResourceNotFoundException("No authoralias found with id " + deleteFeatId));
                AuthorSong authorSong = baseController.getAuthorSongService().findByAuthorAliasAndSong(authorAlias, relatedSong)
                        .orElseThrow(() -> new ResourceNotFoundException("No authorsong found"));
                String localMessage = "Unlinking " + role.value() + " artist " + authorAlias.getAuthor().getName()
                        + " from song " + authorSong.getSong().toAnotherChangeLogString();
                //given we found association, we can delete it and in case of remix - un-remix the field
                baseController.getAuthorSongService().delete(authorSong);
                baseController.sendMessageToChannel(EntityType.AUTHOR_SONG, "delete", localMessage,
                        EntityUrl.SONG, authorSong.getSong().toAnotherChangeLogString(), String.valueOf(authorSong.getSong().getId()));
                if (Role.REMIX.equals(role)) {
                    songSubgroup.setRemix(Remix.NO);
                }
            } else {
                //here we just add association between song and existing artist
                baseController.getSongSubgroupService().saveNewAssignmentOfExistingFeatRemixer(featValue, songSubgroup, role, concatVal, propagate);
            }
        }
    }

    /**
     * method to save new author and assign to the song under specific role
     *
     * @param actualRemixValue
     * @param songSubgroup
     * @param role
     * @param concatValue
     * @param propagate
     */
    private void saveNewFeatOrRemixer(String actualRemixValue, SongSubgroup songSubgroup, Role role,
                                      String concatValue, Boolean propagate) {
        Author author = new Author();
        author.setName(actualRemixValue);
        String localMessage = "Creating new author " + author.getName() + " in role " + role.value()
                + " to song " + songSubgroup.getSong().toAnotherChangeLogString();
        author = baseController.getAuthorService().save(author);
        AuthorAlias authorAlias = new AuthorAlias(author, actualRemixValue);
        authorAlias = baseController.getAuthorAliasService().save(authorAlias);
        //created author and alias
        AuthorSong authorSong = new AuthorSong(authorAlias, songSubgroup.getSong(), role);
        if (role.equals(Role.REMIX)) {
            baseController.getSongSubgroupService().propagateRemixToSongOccurrences(songSubgroup, propagate, authorSong);
        }
        baseController.getSongSubgroupService().setConcatsToAuthorSong(role, authorSong, concatValue);
        //for feat / subcomposer it is easier
        baseController.getAuthorSongService().save(authorSong);
        baseController.sendMessageToChannel(EntityType.AUTHOR_SONG, "create", localMessage,
                EntityUrl.SONG, songSubgroup.getSong().toAnotherChangeLogString(),
                String.valueOf(songSubgroup.getSong().getId()));
    }

}