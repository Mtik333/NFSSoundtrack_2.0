package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.ArtistMgmtSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.others.ArtistSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.others.AuthorAliasSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/author")
public class ArtistController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(ArtistController.class);
    @Value("${spring.application.name}")
    String appName;
    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    AuthorSongRepository authorSongRepository;

    @Autowired
    AuthorAliasRepository authorAliasRepository;

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongSubgroupRepository songSubgroupRepository;

    @Autowired
    private AuthorCountryRepository authorCountryRepository;

    @Autowired
    CountryRepository countryRepository;
    @GetMapping(value = "/{authorId}")
    public String author(Model model, @PathVariable("authorId") String authorId) {
        Author author = authorRepository.findById(Integer.valueOf(authorId)).get();
        List<AuthorAlias> allAliases = authorAliasRepository.findByAuthor(author);
        authorSongRepository.findByAuthorAlias(allAliases.get(0));
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsComposer = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsSubcomposer = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsFeat = new HashMap<>();
        Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsRemixed = new HashMap<>();
        for (AuthorAlias authorAlias : allAliases) {
            List<AuthorSong> allAuthorSongs = authorSongRepository.findByAuthorAlias(authorAlias);
            for (AuthorSong authorSong : allAuthorSongs) {
                fillMapForArtistDisplay(authorAlias, authorSong, Role.COMPOSER, songsAsComposer);
                fillMapForArtistDisplay(authorAlias, authorSong, Role.SUBCOMPOSER, songsAsSubcomposer);
                fillMapForArtistDisplay(authorAlias, authorSong, Role.FEAT, songsAsFeat);
                fillMapForArtistDisplay(authorAlias, authorSong, Role.REMIX, songsRemixed);
            }
        }
        model.addAttribute("author", author);
        model.addAttribute("genre", null);
        model.addAttribute("customPlaylist", null);
        model.addAttribute("songsAsComposer", songsAsComposer);
        model.addAttribute("songsAsSubcomposer", songsAsSubcomposer);
        model.addAttribute("songsAsFeat", songsAsFeat);
        model.addAttribute("songsRemixed", songsRemixed);
        model.addAttribute("allAliases", allAliases);
        model.addAttribute("appName", author.getName() + " - " + appName);
        model.addAttribute("series", serieRepository.findAll(Sort.by(Sort.Direction.ASC, "position")));
        return "index";
    }

    private void fillMapForArtistDisplay(AuthorAlias authorAlias, AuthorSong authorSong, Role role,
                                         Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsComposer) {
        List<SongSubgroup> songSubgroupList = songSubgroupRepository.findBySong(authorSong.getSong());
        if (role.equals(authorSong.getRole())) {
            if (songsAsComposer.get(authorAlias) == null) {
                Map<Song, List<SongSubgroup>> songsPerSubgroup = new HashMap<>();
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

    @GetMapping(value = "/authorAlias/{input}")
    public @ResponseBody String readAliasesFromArtist(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString("[]");
        }
        SimpleModule simpleModule = new SimpleModule();
        AuthorSong authorSong = authorSongRepository.findById(Integer.valueOf(input)).get();
        Author author = authorSong.getAuthorAlias().getAuthor();
        List<AuthorAlias> authorAliases = authorAliasRepository.findByAuthor(author);
        simpleModule.addSerializer(AuthorAlias.class, new AuthorAliasSerializer(AuthorAlias.class));
        objectMapper.registerModule(simpleModule);
        String result = objectMapper.writeValueAsString(authorAliases);
        return result;
    }


    @GetMapping(value = "/aliasName/{input}")
    public @ResponseBody String readAliases(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString("[]");
        }
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(AuthorAlias.class, new AuthorAliasSerializer(AuthorAlias.class));
        objectMapper.registerModule(simpleModule);
        if (input.length() <= 3) {
            AuthorAlias authorAlias = authorAliasRepository.findByAlias(input);
            if (authorAlias==null){
                return objectMapper.writeValueAsString("[]");
            }
            String result = objectMapper.writeValueAsString(Collections.singleton(authorAlias));
            return result;
        } else {
            List<AuthorAlias> authorAliasList = authorAliasRepository.findByAliasContains(input);
            if (authorAliasList==null){
                return objectMapper.writeValueAsString("[]");
            }
            String result = objectMapper.writeValueAsString(authorAliasList);
            return result;
        }
    }

    @GetMapping(value = "/authorName/{input}")
    public @ResponseBody String readSpecialArtists(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Author.class, new ArtistSerializer(Author.class));
        objectMapper.registerModule(simpleModule);
        if (input.length() <= 3) {
            Author author = authorRepository.findByName(input);
            if (author==null){
                return objectMapper.writeValueAsString("[]");
            }
            String result = objectMapper.writeValueAsString(Collections.singleton(author));
            return result;
        } else {
            List<Author> authorList = authorRepository.findByNameContains(input);
            if (authorList==null){
                return objectMapper.writeValueAsString("[]");
            }
            String result = objectMapper.writeValueAsString(authorList);
            return result;
        }
    }

    @GetMapping(value = "/authorNameMgmt/{input}")
    public @ResponseBody String readArtistsForMgmt(Model model, @PathVariable("input") String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString("[]");
        }
        SimpleModule simpleModule = new SimpleModule();
        ArtistMgmtSerializer artistMgmtSerializer = new ArtistMgmtSerializer(Author.class);
        ArtistMgmtSerializer.authorAliasRepository=authorAliasRepository;
        simpleModule.addSerializer(Author.class, artistMgmtSerializer);
        objectMapper.registerModule(simpleModule);
        if (input.length() <= 3) {
            Author author = authorRepository.findByName(input);
            if (author==null){
                return objectMapper.writeValueAsString("[]");
            }
            String result = objectMapper.writeValueAsString(Collections.singleton(author));
            return result;
        } else {
            List<Author> authorList = authorRepository.findByNameContains(input);
            if (authorList==null){
                return objectMapper.writeValueAsString("[]");
            }
            String result = objectMapper.writeValueAsString(authorList);
            return result;
        }
    }

    @PutMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String mergeArtists(@RequestBody String formData) throws JsonProcessingException {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        String authorToMerge = (String) mergeInfo.get("authorToMergeId");
        String targetAuthor = (String) mergeInfo.get("targetAuthorId");
        Author authorToDelete = authorRepository.findById(Integer.valueOf(authorToMerge)).get();
        AuthorAlias existingAuthorAlias = authorAliasRepository.findByAlias(authorToDelete.getName());
        Author authorToUpdate = authorRepository.findById(Integer.valueOf(targetAuthor)).get();
        AuthorAlias authorAlias = new AuthorAlias();
        authorAlias.setAlias(authorToDelete.getName());
        authorAlias.setAuthor(authorToUpdate);
        authorAlias = authorAliasRepository.save(authorAlias);
        List<AuthorSong> songsToReassign = authorSongRepository.findByAuthorAlias(existingAuthorAlias);
        for (AuthorSong authorSong : songsToReassign){
            authorSong.setAuthorAlias(authorAlias);
        }
        authorSongRepository.saveAll(songsToReassign);
        List<AuthorCountry> authorCountries = authorToDelete.getAuthorCountries();
        authorCountryRepository.deleteAll(authorCountries);
        List<AuthorAlias> aliasesToDelete = authorAliasRepository.findByAuthor(authorToDelete);
        authorAliasRepository.deleteAll(aliasesToDelete);
        authorRepository.delete(authorToDelete);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String updateArtist(@RequestBody String formData) throws JsonProcessingException {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        String authorId = (String) mergeInfo.get("authorId");
        String authorName = (String) mergeInfo.get("authorName");
        Author author = authorRepository.findById(Integer.valueOf(authorId)).get();
        author.setName(authorName);
        AuthorAlias rootAlias = authorAliasRepository.findByAuthor(author).get(0);
        List<AuthorCountry> existingCountries = author.getAuthorCountries();
        List<String> countryInfos = (List<String>) mergeInfo.keySet().stream().filter(o -> o.toString().contains("countryInfo")).collect(Collectors.toList());
        List<String> aliasInfos = (List<String>) mergeInfo.keySet().stream().filter(o -> o.toString().contains("aliasInfo")).collect(Collectors.toList());
        List<String> targetCountrIds = new ArrayList<>();
        Set<AuthorCountry> countriesToCreate = new HashSet<>();
        Set<AuthorCountry> countriesToUnlink = new HashSet<>();
        for (String countryInfo : countryInfos){
            String valueToGet = (String) mergeInfo.get(countryInfo);
            if (valueToGet.contains("DELETE")){
                String actualCountryId = valueToGet.replace("DELETE-","");
                AuthorCountry authorCountryToDelete = existingCountries.stream().filter(authorCountry -> authorCountry.getCountry().getId()
                        .equals(Long.parseLong(actualCountryId))).findFirst().get();
                countriesToUnlink.add(authorCountryToDelete);
//                countriesToUnlink.add(authorCountry);
            } else {
                String countryId = (String) mergeInfo.get(countryInfo);
                Optional<AuthorCountry> optionalAuthorCountry = existingCountries.stream().filter(authorCountry
                        -> authorCountry.getCountry().getId()
                        .equals(Long.parseLong(countryId))).findFirst();
                if (optionalAuthorCountry.isEmpty()){
                    AuthorCountry authorCountry = new AuthorCountry();
                    authorCountry.setAuthor(author);
                    Country country = countryRepository.findById(Long.valueOf(countryId));
                    authorCountry.setCountry(country);
                    countriesToCreate.add(authorCountry);
                }
                targetCountrIds.add(countryId);

            }
        }
        for (AuthorCountry authorCountry : existingCountries){
            if (!targetCountrIds.contains(authorCountry.getCountry().getId().toString())){
                countriesToUnlink.add(authorCountry);
            }
        }
        authorCountryRepository.deleteAll(countriesToUnlink);
        authorCountryRepository.saveAll(countriesToCreate);
        for (String aliasInfo : aliasInfos){
            String valueToGet = (String) mergeInfo.get(aliasInfo);
            if (valueToGet.contains("DELETE")){
                String actualAliasId = valueToGet.replace("DELETE-","");
                AuthorAlias authorAlias = authorAliasRepository.findById(Integer.valueOf(actualAliasId)).get();
                List<AuthorSong> authorSongs = authorSongRepository.findByAuthorAlias(authorAlias);
                for (AuthorSong authorSong : authorSongs){
                    authorSong.setAuthorAlias(rootAlias);
                }
                authorSongRepository.saveAll(authorSongs);
                authorAliasRepository.delete(authorAlias);
            } else if (valueToGet.contains("NEW")){
                AuthorAlias authorAlias = new AuthorAlias();
                String actualNewAlias = valueToGet.replace("NEW-","");
                authorAlias.setAlias(actualNewAlias);
                authorAlias.setAuthor(author);
                authorAliasRepository.save(authorAlias);
            } else if (valueToGet.contains("EXISTING")){
                String[] existingAlias = valueToGet.split("-VAL-");
                String existingId = existingAlias[0].replace("EXISTING-","");
                AuthorAlias authorAlias = authorAliasRepository.findById(Integer.valueOf(existingId)).get();
                authorAlias.setAlias(existingAlias[1]);
                authorAliasRepository.save(authorAlias);
            }
        }
        authorRepository.save(author);
//        String authorToMerge = (String) mergeInfo.get("authorToMergeId");
        return new ObjectMapper().writeValueAsString("OK");
    }
}
