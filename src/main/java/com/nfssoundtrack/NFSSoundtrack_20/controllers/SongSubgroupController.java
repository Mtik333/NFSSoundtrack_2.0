package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.deserializers.SongDeserializer;
import com.nfssoundtrack.NFSSoundtrack_20.deserializers.SongSubgroupDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/songSubgroup")
public class SongSubgroupController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(SongSubgroupController.class);

    @Autowired
    SongSubgroupDeserializer songSubgroupDeserializer;

    @Autowired
    SongDeserializer songDeserializer;

    @PutMapping(value = "/positions/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSubgroupPositions(@PathVariable("subgroupId") int subgroupId,
                                @RequestBody String formData) throws Exception {
        System.out.println("???");
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            long songSubgroupId = Long.parseLong(String.valueOf(linkedHashMap.get("songSubgroupId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            SongSubgroup songSubgroup =
                    songSubgroupService.findById(Math.toIntExact(songSubgroupId)).orElseThrow(() -> new Exception("no" +
                            " songsubgroup found with id " + subgroupId));
            songSubgroup.setPosition(position);
            songSubgroupService.save(songSubgroup);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/put/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String putSubgroup(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData)
            throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup = songSubgroupService.findById(subgroupId).orElseThrow(() -> new Exception(
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
            String mainAliasId = localObjectMapper.get("aliasId");
            String authorId = localObjectMapper.get("authorId");
            Song relatedSong = songSubgroup.getSong();
            Author mainComposer;
            AuthorAlias composerAlias;
            if (authorId.startsWith("NEW")) {
                String newAuthor = authorId.replace("NEW-", "");
                mainComposer = new Author();
                mainComposer.setName(newAuthor);
                mainComposer = authorService.save(mainComposer);
                composerAlias = new AuthorAlias(mainComposer, newAuthor);
            } else {
                Author author =
                        authorService.findById(Integer.parseInt(authorId)).orElseThrow(() -> new Exception("No author" +
                                " find id found " + authorId));
                if (mainAliasId.startsWith("NEW")) {
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias(author, newAlias);
                    composerAlias = authorAliasService.save(composerAlias);
                } else {
                    composerAlias = authorAliasService.findById(Integer.parseInt(mainAliasId)).orElseThrow(
                            () -> new Exception("No author" +
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
                        }
                    }
                }
            }
            if (subcomposer) {
                songSubgroupService.updateFeat(localObjectMapper, "subcomposerSelect",
                        "subcomposerConcatInput", songSubgroup, Role.SUBCOMPOSER, relatedSong);
            }
            if (feat) {
                songSubgroupService.updateFeat(localObjectMapper, "featSelect",
                        "featConcatInput", songSubgroup, Role.FEAT, relatedSong);
            }
            if (remix) {
                songSubgroupService.updateFeat(localObjectMapper, "remixSelect",
                        "remixConcatInput", songSubgroup, Role.REMIX, relatedSong);
            }
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
                        new Exception("No subgroup with id found " + songSubgroupId));
        return objectMapper.writeValueAsString(songSubgroup);
    }

    @DeleteMapping(value = "/delete/{subgroupId}")
    public @ResponseBody
    String deleteSongSubgroup(@PathVariable("subgroupId") int subgroupId) throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup =
                    songSubgroupService.findById(subgroupId).orElseThrow(() -> new Exception("No subgroup found " +
                            "with id " + subgroupId));
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
            SongSubgroup songSubgroup = songSubgroupService.findById(subgroupId).orElseThrow(() -> new Exception(
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
                String keyGenre = comingGenre;
                String genreValue = localObjectMapper.get(keyGenre);
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
                            .orElseThrow(() -> new Exception("No genre found with id " + deleteGenreId));
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
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Throwable thr) {
            return new ObjectMapper().writeValueAsString(thr);
        }
    }

    @PostMapping(value = "/post/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String postNewSong(@PathVariable("subgroupId") int subgroupId, @RequestBody String formData) throws Exception {
        Subgroup subgroup = subgroupService.findById(subgroupId).orElseThrow(() -> new Exception("No song " +
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
                        .orElseThrow(() -> new Exception("No author with id found " + authorId));
                if (mainAliasId.startsWith("NEW")) {
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias(author, newAlias);
                    composerAlias = authorAliasService.save(composerAlias);
                } else {
                    composerAlias = authorAliasService.findById(Integer.parseInt(mainAliasId))
                            .orElseThrow(() -> new Exception("No alias found with id " + mainAliasId));
                }
            }
            AuthorSong authorSong = new AuthorSong(composerAlias, songSubgroup.getSong(), Role.COMPOSER);
            authorSongService.save(authorSong);
            boolean feat = Boolean.parseBoolean(objectMapper.get("feat"));
            boolean subcomposer = Boolean.parseBoolean(objectMapper.get("subcomposer"));
            boolean remix = Boolean.parseBoolean(objectMapper.get("remix"));
            if (subcomposer) {
                songSubgroupService.updateFeat(objectMapper, "subcomposerSelect", "subcomposerConcatInput",
                        songSubgroup, Role.SUBCOMPOSER, song);
            }
            if (feat) {
                songSubgroupService.updateFeat(objectMapper, "featSelect", "featConcatInput",
                        songSubgroup, Role.FEAT, song);
            }
            if (remix) {
                songSubgroupService.updateFeat(objectMapper, "remixSelect", "remixConcatInput",
                        songSubgroup, Role.REMIX, song);
            }
            List<String> comingGenres = objectMapper.keySet().stream().filter(
                    o -> o.contains("genreSelect")).toList();
            for (String comingGenre : comingGenres) {
                String keyGenre = comingGenre;
                String genreValue = objectMapper.get(keyGenre);
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
        return new ObjectMapper().writeValueAsString("OK");

    }
}