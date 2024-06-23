package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.deserializers.SongDeserializer;
import com.nfssoundtrack.NFSSoundtrack_20.deserializers.SongSubgroupDeserializer;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@Controller
@RequestMapping("/songSubgroup")
public class SongSubgroupController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(SongSubgroupController.class);

    @Autowired
    SongSubgroupDeserializer songSubgroupDeserializer;

    @Autowired
    SongDeserializer songDeserializer;

    @Autowired
    CacheManager cacheManager;

    @PutMapping(value = "/positions/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSubgroupPositions(@PathVariable("subgroupId") int subgroupId,
                                @RequestBody String formData) throws Exception {
        System.out.println("???");
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        SongSubgroup firstSongSubgroup=null;
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            long songSubgroupId = Long.parseLong(String.valueOf(linkedHashMap.get("songSubgroupId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            SongSubgroup songSubgroup =
                    songSubgroupService.findById(Math.toIntExact(songSubgroupId)).orElseThrow(() -> new ResourceNotFoundException("no" +
                            " songsubgroup found with id " + subgroupId));
            songSubgroup.setPosition(position);
            songSubgroupService.save(songSubgroup);
            firstSongSubgroup = songSubgroup;
        }
        if (!objectMapper.isEmpty()){
            String gameShort = firstSongSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            removeCacheEntry(gameShort);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/put/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSubgroup(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData)
            throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup = songSubgroupService.findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException(
                    "No songsubgroup found with id " + subgroupId));
            SimpleModule module = new SimpleModule();
            ObjectMapper objectMapper = new ObjectMapper();
            module.addDeserializer(SongSubgroup.class, songSubgroupDeserializer);
            objectMapper.registerModule(module);
            songSubgroup = objectMapper.readerForUpdating(songSubgroup).readValue(formData, SongSubgroup.class);
            Map<String, String> localObjectMapper = new ObjectMapper().readValue(formData,
                    TypeFactory.defaultInstance().constructMapType(Map.class, String.class, String.class));
            boolean feat = Boolean.parseBoolean(localObjectMapper.get("feat"));
            boolean subcomposer = Boolean.parseBoolean(localObjectMapper.get("subcomposer"));
            boolean remix = Boolean.parseBoolean(localObjectMapper.get("remix"));
            boolean propagate = Boolean.parseBoolean(localObjectMapper.get("propagate"));
            String remixOf = localObjectMapper.get("remixOf");
            String mainAliasId = localObjectMapper.get("aliasId");
            String authorId = localObjectMapper.get("authorId");
            Song relatedSong = songSubgroup.getSong();
            Author mainComposer;
            AuthorAlias composerAlias;
            if (authorId != null && authorId.startsWith("NEW")) {
                String newAuthor = authorId.replace("NEW-", "");
                mainComposer = new Author();
                mainComposer.setName(newAuthor);
                mainComposer = authorService.save(mainComposer);
                composerAlias = new AuthorAlias(mainComposer, newAuthor);
                authorAliasService.save(composerAlias);
            } else {
                Author author =
                        authorService.findById(Integer.parseInt(authorId)).orElseThrow(() -> new ResourceNotFoundException("No author" +
                                " find id found " + authorId));
                if (mainAliasId.startsWith("NEW")) {
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias(author, newAlias);
                    composerAlias = authorAliasService.save(composerAlias);
                } else {
                    composerAlias = authorAliasService.findById(Integer.parseInt(mainAliasId)).orElseThrow(
                            () -> new ResourceNotFoundException("No author" +
                                    " find id found " + mainAliasId));
                }
                List<AuthorSong> authorSongList = relatedSong.getAuthorSongList();
                for (AuthorSong authorSong : authorSongList) {
                    if (Role.COMPOSER.equals(authorSong.getRole())) {
                        Author persistedAuthor = authorSong.getAuthorAlias().getAuthor();
                        if (persistedAuthor.equals(author)) {
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
            if (!remixOf.isEmpty()) {
                Integer existingSongId = Integer.parseInt(remixOf);
                Optional<Song> existingSong = songService.findById(existingSongId);
                if (existingSong.isPresent()) {
                    songSubgroup.getSong().setBaseSong(existingSong.get());
                    songSubgroup.setRemix(Remix.YES);
                }
            }
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            removeCacheEntry(gameShort);
            songSubgroupService.save(songSubgroup);
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Throwable thr) {
            return new ObjectMapper().writeValueAsString(thr);
        }
    }

    @GetMapping(value = "/read/{songSubgroup}")
    public @ResponseBody
    String readAllSubgroupManage(@PathVariable("songSubgroup") int songSubgroupId)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SongSubgroup songSubgroup =
                songSubgroupService.findById(songSubgroupId).orElseThrow(() ->
                        new ResourceNotFoundException("No subgroup with id found " + songSubgroupId));
        return objectMapper.writeValueAsString(songSubgroup);
    }

    @DeleteMapping(value = "/delete/{subgroupId}")
    public @ResponseBody
    String deleteSongSubgroup(@PathVariable("subgroupId") int subgroupId) throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup =
                    songSubgroupService.findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No subgroup found " +
                            "with id " + subgroupId));
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            removeCacheEntry(gameShort);
            Song song = songSubgroup.getSong();
            List<SongSubgroup> allSongSubgroupEntries = songSubgroupService.findBySong(song);
            if (allSongSubgroupEntries.size() == 1) {
                //means we basically have to delete song entirely to avoid orphans
                List<SongGenre> songGenresToDelete = song.getSongGenreList();
                List<Genre> genresToDelete = new ArrayList<>();
                for (SongGenre songGenre : songGenresToDelete) {
                    Genre genre = songGenre.getGenre();
                    List<SongGenre> allUsagesOfGenre = songGenreService.findByGenre(genre);
                    if (allUsagesOfGenre.size() == 1) {
                        genresToDelete.add(genre);
                    }
                }
                List<AuthorSong> authorsOfSong = song.getAuthorSongList();
                List<AuthorAlias> authorAliasToDelete = new ArrayList<>();
                List<Author> authorsToDelete = new ArrayList<>();
                List<AuthorCountry> authorCountriesToDelete = new ArrayList<>();
                for (AuthorSong authorSong : authorsOfSong) {
                    Author author = authorSong.getAuthorAlias().getAuthor();
                    List<AuthorSong> allUsagesOfAlias = authorSongService.findByAuthorAlias(
                            authorSong.getAuthorAlias());
                    if (allUsagesOfAlias.size() == 1) {
                        authorAliasToDelete.add(authorSong.getAuthorAlias());
                        List<AuthorAlias> authorAliases = authorAliasService.findByAuthor(author);
                        if (authorAliases.size() == 1) {
                            authorsToDelete.add(author);
                            authorCountriesToDelete.addAll(author.getAuthorCountries());
                        }
                    }
                }
                authorSongService.deleteAll(authorsOfSong);
                if (!authorsToDelete.isEmpty()) {
                    authorCountryService.deleteAll(authorCountriesToDelete);
                    authorAliasService.deleteAll(authorAliasToDelete);
                    authorService.deleteAll(authorsToDelete);
                }
                songGenreService.deleteAll(songGenresToDelete);
                if (!genresToDelete.isEmpty()) {
                    genreService.deleteAll(genresToDelete);
                }
                songSubgroupService.delete(songSubgroup);
                songService.delete(song);
            } else {
                List<Correction> relatedCorrections = correctionService.findBySongSubgroup(songSubgroup);
                for (Correction correction : relatedCorrections) {
                    correction.setSongSubgroup(null);
                    correction.setCorrectValue(correction.getCorrectValue()
                            + "; deleted song-subgroup: " + songSubgroup.getId());
                    correctionService.save(correction);
                }
                songSubgroupService.delete(songSubgroup);
            }
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Throwable thr) {
            return new ObjectMapper().writeValueAsString(thr);
        }
    }

    @PutMapping(value = "/putGlobally/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putGlobally(@PathVariable("subgroupId") int subgroupId,
                       @RequestBody String formData) throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup = songSubgroupService.findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException(
                    "No song subgroup with id found " + subgroupId));
            Song relatedSong = songSubgroup.getSong();
            SimpleModule module = new SimpleModule();
            ObjectMapper objectMapper = new ObjectMapper();
            module.addDeserializer(Song.class, songDeserializer);
            objectMapper.registerModule(module);
            relatedSong = objectMapper.readerForUpdating(relatedSong).readValue(formData, Song.class);
            Map<String, String> localObjectMapper = new ObjectMapper().readValue(formData,
                    TypeFactory.defaultInstance().constructMapType(Map.class, String.class, String.class));
            List<String> comingGenres = localObjectMapper.keySet().stream().filter(
                    o -> o.contains("genreSelect")).toList();
            for (String comingGenre : comingGenres) {
                String genreValue = localObjectMapper.get(comingGenre);
                if (genreValue.startsWith("NEW")) {
                    String actualGenreValue = genreValue.replace("NEW-", "");
                    Genre genre = new Genre();
                    genre.setGenreName(actualGenreValue);
                    genre = genreService.save(genre);
                    SongGenre songGenre = new SongGenre(songSubgroup.getSong(), genre);
                    songGenreService.save(songGenre);
                } else if (genreValue.startsWith("DELETE")) {
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
                    songService.saveNewAssignmentOfExistingGenre(genreValue, songSubgroup.getSong());
                }
            }
            songService.save(relatedSong);
            if (Boolean.parseBoolean(localObjectMapper.get("propagate"))) {
                songSubgroup.setSrcId(relatedSong.getSrcId());
                songSubgroup.setSpotifyId(relatedSong.getSpotifyId());
                songSubgroup.setDeezerId(relatedSong.getDeezerId());
                songSubgroup.setItunesLink(relatedSong.getItunesLink());
                songSubgroupService.save(songSubgroup);
            }
            String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
            removeCacheEntry(gameShort);
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Throwable thr) {
            return new ObjectMapper().writeValueAsString(thr);
        }
    }

    @PostMapping(value = "/post/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String postNewSong(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData) throws Exception {
        Subgroup subgroup = subgroupService.findById(subgroupId).orElseThrow(() -> new ResourceNotFoundException("No song " +
                "subgroup found with id " + subgroupId));
        ObjectMapper songSubgroupObjectMapper = new ObjectMapper();
        SimpleModule subgroupModule = new SimpleModule();
        subgroupModule.addDeserializer(SongSubgroup.class, songSubgroupDeserializer);
        songSubgroupObjectMapper.registerModule(subgroupModule);
        SongSubgroup songSubgroup = songSubgroupObjectMapper.readValue(formData, SongSubgroup.class);
        songSubgroup.setSubgroup(subgroup);
        Map<String, String> objectMapper = new ObjectMapper().readValue(formData,
                TypeFactory.defaultInstance().constructMapType(Map.class, String.class, String.class));
        String mainAliasId = objectMapper.get("aliasId");
        String authorId = objectMapper.get("authorId");
        if (songSubgroup.getSong() != null) {
            songSubgroupService.save(songSubgroup);
        } else {
            ObjectMapper songObjectMapper = new ObjectMapper();
            SimpleModule songModule = new SimpleModule();
            songModule.addDeserializer(Song.class, songDeserializer);
            songObjectMapper.registerModule(songModule);
            Song song = songObjectMapper.readValue(formData, Song.class);
            song = songService.save(song);
            songSubgroup.setSong(song);
            Author mainComposer;
            AuthorAlias composerAlias;
            if (authorId.startsWith("NEW")) {
                String newAuthor = authorId.replace("NEW-", "");
                mainComposer = new Author();
                mainComposer.setName(newAuthor);
                mainComposer = authorService.save(mainComposer);
                composerAlias = new AuthorAlias(mainComposer, newAuthor);
                composerAlias = authorAliasService.save(composerAlias);
            } else {
                Author author = authorService.findById(Integer.parseInt(authorId))
                        .orElseThrow(() -> new ResourceNotFoundException("No author with id found " + authorId));
                if (mainAliasId.startsWith("NEW")) {
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias(author, newAlias);
                    composerAlias = authorAliasService.save(composerAlias);
                } else {
                    composerAlias = authorAliasService.findById(Integer.parseInt(mainAliasId))
                            .orElseThrow(() -> new ResourceNotFoundException("No alias found with id " + mainAliasId));
                }
            }
            AuthorSong authorSong = new AuthorSong(composerAlias, songSubgroup.getSong(), Role.COMPOSER);
            authorSongService.save(authorSong);
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
        String gameShort = songSubgroup.getSubgroup().getMainGroup().getGame().getGameShort();
        removeCacheEntry(gameShort);
        return new ObjectMapper().writeValueAsString("OK");

    }

    @GetMapping(value = "/links/{songSubgroup}")
    public @ResponseBody
    String getLinksFromYoutubeId(@PathVariable("songSubgroup") String youtubeId)
            throws Exception {
        StringBuilder content = new StringBuilder();
        URL url = new URL("https://odesli.co/embed?url="+"https://www.youtube.com/watch?v="+youtubeId);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line).append("\n");
        }
        bufferedReader.close();
        String valueToCheck = content.toString();
        SongSubgroup songSubgroup = new SongSubgroup();
        int beginPositionITunes = valueToCheck.indexOf("https://geo.music.apple.com");
        if (beginPositionITunes>-1) {
            int endPosition = valueToCheck.indexOf("0026mt=1");
            String iTunesLink = valueToCheck.substring(beginPositionITunes, endPosition - 2);
            songSubgroup.setItunesLink(iTunesLink);
        }
        int beginPositionTidal = valueToCheck.indexOf("https://listen.tidal.com");
        if (beginPositionTidal>-1) {
            int endPosition = beginPositionTidal + 40;
            String tidalLink = valueToCheck.substring(beginPositionTidal, endPosition);
            tidalLink = tidalLink.replace("\"","").replace("}","").replace(",","");
            songSubgroup.setTidalLink(tidalLink);
        }
        int beginPositionDeezer = valueToCheck.indexOf("www.deezer.com");
        if (beginPositionDeezer>-1) {
            int endPosition = beginPositionDeezer + 30;
            String deezerId = "deezer://" + valueToCheck.substring(beginPositionDeezer, endPosition);
            deezerId = deezerId.replace("\"","").replace("}","").replace(",","");
            songSubgroup.setDeezerId(deezerId);
        }
        int beginPositionSoundcloud = valueToCheck.indexOf("\"https://soundcloud.com");
        if (beginPositionSoundcloud>-1) {
            int endPosition = valueToCheck.indexOf("\"", beginPositionSoundcloud+1);
            String soundcloudLink = valueToCheck.substring(beginPositionSoundcloud+1, endPosition);
            songSubgroup.setSoundcloudLink(soundcloudLink);
        }
        int beginPositionSpotify = valueToCheck.indexOf("https://open.spotify.com/track/");
        if (beginPositionSpotify>-1) {
            int endPosition = beginPositionSpotify + 46;
            String spotifyLink = "spotify:track:"+valueToCheck.substring(beginPositionSpotify+31, endPosition);
            songSubgroup.setSpotifyId(spotifyLink);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(songSubgroup);
    }

    private void removeCacheEntry(String gameShort){
        Cache cache = cacheManager.getCache("findByGameShort");
        if (cache!=null){
            cache.evict(gameShort);
        }
    }
}