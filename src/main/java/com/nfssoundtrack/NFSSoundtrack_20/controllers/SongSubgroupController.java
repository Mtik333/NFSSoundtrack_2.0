package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/songSubgroup")
public class SongSubgroupController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(SongSubgroupController.class);
    @Autowired
    private SubgroupRepository subgroupRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorAliasRepository authorAliasRepository;

    @Autowired
    private AuthorSongRepository authorSongRepository;

    @Autowired
    private SongGenreRepository songGenreRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private AuthorCountryRepository authorCountryRepository;

    @Autowired
    private SongRepository songRepository;

    @PutMapping(value = "/positions/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putSubgroupPositions(@PathVariable("subgroupId") String subgroupId, @RequestBody String formData) throws JsonProcessingException {
        System.out.println("???");
        Subgroup subgroup = subgroupRepository.getReferenceById(Integer.valueOf(subgroupId));
        List<?> objectMapper = new ObjectMapper().readValue(formData, List.class);
        for (Object obj : objectMapper) {
            LinkedHashMap<?, ?> linkedHashMap = (LinkedHashMap<?, ?>) obj;
            Long songSubgroupId = Long.parseLong(String.valueOf(linkedHashMap.get("songSubgroupId")));
            Long position = Long.parseLong(String.valueOf(linkedHashMap.get("position")));
            SongSubgroup songSubgroup = songSubgroupRepository.getReferenceById(Math.toIntExact(songSubgroupId));
            songSubgroup.setPosition(position);
            songSubgroupRepository.save(songSubgroup);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/put/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putSubgroup(@PathVariable("subgroupId") String subgroupId, @RequestBody String formData) throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup = songSubgroupRepository.findById(Integer.valueOf(subgroupId)).get();
            Map<?, ?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
            String spotifyLink = (String) objectMapper.get("spotify");
            String itunesLink = (String) objectMapper.get("itunes");
            String soundcloudLink = (String) objectMapper.get("soundcloud");
            String deezerLink = (String) objectMapper.get("deezer");
            String tidalink = (String) objectMapper.get("tidal");
            String ingameBand = (String) objectMapper.get("ingameBand");
            String ingameTitle = (String) objectMapper.get("ingameTitle");
            String ingameSrcId = (String) objectMapper.get("ingameSrcId");
            String lyrics = (String) objectMapper.get("lyrics");
            String info = (String) objectMapper.get("info");
            boolean instrumental = (boolean) objectMapper.get("instrumental");
            boolean feat = (boolean) objectMapper.get("feat");
            boolean remix = (boolean) objectMapper.get("remix");
            boolean subcomposer = (boolean) objectMapper.get("subcomposer");
            String mainAliasId = (String) objectMapper.get("aliasId");
            String authorId = (String) objectMapper.get("authorId");
            Song relatedSong = songSubgroup.getSong();
            Author mainComposer;
            AuthorAlias composerAlias;
            if (authorId.startsWith("NEW")) {
                String newAuthor = authorId.replace("NEW-", "");
                mainComposer = new Author();
                mainComposer.setName(newAuthor);
                mainComposer = authorRepository.save(mainComposer);
                composerAlias = new AuthorAlias();
                composerAlias.setAuthor(mainComposer);
                composerAlias.setAlias(newAuthor);
            } else {
                Author author = authorRepository.findById(Integer.valueOf(authorId)).get();
                if (mainAliasId.startsWith("NEW")) {
                    String newAlias = mainAliasId.replace("NEW-", "");
                    composerAlias = new AuthorAlias();
                    composerAlias.setAlias(newAlias);
                    composerAlias.setAuthor(author);
                    composerAlias = authorAliasRepository.save(composerAlias);
                } else {
                    composerAlias = authorAliasRepository.findById(Integer.valueOf(mainAliasId)).get();
                }
                List<AuthorSong> authorSongList = relatedSong.getAuthorSongList();
                for (AuthorSong authorSong : authorSongList) {
                    if (Role.COMPOSER.equals(authorSong.getRole())) {
                        Author persistedAuthor = authorSong.getAuthorAlias().getAuthor();
                        if (persistedAuthor.equals(author)) {
                            AuthorAlias persistedAuthorAlias = authorSong.getAuthorAlias();
                            if (!persistedAuthorAlias.equals(composerAlias)) {
                                authorSong.setAuthorAlias(composerAlias);
                                authorSongRepository.save(authorSong);
                            }
                        } else {
                            AuthorSong newAuthorSong = new AuthorSong();
                            newAuthorSong.setAuthorAlias(composerAlias);
                            newAuthorSong.setRole(Role.COMPOSER);
                            newAuthorSong.setSong(relatedSong);
                            authorSongRepository.save(newAuthorSong);
                        }
                    }
                }
            }
            songSubgroup.setInstrumental(Instrumental.fromBoolean(instrumental));
            songSubgroup.setRemix(Remix.fromBoolean(remix));
            songSubgroup.setIngameDisplayBand(returnValueToSet(ingameBand));
            songSubgroup.setIngameDisplayTitle(returnValueToSet(ingameTitle));
            songSubgroup.setSpotifyId(returnValueToSet(spotifyLink));
            songSubgroup.setItunesLink(returnValueToSet(itunesLink));
            songSubgroup.setSoundcloudLink(returnValueToSet(soundcloudLink));
            songSubgroup.setDeezerId(returnValueToSet(deezerLink));
            songSubgroup.setTidalLink(returnValueToSet(tidalink));
            songSubgroup.setSrcId(returnValueToSet(ingameSrcId));
            songSubgroup.setInfo(returnValueToSet(info));
            if (subcomposer) {
                List<String> comingSubcomposers = (List<String>) objectMapper.keySet().stream().filter(o -> o.toString().contains("subcomposerSelect")).collect(Collectors.toList());
                Iterator<String> comingConcats = (Iterator<String>) objectMapper.keySet().stream().filter(o -> o.toString().contains("subcomposerConcatInput")).collect(Collectors.toList()).iterator();
                for (String comingFeat : comingSubcomposers) {
                    String concatVal = null;
                    if (comingConcats.hasNext()) {
                        concatVal = (String) objectMapper.get(comingConcats.next());
                    }
                    String keySubcomposer = comingFeat;
                    String subcomposerValue = (String) objectMapper.get(keySubcomposer);
                    if (subcomposerValue.startsWith("NEW")) {
                        String actualSubcomposerValue = subcomposerValue.replace("NEW-", "");
                        saveNewFeatOrRemixer(actualSubcomposerValue, songSubgroup, Role.SUBCOMPOSER, concatVal);
                    } else if (subcomposerValue.startsWith("DELETE")) {
                        String deleteFeatId = subcomposerValue.replace("DELETE-", "");
                        AuthorAlias authorAlias = authorAliasRepository.findById(Integer.valueOf(deleteFeatId)).get();
                        AuthorSong authorSong = authorSongRepository.findByAuthorAliasAndSong(authorAlias, relatedSong);
                        authorSongRepository.delete(authorSong);
                    } else {
                        saveNewAssignmentOfExistingFeatRemixer(subcomposerValue, songSubgroup, Role.SUBCOMPOSER, concatVal);
                    }
                }
            }
            if (feat) {
                List<String> comingFeats = (List<String>) objectMapper.keySet().stream().filter(o -> o.toString().contains("featSelect")).collect(Collectors.toList());
                Iterator<String> comingConcats = (Iterator<String>) objectMapper.keySet().stream().filter(o -> o.toString().contains("featConcatInput")).collect(Collectors.toList()).iterator();
                for (String comingFeat : comingFeats) {
                    String concatVal = null;
                    if (comingConcats.hasNext()) {
                        concatVal = (String) objectMapper.get(comingConcats.next());
                    }
                    String keyFeat = comingFeat;
                    String featValue = (String) objectMapper.get(keyFeat);
                    if (featValue.startsWith("NEW")) {
                        String actualFeatValue = featValue.replace("NEW-", "");
                        saveNewFeatOrRemixer(actualFeatValue, songSubgroup, Role.FEAT, concatVal);
                    } else if (featValue.startsWith("DELETE")) {
                        String deleteFeatId = featValue.replace("DELETE-", "");
                        AuthorAlias authorAlias = authorAliasRepository.findById(Integer.valueOf(deleteFeatId)).get();
                        AuthorSong authorSong = authorSongRepository.findByAuthorAliasAndSong(authorAlias, relatedSong);
                        authorSongRepository.delete(authorSong);
                    } else {
                        saveNewAssignmentOfExistingFeatRemixer(featValue, songSubgroup, Role.FEAT, concatVal);
                    }
                }
            }
            if (remix) {
                List<String> comingRemixes = (List<String>) objectMapper.keySet().stream().filter(o -> o.toString().contains("remixSelect")).collect(Collectors.toList());
                Iterator<String> comingConcats = (Iterator<String>) objectMapper.keySet().stream().filter(o -> o.toString().contains("remixConcatInput")).collect(Collectors.toList()).iterator();
                for (String comingRemix : comingRemixes) {
                    String concatVal = null;
                    if (comingConcats.hasNext()) {
                        concatVal = (String) objectMapper.get(comingConcats.next());
                    }
                    String keyRemix = comingRemix;
                    String remixValue = (String) objectMapper.get(keyRemix);
                    if (remixValue.startsWith("NEW")) {
                        String actualRemixValue = remixValue.replace("NEW-", "");
                        saveNewFeatOrRemixer(actualRemixValue, songSubgroup, Role.REMIX, concatVal);
                    } else if (remixValue.startsWith("DELETE")) {
                        String deleteRemixId = remixValue.replace("DELETE-", "");
                        AuthorAlias authorAlias = authorAliasRepository.findById(Integer.valueOf(deleteRemixId)).get();
                        AuthorSong authorSong = authorSongRepository.findByAuthorAliasAndSong(authorAlias, relatedSong);
                        authorSongRepository.delete(authorSong);
                    } else {
                        saveNewAssignmentOfExistingFeatRemixer(remixValue, songSubgroup, Role.REMIX, concatVal);
                    }
                }
            }
            songSubgroup.setLyrics(lyrics);
            songSubgroupRepository.save(songSubgroup);
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Throwable thr) {
            return new ObjectMapper().writeValueAsString(thr);
        }
    }

    @GetMapping(value = "/read/{songSubgroup}")
    public @ResponseBody String readAllSubgroupManage(Model model, @PathVariable("songSubgroup") String gameId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SongSubgroup songSubgroup = songSubgroupRepository.findById(Integer.valueOf(gameId)).get();
        return objectMapper.writeValueAsString(songSubgroup);
    }

    private void saveNewAssignmentOfExistingFeatRemixer(String remixValue, SongSubgroup songSubgroup, Role role, String concatValue) {
        AuthorAlias authorAlias = authorAliasRepository.findById(Integer.valueOf(remixValue)).get();
        boolean alreadyAssigned = false;
        for (AuthorSong authorSong : songSubgroup.getSong().getAuthorSongList()) {
            if (authorSong.getAuthorAlias().equals(authorAlias)) {
                alreadyAssigned = true;
                break;
            }
        }
        if (!alreadyAssigned) {
            AuthorSong authorSong = new AuthorSong();
            authorSong.setSong(songSubgroup.getSong());
            authorSong.setAuthorAlias(authorAlias);
            authorSong.setRole(role);
            if (role.equals(Role.REMIX)) {
                authorSong.setRemixConcat(concatValue);
            }
            if (role.equals(Role.FEAT)) {
                authorSong.setFeatConcat(concatValue);
            }
            if (role.equals(Role.SUBCOMPOSER)) {
                authorSong.setSubcomposerConcat(concatValue);
            }
            authorSongRepository.save(authorSong);
        }
    }

    private void saveNewFeatOrRemixer(String actualRemixValue, SongSubgroup songSubgroup, Role role, String concatValue) {
        Author author = new Author();
        author.setName(actualRemixValue);
        author = authorRepository.save(author);
        AuthorAlias authorAlias = new AuthorAlias();
        authorAlias.setAuthor(author);
        authorAlias.setAlias(actualRemixValue);
        authorAlias = authorAliasRepository.save(authorAlias);
        AuthorSong authorSong = new AuthorSong();
        authorSong.setSong(songSubgroup.getSong());
        authorSong.setAuthorAlias(authorAlias);
        authorSong.setRole(role);
        if (role.equals(Role.REMIX)) {
            authorSong.setRemixConcat(concatValue);
        }
        if (role.equals(Role.FEAT)) {
            authorSong.setFeatConcat(concatValue);
        }
        if (role.equals(Role.SUBCOMPOSER)) {
            authorSong.setSubcomposerConcat(concatValue);
        }
        authorSongRepository.save(authorSong);
    }

    @DeleteMapping(value = "/delete/{subgroupId}")
    public @ResponseBody String deleteSongSubgroup(@PathVariable("subgroupId") String subgroupId) throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup = songSubgroupRepository.findById(Integer.valueOf(subgroupId)).get();
            Song song = songSubgroup.getSong();
            List<SongSubgroup> allSongSubgroupEntries = songSubgroupRepository.findBySong(song);
            if (allSongSubgroupEntries.size() == 1) {
                //means we basically have to delete song entirely to avoid orphans
                List<SongGenre> songGenresToDelete = song.getSongGenreList();
                List<Genre> genresToDelete = new ArrayList<>();
                for (SongGenre songGenre : songGenresToDelete) {
                    Genre genre = songGenre.getGenre();
                    List<SongGenre> allUsagesOfGenre = songGenreRepository.findByGenre(genre);
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
                    List<AuthorSong> allUsagesOfAlias = authorSongRepository.findByAuthorAlias(authorSong.getAuthorAlias());
                    if (allUsagesOfAlias.size() == 1) {
                        authorAliasToDelete.add(authorSong.getAuthorAlias());
                        List<AuthorAlias> authorAliases = authorAliasRepository.findByAuthor(author);
                        if (authorAliases.size() == 1) {
                            authorsToDelete.add(author);
                            authorCountriesToDelete.addAll(author.getAuthorCountries());
                        }
                    }
                }
                authorSongRepository.deleteAll(authorsOfSong);
                if (!authorsToDelete.isEmpty()) {
                    authorCountryRepository.deleteAll(authorCountriesToDelete);
                    authorAliasRepository.deleteAll(authorAliasToDelete);
                    authorRepository.deleteAll(authorsToDelete);
                }
                songGenreRepository.deleteAllInBatch(songGenresToDelete);
                if (!genresToDelete.isEmpty()) {
                    genreRepository.deleteAllInBatch(genresToDelete);
                }
                songSubgroupRepository.delete(songSubgroup);
                songRepository.delete(song);
            } else {
                songSubgroupRepository.delete(songSubgroup);
            }
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Throwable thr) {
            return new ObjectMapper().writeValueAsString(thr);
        }
    }

    @PutMapping(value = "/putGlobally/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String putGlobally(@PathVariable("subgroupId") String subgroupId,
                                            @RequestBody String formData) throws JsonProcessingException {
        try {
            SongSubgroup songSubgroup = songSubgroupRepository.findById(Integer.valueOf(subgroupId)).get();
            Song relatedSong = songSubgroup.getSong();
            Map<?, ?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
            String spotifyLink = (String) objectMapper.get("spotify");
            String itunesLink = (String) objectMapper.get("itunes");
            String soundcloudLink = (String) objectMapper.get("soundcloud");
            String deezerLink = (String) objectMapper.get("deezer");
            String tidalink = (String) objectMapper.get("tidal");
            String officialBand = (String) objectMapper.get("officialBand");
            String officialTitle = (String) objectMapper.get("officialTitle");
            String officialSrcId = (String) objectMapper.get("officialSrcId");
            String lyrics = (String) objectMapper.get("lyrics");
            relatedSong.setOfficialDisplayBand(returnValueToSet(officialBand));
            relatedSong.setOfficialDisplayTitle(returnValueToSet(officialTitle));
            relatedSong.setSpotifyId(returnValueToSet(spotifyLink));
            relatedSong.setItunesLink(returnValueToSet(itunesLink));
            relatedSong.setSoundcloudLink(returnValueToSet(soundcloudLink));
            relatedSong.setDeezerId(returnValueToSet(deezerLink));
            relatedSong.setTidalLink(returnValueToSet(tidalink));
            relatedSong.setSrcId(returnValueToSet(officialSrcId));
            songSubgroup.setLyrics(lyrics);
            songRepository.save(relatedSong);
            return new ObjectMapper().writeValueAsString("OK");
        } catch (Throwable thr) {
            return new ObjectMapper().writeValueAsString(thr);
        }
    }

    private String returnValueToSet(String field) {
        if (!field.equals("null") && !field.equals("undefined")) {
            return field;
        } else return null;
    }

    @PostMapping(value = "/post/{subgroupId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String postNewSong(@PathVariable("subgroupId") String subgroupId, @RequestBody String formData) throws JsonProcessingException {
        try {
            Map<?, ?> objectMapper = new ObjectMapper().readValue(formData, Map.class);
            String ingameBand = (String) objectMapper.get("ingameBand");
            String ingameTitle = (String) objectMapper.get("ingameTitle");
            String ingameSrcId = (String) objectMapper.get("ingameSrcId");
            String mainAliasId = (String) objectMapper.get("aliasId");
            String authorId = (String) objectMapper.get("authorId");
            Subgroup subgroup = subgroupRepository.findById(Integer.valueOf(subgroupId)).get();
            Object potentialExistingSong = objectMapper.get("existingSongId");
            if (potentialExistingSong != null) {
                Integer existingSongId = Integer.valueOf(potentialExistingSong.toString());
                Song existingSong = songRepository.findById(existingSongId).get();
                SongSubgroup newSongSubgroup = new SongSubgroup();
                newSongSubgroup.setSubgroup(subgroup);
                newSongSubgroup.setIngameDisplayBand(returnValueToSet(ingameBand));
                newSongSubgroup.setIngameDisplayTitle(returnValueToSet(ingameTitle));
                newSongSubgroup.setSrcId(returnValueToSet(ingameSrcId));
//                newSongSubgroup.set
//                newSongSubgroup.set
            }
            return "OK";
        } catch (Exception exp) {
            return null;
        }
    }
}