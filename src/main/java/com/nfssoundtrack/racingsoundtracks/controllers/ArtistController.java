package com.nfssoundtrack.racingsoundtracks.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.racingsoundtracks.dbmodel.*;
import com.nfssoundtrack.racingsoundtracks.others.DiscoGSObj;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.others.ResourceNotFoundException;
import com.nfssoundtrack.racingsoundtracks.serializers.ArtistMgmtSerializer;
import com.nfssoundtrack.racingsoundtracks.serializers.AuthorAliasSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.*;

/**
 * controller for various author-related operations
 * used in fixDuplicateArtist.js, artistMgmt.js, mergeArtist.js, songsMgmt.js, associateArtist.js
 */
@Controller
@RequestMapping(path = "/author")
public class ArtistController extends BaseControllerWithErrorHandling {

    private static final Logger logger = LoggerFactory.getLogger(ArtistController.class);

    @Autowired
    ArtistMgmtSerializer artistMgmtSerializer;

    @Autowired
    AuthorAliasSerializer authorAliasSerializer;

    @Autowired
    Map<Long, DiscoGSObj> discoGSObjMap;

    /**
     * used when editing song, we get and render alias used for the song
     * you can see it when typing in "alias" textfield on manage songs when creating / editing song
     * songMgmt.js
     *
     * @param input id of author (or rather author's alias) associated with song
     * @return json list of aliases of author associated with looked-up song
     * @throws ResourceNotFoundException when no author-song entry is found
     * @throws JsonProcessingException   issue with writing json to frontend
     */
    @GetMapping(value = "/authorAlias/{input}")
    public @ResponseBody
    String readAliasesFromArtist(@PathVariable("input") int input)
            throws ResourceNotFoundException, JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(AuthorAlias.class,
                authorAliasSerializer);
        AuthorSong authorSong = authorSongService.findById(input).orElseThrow(
                () -> new ResourceNotFoundException("No alias with id " +
                        "found " + input));
        Author author = authorSong.getAuthorAlias().getAuthor();
        List<AuthorAlias> authorAliases = authorAliasService.findByAuthor(author);
        if (logger.isDebugEnabled()) {
            logger.debug("authorSong: {}, author {}", authorSong, author);
        }
        return objectMapper.writeValueAsString(authorAliases);
    }

    /**
     * used when creating song and modifying existing artist (after we selected one through input)
     * you can see it being used when going to 'manage artists' and once you select author, trying to type in alias
     * also when you type in 'feat' / 'subcomposer' text field in edit / new song in 'manage songs'
     * artistMgmt.js, songsMgmt.js
     *
     * @param input alias value
     * @return json list or single alias related to provided input
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/aliasName/{aliasValue}")
    public @ResponseBody
    String readAliases(@PathVariable("aliasValue") String input)
            throws JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(AuthorAlias.class,
                authorAliasSerializer);
        //it might be that JS will try to send empty input for alias so double checking this
        if (input.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        //we dont want to storm db with so short type character but there can be really so short aliases
        //therefore we look for exactly the one as in input
        if (input.length() <= 3) {
            Optional<AuthorAlias> authorAlias = authorAliasService.findByAlias(input);
            if (authorAlias.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(Collections.singleton(authorAlias.get()));
        } else {
            //otherwise just look for any alias that contains the input
            List<AuthorAlias> authorAliasList = authorAliasService.findByAliasContains(input);
            if (authorAliasList == null || authorAliasList.isEmpty()) {
                return objectMapper.writeValueAsString(null);
            }
            return objectMapper.writeValueAsString(authorAliasList);
        }
    }

    /**
     * used when we just type in the author name and look for artist to edit and when merging artists
     * artistMgmt.js, mergeArtist.js, associateArtist.js
     * you can see it being used when going to 'manage artists' and you type in 'author name'
     * also when you type in 'author' in 'associate existing alias' module
     *
     * @param input official name of the author
     * @return json list of authors or just author to be managed
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/authorNameMgmt/{authorName}")
    public @ResponseBody
    String readArtistsForMgmt(@PathVariable(name = "authorName", required = false) String input)
            throws JsonProcessingException {
        ObjectMapper objectMapper = JustSomeHelper.registerSerializerForObjectMapper(Author.class,
                artistMgmtSerializer);
        if (input == null || input.isEmpty()) {
            return objectMapper.writeValueAsString(null);
        }
        //here we do the same as in readAliases but instead we look for author who should have one 'official' name
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

    /**
     * used when merging artists in 'associate existing alias' once you click on 'save'
     * mergeArtist.js
     *
     * @param formData consist of author-slave that should be merged with author-master
     *                 as database can have duplicates, allowing to create alias in author-master
     *                 based on value of author-slave
     * @return OK string if successful merge
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String mergeArtists(@RequestBody String formData)
            throws JsonProcessingException, ResourceNotFoundException {
        Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
        int authorToMerge = (int) mergeInfo.get("authorToMergeId");
        int targetAuthor = (int) mergeInfo.get("targetAuthorId");
        boolean deleteTargetAlias = (boolean) mergeInfo.get("mergeDeleteAlias");
        //we look for author-slave, then author-master (but input for second one accepts alias too)
        Author authorToDelete =
                authorService.findById(authorToMerge).orElseThrow(() -> new ResourceNotFoundException("No author " +
                        "with id found" + authorToMerge));
        AuthorAlias existingAuthorAlias =
                authorAliasService.findByAlias(authorToDelete.getName()).orElseThrow(
                        () -> new ResourceNotFoundException("No alias " +
                                "with input found " + authorToDelete.getName()));
        Author authorToUpdate = authorService.findById(targetAuthor).orElseThrow(
                () -> new ResourceNotFoundException("No author with " +
                        "id found " + targetAuthor));
        String message = "Merging " + authorToDelete.getName() + " into " + authorToUpdate.getName()
                + (!deleteTargetAlias ? " - old name is deleted" : "");
        AuthorAlias authorAlias;
        //if we want to make alias on target author due to merge, here's this done
        if (!deleteTargetAlias) {
            authorAlias = new AuthorAlias(authorToUpdate, authorToDelete.getName());
            authorAlias = authorAliasService.save(authorAlias);
        } else {
            authorAlias = authorAliasService.findByAuthor(authorToUpdate).get(0);
        }
        //for each song associated with author-slave we have to change author to avoid DB exception
        List<AuthorSong> songsToReassign =
                authorSongService.findByAuthorAlias(existingAuthorAlias);
        for (AuthorSong authorSong : songsToReassign) {
            authorSong.setAuthorAlias(authorAlias);
        }
        //then we simply delete associations from author-slave: countries and aliases
        authorSongService.saveAll(songsToReassign);
        List<AuthorCountry> authorCountries = authorToDelete.getAuthorCountries();
        authorCountryService.deleteAll(authorCountries);
        List<AuthorAlias> aliasesToDelete = authorAliasService.findByAuthor(authorToDelete);
        authorAliasService.deleteAll(aliasesToDelete);
        authorService.delete(authorToDelete);
        sendMessageToChannel(EntityType.AUTHOR_ALIAS, "merge", message, EntityUrl.AUTHOR,
                authorToUpdate.getName(), String.valueOf(authorToUpdate.getId()));
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * used when associating one artist into another in 'Add artist as member' once you click on 'save'
     * associateArtist.js
     *
     * @param formData consist of author-slave that should be merged with author-master
     *                 as database can have duplicates, allowing to create alias in author-master
     *                 based on value of author-slave
     * @return OK string if successful merge
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @PutMapping(value = "/associateArtist", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String associateArtist(@RequestBody String formData) throws ResourceNotFoundException, JsonProcessingException {
        Map<?, ?> associateInfo = new ObjectMapper().readValue(formData, Map.class);
        int memberAuthorId = (int) associateInfo.get("authorToBeMemberId");
        int targetAuthorId = (int) associateInfo.get("authorToAssignMemberId");
        Author newMemberAuthor =
                authorService.findById(memberAuthorId).orElseThrow(() -> new ResourceNotFoundException("No author " +
                        "with id found" + memberAuthorId));
        Author targetAuthor = authorService.findById(targetAuthorId).orElseThrow(
                () -> new ResourceNotFoundException("No author with " +
                        "id found " + targetAuthorId));
        String message = "Adding " + newMemberAuthor.getName() + " as member of " + targetAuthor.getName();
        AuthorMember authorMember = new AuthorMember(newMemberAuthor, targetAuthor);
        authorMemberService.save(authorMember);
        sendMessageToChannel(EntityType.AUTHOR_MEMBER, "save", message, EntityUrl.AUTHOR,
                targetAuthor.getName(), String.valueOf(targetAuthor.getId()));
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * earlier there was an issue that sometimes clicking on author input when creating song would make a duplicate
     * artist with same name but different id
     * so i created method to "merge" artists with exactly the same name that got created due to this
     * so you can see it used when clicking 'save' in 'merge duplicate artists' module
     * used in fixDuplicateArtist.js
     *
     * @param formData consists just of artist name
     * @return OK if successful
     * @throws JsonProcessingException
     */
    @PutMapping(value = "/fixDuplicate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String fixDuplicate(@RequestBody String formData)
            throws JsonProcessingException {
        Map<?, ?> formDataMap = new ObjectMapper().readValue(formData, Map.class);
        String artistName = (String) formDataMap.get("artistName");
        //expecting more than just 1 artist
        //assumption that right artist is the one with lowest ID
        List<Author> authorList = authorService.findAllByName(artistName);
        authorList.sort(Comparator.comparing(Author::getId));
        Author targetAuthor = authorList.get(0);
        String message = "Fixing duplicate entries of author " + targetAuthor.getName();
        authorList.remove(targetAuthor);
        //so for each duplicate artist, gonna change song assignment to master artist
        //then just deleting alias of such author, countries, and the author in the end
        AuthorAlias defaultAlias = authorAliasService.findByAuthor(targetAuthor).get(0);
        for (Author author : authorList) {
            authorCountryService.deleteAll(author.getAuthorCountries());
            List<AuthorAlias> aliases = authorAliasService.findByAuthor(author);
            for (AuthorAlias authorAlias : aliases) {
                List<AuthorSong> authorSongs = authorSongService.findByAuthorAlias(authorAlias);
                for (AuthorSong authorSong : authorSongs) {
                    authorSong.setAuthorAlias(defaultAlias);
                    authorSongService.save(authorSong);
                }
                authorAliasService.delete(authorAlias);
            }
            authorService.delete(author);
        }
        sendMessageToChannel(EntityType.AUTHOR, "fix", message, EntityUrl.AUTHOR,
                targetAuthor.getName(), String.valueOf(targetAuthor.getId()));
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * used to update existing author info, we can update discogs links, author name, aliases, country or countries
     * artistMgmt.js
     * you can see it triggered when you click on 'save' button in 'manage artists' module
     *
     * @param formData a lot of information, need to document it somewhere
     * @return OK if success
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     * @throws LoginException
     * @throws InterruptedException
     */
    @PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String updateArtist(@RequestBody String formData)
            throws JsonProcessingException, ResourceNotFoundException, LoginException, InterruptedException {
        Map<String, ?> formDataMap = new ObjectMapper().readValue(formData,
                TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Object.class));
        String authorId = (String) formDataMap.get("authorId");
        String authorName = (String) formDataMap.get("authorName");
        //need to get author entity and send potential new name
        Author author = authorService.findById(Integer.parseInt(authorId)).orElseThrow(()
                -> new ResourceNotFoundException("No author with id found " + authorId));
        author.setName(authorName);
        String message = "Updating author " + author.getName() + " finished";
        //it might be that author is so obscure that it is not on DiscoGS so we skip looking him up there
        Boolean setSkipDiscogs = (Boolean) formDataMap.get("setSkipDiscogs");
        author.setSkipDiscogs(setSkipDiscogs);
        author = authorService.saveUpdate(author);
        //now we have to check if there are ID of countries that have to be assigned
        AuthorAlias rootAlias = authorAliasService.findByAuthor(author).get(0);
        List<AuthorCountry> existingCountries = author.getAuthorCountries();
        List<String> countryInfos = formDataMap.keySet().stream().filter(
                o -> o.contains("countryInfo")).toList();
        List<String> aliasInfos = formDataMap.keySet().stream().filter(o -> o.contains("aliasInfo")).toList();
        List<String> targetCountrIds = new ArrayList<>();
        List<AuthorCountry> countriesToCreate = new ArrayList<>();
        List<AuthorCountry> countriesToUnlink = new ArrayList<>();
        for (String countryInfo : countryInfos) {
            String valueToGet = (String) formDataMap.get(countryInfo);
            //we might need to unlink country to artist
            if (valueToGet.contains("DELETE")) {
                String actualCountryId = valueToGet.replace("DELETE-", "");
                Optional<AuthorCountry> authorCountryToDelete =
                        existingCountries.stream().filter(authorCountry -> authorCountry.getCountry().getId()
                                .equals(Long.parseLong(actualCountryId))).findFirst();
                if (authorCountryToDelete.isEmpty()) {
                    continue;
                }
                String localMessage = "Unlinking country " + authorCountryToDelete.get().getCountry().getCountryName()
                        + " from author " + author.getName();
                sendMessageToChannel(EntityType.AUTHOR_COUNTRY, "delete", localMessage,
                        EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
                authorCountryToDelete.ifPresent(countriesToUnlink::add);
            } else {
                //otherwise we wll simply create association between author and country if it does not exist
                String countryId = (String) formDataMap.get(countryInfo);
                Optional<AuthorCountry> optionalAuthorCountry = existingCountries.stream().filter(authorCountry
                        -> authorCountry.getCountry().getId()
                        .equals(Long.parseLong(countryId))).findFirst();
                if (optionalAuthorCountry.isEmpty()) {
                    Optional<Country> country = countryService.findById(Integer.parseInt(countryId));
                    if (country.isPresent()) {
                        AuthorCountry authorCountry = new AuthorCountry(author, country.get());
                        countriesToCreate.add(authorCountry);
                        String localMessage = "Adding country " + authorCountry.getCountry().getCountryName()
                                + " to author " + author.getName();
                        sendMessageToChannel(EntityType.AUTHOR_COUNTRY, "create", localMessage,
                                EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
                    }
                }
                targetCountrIds.add(countryId);
            }
        }
        //so from JS we really send all countries associated with author, even ones persisted already
        //but i'm not sure if this one is really needed
        for (AuthorCountry authorCountry : existingCountries) {
            if (!targetCountrIds.contains(authorCountry.getCountry().getId().toString())) {
                countriesToUnlink.add(authorCountry);
            }
        }
        authorCountryService.deleteAll(countriesToUnlink);
        authorCountryService.saveAll(countriesToCreate);
        //and if there are new aliases too
        for (String aliasInfo : aliasInfos) {
            String valueToGet = (String) formDataMap.get(aliasInfo);
            //first case: delete to remove the alias and re-assign songs to the 'main name'
            if (valueToGet.contains("DELETE")) {
                String actualAliasId = valueToGet.replace("DELETE-", "");
                AuthorAlias authorAlias =
                        authorAliasService.findById(Integer.parseInt(actualAliasId)).orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "No authoralias with id found " + actualAliasId));
                List<AuthorSong> authorSongs = authorSongService.findByAuthorAlias(authorAlias);
                for (AuthorSong authorSong : authorSongs) {
                    authorSong.setAuthorAlias(rootAlias);
                }
                String localMessage = "Deleting alias " + authorAlias.getAlias()
                        + " from author " + author.getName();
                authorSongService.saveAll(authorSongs);
                authorAliasService.delete(authorAlias);
                sendMessageToChannel(EntityType.AUTHOR_ALIAS, "delete", localMessage, EntityUrl.AUTHOR,
                        author.getName(), String.valueOf(author.getId()));
            } else if (valueToGet.contains("NEW")) {
                //second case - just create a new alias for author
                String actualNewAlias = valueToGet.replace("NEW-", "");
                AuthorAlias authorAlias = new AuthorAlias(author, actualNewAlias);
                String localMessage = "Creating alias " + authorAlias.getAlias()
                        + " for author " + author.getName();
                authorAliasService.save(authorAlias);
                sendMessageToChannel(EntityType.AUTHOR_ALIAS, "create", localMessage, EntityUrl.AUTHOR,
                        author.getName(), String.valueOf(author.getId()));
            } else if (valueToGet.contains("EXISTING")) {
                //if exists, we might want to just change its value so we will do it here
                String[] existingAlias = valueToGet.split("-VAL-");
                String existingId = existingAlias[0].replace("EXISTING-", "");
                AuthorAlias authorAlias =
                        authorAliasService.findById(Integer.parseInt(existingId)).orElseThrow(
                                () -> new ResourceNotFoundException("No" +
                                        " authoralias with id found " + existingId));
                if (authorAlias.getAlias().equals(existingAlias[1])) {
                    continue;
                }
                String localMessage = "Updating alias " + authorAlias.getAlias() + " to " + existingAlias[1]
                        + " for author " + author.getName();
                authorAlias.setAlias(existingAlias[1]);
                authorAliasService.saveUpdate(authorAlias);
                sendMessageToChannel(EntityType.AUTHOR_ALIAS, "update", localMessage, EntityUrl.AUTHOR,
                        author.getName(), String.valueOf(author.getId()));
            }
        }
        //if we do care about DiscoGS, all links will be updated in "DiscoGS" index stored locally on the server
        if (Boolean.FALSE.equals(author.getSkipDiscogs())) {
            Boolean updateDiscogs = (Boolean) formDataMap.get("discogsToUpdate");
            if (Boolean.TRUE.equals(updateDiscogs)) {
                String uri = JustSomeHelper.returnProperValueToDb((String) formDataMap.get("uri"));
                String twitter = JustSomeHelper.returnProperValueToDb((String) formDataMap.get("twitter"));
                String facebook = JustSomeHelper.returnProperValueToDb((String) formDataMap.get("facebook"));
                String instagram = JustSomeHelper.returnProperValueToDb((String) formDataMap.get("instagram"));
                String soundcloud = JustSomeHelper.returnProperValueToDb((String) formDataMap.get("soundcloud"));
                String wikipedia = JustSomeHelper.returnProperValueToDb((String) formDataMap.get("wikipedia"));
                String myspace = JustSomeHelper.returnProperValueToDb((String) formDataMap.get("myspace"));
                String profile = JustSomeHelper.returnProperValueToDb((String) formDataMap.get("profile"));
                String id = (String) formDataMap.get("id");
                DiscoGSObj discoGSObj = new DiscoGSObj(false, Integer.parseInt(id), uri, profile);
                discoGSObj.setSocialLink(twitter, facebook, instagram, soundcloud, myspace, wikipedia);
                String localMessage = "Updating DiscoGS info for author " + author.getName();
                //as during edit we maybe updated the author, we want to update the index too
                authorService.updateDiscoGSObj(Long.valueOf(authorId), discoGSObj);
                sendMessageToChannel(EntityType.AUTHOR, "update", localMessage,
                        EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
            }
        }
        //it might be that there was a typo in author official name, so if we want to fix all the songs associated
        //with such author, we will go through all songs and correct this value there
        Boolean updateOfficialArtist = (Boolean) formDataMap.get("changeOfficialArtist");
        if (Boolean.TRUE.equals(updateOfficialArtist)) {
            String artistOldName = (String) formDataMap.get("artistOldName");
            List<AuthorSong> authorSongs = authorSongService.findByAuthorAlias(rootAlias);
            if (artistOldName != null) {
                String localMessage = "Changing official display of artist from " + artistOldName +
                        " to " + authorName + " on " + authorSongs.size() + " songs";
                for (AuthorSong authorSong : authorSongs) {
                    // in case songs consist of multiple artists in official band
                    // we rather want to replace old value of band by new value
                    Song song = authorSong.getSong();
                    String oldOfficialDisplayBand = song.getOfficialDisplayBand();
                    song.setOfficialDisplayBand(oldOfficialDisplayBand.replace(artistOldName, authorName));
                    songService.save(song);
                }
                sendMessageToChannel(EntityType.AUTHOR_SONG, "update", localMessage,
                        EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
            }
        }
        sendMessageToChannel(EntityType.AUTHOR, "update", message,
                EntityUrl.AUTHOR, author.getName(), String.valueOf(author.getId()));
        return new ObjectMapper().writeValueAsString("OK");
    }

    /**
     * used to get DiscoGS info of the author - if it exists we will render all these links and stuff
     * otherwise it's gonna be empty / full of nulls
     * artistMgmt.js
     * used when you select author in 'author name' in manage artists - div with DiscoGS info will be rendered
     *
     * @param input id of author that we are editing
     * @return json with all info from DiscoGS about author
     * @throws JsonProcessingException
     */
    @GetMapping(value = "/discogsInfo/{input}")
    public @ResponseBody
    String getDiscogsInfo(@PathVariable("input") int input)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Optional<Long> authorIdAlreadyThere = discoGSObjMap.keySet().stream().filter(aLong ->
                aLong.equals((long) input)).findFirst();
        if (authorIdAlreadyThere.isPresent()) {
            DiscoGSObj discoGSObj = discoGSObjMap.get(authorIdAlreadyThere.get());
            return objectMapper.writeValueAsString(discoGSObj);
        } else {
            //i don't remember what's going to happen in the frontend if we get here
            return objectMapper.writeValueAsString("null");
        }
    }

    /**
     * used to fetch info from DiscoGS when editing the author
     * you can see it being triggered when clicking 'fetch from DiscoGS' button
     * used in artistMgmt.js
     *
     * @param discogsId id of author on DiscoGS
     * @param formData  just name of artist and it really is used only when author is not in DiscoGS
     * @return json all info about the author on DiscoGS
     * @throws JsonProcessingException
     * @throws LoginException
     * @throws InterruptedException
     */
    @PostMapping(value = "/discogsEntry/{discogsId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    String findDiscogsInfoViaId(@PathVariable("discogsId") int discogsId, @RequestBody String formData)
            throws JsonProcessingException, LoginException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<?, ?> discogsInfo = new ObjectMapper().readValue(formData, Map.class);
        String artistName = (String) discogsInfo.get("name");
        //calling the service to get author details and bring it back to frontend
        //once we save the author, these fetched values will be saved to index too
        DiscoGSObj discoGSObj = authorService.manuallyFetchDiscogsInfo(artistName, discogsId);
        return objectMapper.writeValueAsString(discoGSObj);
    }
}
