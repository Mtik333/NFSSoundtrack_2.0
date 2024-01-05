package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/songSubgroup")
public class SongSubgroupController extends BaseControllerWIthErrorHandling{

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
            Song relatedSong = songSubgroup.getSong();
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
            boolean instrumental = (boolean) objectMapper.get("instrumental");
            boolean feat = (boolean) objectMapper.get("feat");
            boolean remix = (boolean) objectMapper.get("remix");
            boolean subcomposer = (boolean) objectMapper.get("subcomposer");
            String mainAliasId = (String) objectMapper.get("aliasId");
            String authorId = (String) objectMapper.get("authorId");
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
            if (instrumental) {
                songSubgroup.setInstrumental(Instrumental.YES);
            } else {
                songSubgroup.setInstrumental(Instrumental.NO);
            }
            if (remix) {
                songSubgroup.setRemix(Remix.YES);
            } else {
                songSubgroup.setRemix(Remix.NO);
            }
            if (!ingameBand.equals("null") && !ingameBand.equals("undefined")) {
                songSubgroup.setIngameDisplayBand(ingameBand);
            }
            if (!ingameTitle.equals("null") && !ingameTitle.equals("undefined")) {
                songSubgroup.setIngameDisplayTitle(ingameTitle);
            }
            if (!spotifyLink.equals("null") && !spotifyLink.equals("undefined")) {
                songSubgroup.setSpotifyId(spotifyLink);
            } else {
                songSubgroup.setSpotifyId(null);
            }
            if (!itunesLink.equals("null") && !itunesLink.equals("undefined")) {
                songSubgroup.setItunesLink(itunesLink);
            } else {
                songSubgroup.setItunesLink(null);
            }
            if (!soundcloudLink.equals("null") && !soundcloudLink.equals("undefined")) {
                songSubgroup.setSoundcloudLink(soundcloudLink);
            } else {
                songSubgroup.setSoundcloudLink(null);
            }
            if (!deezerLink.equals("null") && !deezerLink.equals("undefined")) {
                songSubgroup.setDeezerId(deezerLink);
            } else {
                songSubgroup.setDeezerId(null);
            }
            if (!tidalink.equals("null") && !tidalink.equals("undefined")) {
                songSubgroup.setTidalLink(tidalink);
            } else {
                songSubgroup.setTidalLink(null);
            }
            if (!ingameSrcId.equals("null") && !ingameSrcId.equals("undefined")) {
                songSubgroup.setSrcId(ingameSrcId);
            } else {
                songSubgroup.setSrcId(null);
            }
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
}
