package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.others.DiscoGSObj;
import com.nfssoundtrack.NFSSoundtrack_20.others.JustSomeHelper;
import com.nfssoundtrack.NFSSoundtrack_20.others.ResourceNotFoundException;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.ArtistMgmtSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.ArtistSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.AuthorAliasSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.*;


@Controller
@RequestMapping(path = "/author")
public class ArtistController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    ArtistMgmtSerializer artistMgmtSerializer;

    @Autowired
    AuthorAliasSerializer authorAliasSerializer;

    @Autowired
    ArtistSerializer artistSerializer;

    @Autowired
    Map<Long, DiscoGSObj> discoGSObjMap;

    @GetMapping(value = "/authorAlias/{input}")
    public @ResponseBody String readAliasesFromArtist(@PathVariable("input") int input)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(AuthorAlias.class, authorAliasSerializer);
        objectMapper.registerModule(simpleModule);
        AuthorSong authorSong = authorSongService.findById(input).orElseThrow(() -> new ResourceNotFoundException("No alias with id " +
                "found " + input));
        Author author = authorSong.getAuthorAlias().getAuthor();
        List<AuthorAlias> authorAliases = authorAliasService.findByAuthor(author);
        if (logger.isDebugEnabled()){
            logger.debug("authorSong: " + authorSong + ", author " + author);
        }
        return objectMapper.writeValueAsString(authorAliases);
    }

    @GetMapping(value = "/aliasName/{input}")
    public @ResponseBody String readAliases(@PathVariable("input") String input)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(AuthorAlias.class, authorAliasSerializer);
        objectMapper.registerModule(simpleModule);
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        if (input.length() <= 3) {
            Optional<AuthorAlias> authorAlias = authorAliasService.findByAlias(input);
            if (authorAlias.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(Collections.singleton(authorAlias.get()));
        } else {
            List<AuthorAlias> authorAliasList = authorAliasService.findByAliasContains(input);
            if (authorAliasList.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(authorAliasList);
        }
    }

    @GetMapping(value = "/authorName/{input}")
    public @ResponseBody
    String readSpecialArtists(@PathVariable("input") String input)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(AuthorAlias.class, authorAliasSerializer);
        objectMapper.registerModule(simpleModule);
        if (input.length() <= 3) {
            Optional<AuthorAlias> author = authorAliasService.findByAlias(input);
            if (author.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(Collections.singleton(author.get()));
        } else {
            List<AuthorAlias> authorList = authorAliasService.findByAliasContains(input);
            if (authorList == null) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(authorList);
        }
    }

    @GetMapping(value = "/authorNameMgmt/{input}")
    public @ResponseBody
    String readArtistsForMgmt(@PathVariable(name = "input", required = false) String input)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Author.class, artistMgmtSerializer);
        objectMapper.registerModule(simpleModule);
        if (input == null || input.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        if (input.length() <= 3) {
            Optional<Author> author = authorService.findByName(input);
            if (author.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(Collections.singleton(author.get()));
        } else {
            List<Author> authorList = authorService.findByNameContains(input);
            if (authorList == null) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(authorList);
        }
    }

    @PutMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String mergeArtists(@RequestBody String formData)
            throws Exception {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        int authorToMerge = (int) mergeInfo.get("authorToMergeId");
        int targetAuthor = (int) mergeInfo.get("targetAuthorId");
        boolean deleteTargetAlias = (boolean) mergeInfo.get("mergeDeleteAlias");
        Author authorToDelete =
                authorService.findById(authorToMerge).orElseThrow(() -> new ResourceNotFoundException("No author " +
                        "with id found" + authorToMerge));
        AuthorAlias existingAuthorAlias =
                authorAliasService.findByAlias(authorToDelete.getName()).orElseThrow(() -> new ResourceNotFoundException("No alias " +
                        "with input found " + authorToDelete.getName()));
        Author authorToUpdate = authorService.findById(targetAuthor).orElseThrow(() -> new ResourceNotFoundException("No author with " +
                "id found " + targetAuthor));
        AuthorAlias authorAlias;
        if (!deleteTargetAlias){
            authorAlias = new AuthorAlias(authorToUpdate, authorToDelete.getName());
            authorAlias = authorAliasService.save(authorAlias);
        } else {
            authorAlias = authorAliasService.findByAuthor(authorToUpdate).get(0);
        }
        List<AuthorSong> songsToReassign =
                authorSongService.findByAuthorAlias(existingAuthorAlias);
        for (AuthorSong authorSong : songsToReassign) {
            authorSong.setAuthorAlias(authorAlias);
        }
        authorSongService.saveAll(songsToReassign);
        List<AuthorCountry> authorCountries = authorToDelete.getAuthorCountries();
        authorCountryService.deleteAll(authorCountries);
        List<AuthorAlias> aliasesToDelete = authorAliasService.findByAuthor(authorToDelete);
        authorAliasService.deleteAll(aliasesToDelete);
        authorService.delete(authorToDelete);
        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/fixDuplicate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String fixDuplicate(@RequestBody String formData)
            throws Exception {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        String artistName = (String) mergeInfo.get("artistName");
        List<Author> authorList = authorService.findAllByName(artistName);
        authorList.sort(Comparator.comparing(Author::getId));
        Author targetAuthor = authorList.get(0);
        authorList.remove(targetAuthor);
        AuthorAlias defaultAlias = authorAliasService.findByAuthor(targetAuthor).get(0);
        for (Author author : authorList){
            authorCountryService.deleteAll(author.getAuthorCountries());
            List<AuthorAlias> aliases = authorAliasService.findByAuthor(author);
            for (AuthorAlias authorAlias : aliases){
                List<AuthorSong> authorSongs = authorSongService.findByAuthorAlias(authorAlias);
                for (AuthorSong authorSong : authorSongs){
                    authorSong.setAuthorAlias(defaultAlias);
                    authorSongService.save(authorSong);
                }
                authorAliasService.delete(authorAlias);
            }
            authorService.delete(author);
        }

        return new ObjectMapper().writeValueAsString("OK");
    }

    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String updateArtist(@RequestBody String formData)
            throws Exception {
        Map<String, ?> mergeInfo = new ObjectMapper().readValue(formData,
                TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Object.class));
        String authorId = (String) mergeInfo.get("authorId");
        String authorName = (String) mergeInfo.get("authorName");
        Author author = authorService.findById(Integer.parseInt(authorId)).orElseThrow(()
                -> new ResourceNotFoundException("No author with id found " + authorId));
        author.setName(authorName);
        author = authorService.save(author);
        AuthorAlias rootAlias = authorAliasService.findByAuthor(author).get(0);
        List<AuthorCountry> existingCountries = author.getAuthorCountries();
        List<String> countryInfos = mergeInfo.keySet().stream().filter(
                o -> o.contains("countryInfo")).toList();
        List<String> aliasInfos = mergeInfo.keySet().stream().filter(o -> o.contains("aliasInfo")).toList();
        List<String> targetCountrIds = new ArrayList<>();
        List<AuthorCountry> countriesToCreate = new ArrayList<>();
        List<AuthorCountry> countriesToUnlink = new ArrayList<>();
        for (String countryInfo : countryInfos) {
            String valueToGet = (String) mergeInfo.get(countryInfo);
            if (valueToGet.contains("DELETE")) {
                String actualCountryId = valueToGet.replace("DELETE-", "");
                Optional<AuthorCountry> authorCountryToDelete =
                        existingCountries.stream().filter(authorCountry -> authorCountry.getCountry().getId()
                                .equals(Long.parseLong(actualCountryId))).findFirst();
                authorCountryToDelete.ifPresent(countriesToUnlink::add);
            } else {
                String countryId = (String) mergeInfo.get(countryInfo);
                Optional<AuthorCountry> optionalAuthorCountry = existingCountries.stream().filter(authorCountry
                        -> authorCountry.getCountry().getId()
                        .equals(Long.parseLong(countryId))).findFirst();
                if (optionalAuthorCountry.isEmpty()) {
                    Optional<Country> country = countryService.findById(Integer.parseInt(countryId));
                    if (country.isPresent()) {
                        AuthorCountry authorCountry = new AuthorCountry(author, country.get());
                        countriesToCreate.add(authorCountry);
                    }
                }
                targetCountrIds.add(countryId);
            }
        }
        for (AuthorCountry authorCountry : existingCountries) {
            if (!targetCountrIds.contains(authorCountry.getCountry().getId().toString())) {
                countriesToUnlink.add(authorCountry);
            }
        }
        authorCountryService.deleteAll(countriesToUnlink);
        authorCountryService.saveAll(countriesToCreate);
        for (String aliasInfo : aliasInfos) {
            String valueToGet = (String) mergeInfo.get(aliasInfo);
            if (valueToGet.contains("DELETE")) {
                String actualAliasId = valueToGet.replace("DELETE-", "");
                AuthorAlias authorAlias =
                        authorAliasService.findById(Integer.parseInt(actualAliasId)).orElseThrow(() -> new ResourceNotFoundException(
                                "No authoralias with id found " + actualAliasId));
                List<AuthorSong> authorSongs = authorSongService.findByAuthorAlias(authorAlias);
                for (AuthorSong authorSong : authorSongs) {
                    authorSong.setAuthorAlias(rootAlias);
                }
                authorSongService.saveAll(authorSongs);
                authorAliasService.delete(authorAlias);
            } else if (valueToGet.contains("NEW")) {
                String actualNewAlias = valueToGet.replace("NEW-", "");
                AuthorAlias authorAlias = new AuthorAlias(author, actualNewAlias);
                authorAliasService.save(authorAlias);
            } else if (valueToGet.contains("EXISTING")) {
                String[] existingAlias = valueToGet.split("-VAL-");
                String existingId = existingAlias[0].replace("EXISTING-", "");
                AuthorAlias authorAlias =
                        authorAliasService.findById(Integer.parseInt(existingId)).orElseThrow(() -> new ResourceNotFoundException("No" +
                                " authoralias with id found " + existingId));
                authorAlias.setAlias(existingAlias[1]);
                authorAliasService.save(authorAlias);
            }
        }
        Boolean updateDiscogs = (Boolean) mergeInfo.get("discogsToUpdate");
        if (updateDiscogs){
            String uri = JustSomeHelper.returnProperValueToDb((String) mergeInfo.get("uri"));
            String twitter = JustSomeHelper.returnProperValueToDb((String) mergeInfo.get("twitter"));
            String facebook = JustSomeHelper.returnProperValueToDb((String) mergeInfo.get("facebook"));
            String instagram = JustSomeHelper.returnProperValueToDb((String) mergeInfo.get("instagram"));
            String soundcloud = JustSomeHelper.returnProperValueToDb((String) mergeInfo.get("soundcloud"));
            String wikipedia = JustSomeHelper.returnProperValueToDb((String) mergeInfo.get("wikipedia"));
            String myspace = JustSomeHelper.returnProperValueToDb((String) mergeInfo.get("myspace"));
            String profile = JustSomeHelper.returnProperValueToDb((String) mergeInfo.get("profile"));
            String id = (String) mergeInfo.get("id");
            DiscoGSObj discoGSObj = new DiscoGSObj(false, Integer.parseInt(id),uri, profile,
                    twitter,facebook,instagram,soundcloud,myspace,wikipedia);
            authorService.updateDiscoGSObj(Integer.valueOf(authorId),discoGSObj);
        }
        return new ObjectMapper().writeValueAsString("OK");
    }

    @GetMapping(value = "/discogsInfo/{input}")
    public @ResponseBody
    String getDiscogsInfo(@PathVariable("input") int input)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<Long> authorIdAlreadyThere = discoGSObjMap.keySet().stream().filter(aLong ->
                aLong.equals(Long.valueOf(input))).findFirst();
        if (authorIdAlreadyThere.isPresent()) {
            DiscoGSObj discoGSObj = discoGSObjMap.get(authorIdAlreadyThere.get());
            return objectMapper.writeValueAsString(discoGSObj);
        } else {
            return objectMapper.writeValueAsString("null");
        }
    }

    @GetMapping(value = "/discogsEntry/{input}")
    public @ResponseBody
    String findDiscogsInfoViaId(@PathVariable("input") int input)
            throws JsonProcessingException, LoginException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoGSObj discoGSObj = authorService.manuallyFetchDiscogsInfo(input);
        return objectMapper.writeValueAsString(discoGSObj);
    }
}
