package com.nfssoundtrack.NFSSoundtrack_20.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorCountry;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorSong;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Country;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.ArtistMgmtSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.ArtistSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.AuthorAliasSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

	@GetMapping(value = "/authorAlias/{input}")
	public @ResponseBody
	String readAliasesFromArtist(@PathVariable("input") int input) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(AuthorAlias.class, authorAliasSerializer);
		objectMapper.registerModule(simpleModule);
		AuthorSong authorSong = authorSongService.findById(input).orElseThrow(() -> new Exception("No alias with id " +
				"found " + input));
		Author author = authorSong.getAuthorAlias().getAuthor();
		List<AuthorAlias> authorAliases = authorAliasService.findByAuthor(author);
		String result = objectMapper.writeValueAsString(authorAliases);
		return result;
	}

	@GetMapping(value = "/aliasName/{input}")
	public @ResponseBody
	String readAliases(@PathVariable("input") String input)
			throws JsonProcessingException, InvocationTargetException, NoSuchMethodException, InstantiationException,
			IllegalAccessException {
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
			String result = objectMapper.writeValueAsString(Collections.singleton(authorAlias.get()));
			return result;
		} else {
			List<AuthorAlias> authorAliasList = authorAliasService.findByAliasContains(input);
			if (authorAliasList.isEmpty()) {
				return objectMapper.writeValueAsString(null);
			}
			String result = objectMapper.writeValueAsString(authorAliasList);
			return result;
		}
	}

	@GetMapping(value = "/authorName/{input}")
	public @ResponseBody
	String readSpecialArtists(@PathVariable("input") String input)
			throws JsonProcessingException, InvocationTargetException, NoSuchMethodException, InstantiationException,
			IllegalAccessException {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Author.class, artistSerializer);
		objectMapper.registerModule(simpleModule);
		if (input.length() <= 3) {
			Optional<Author> author = authorService.findByName(input);
			if (author.isEmpty()) {
				return objectMapper.writeValueAsString(null);
			}
			String result = objectMapper.writeValueAsString(Collections.singleton(author.get()));
			return result;
		} else {
			List<Author> authorList = authorService.findByNameContains(input);
			if (authorList == null) {
				return objectMapper.writeValueAsString(null);
			}
			String result = objectMapper.writeValueAsString(authorList);
			return result;
		}
	}

	@GetMapping(value = "/authorNameMgmt/{input}")
	public @ResponseBody
	String readArtistsForMgmt(@PathVariable("input") String input)
			throws JsonProcessingException, InvocationTargetException, NoSuchMethodException, InstantiationException,
			IllegalAccessException {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Author.class, artistMgmtSerializer);
		objectMapper.registerModule(simpleModule);
		if (input.isEmpty()) {
			return objectMapper.writeValueAsString(null);
		}
		if (input.length() <= 3) {
			Optional<Author> author = authorService.findByName(input);
			if (author.isEmpty()) {
				return objectMapper.writeValueAsString(null);
			}
			String result = objectMapper.writeValueAsString(Collections.singleton(author));
			return result;
		} else {
			List<Author> authorList = authorService.findByNameContains(input);
			if (authorList == null) {
				return objectMapper.writeValueAsString(null);
			}
			String result = objectMapper.writeValueAsString(authorList);
			return result;
		}
	}

	@PutMapping(value = "/merge", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String mergeArtists(@RequestBody String formData) throws Exception {
		Map<?, ?> mergeInfo = new ObjectMapper().readValue(formData, Map.class);
		int authorToMerge = (int) mergeInfo.get("authorToMergeId");
		int targetAuthor = (int) mergeInfo.get("targetAuthorId");
		Author authorToDelete =
				authorService.findById(authorToMerge).orElseThrow(() -> new Exception("No author " +
						"with id found" + authorToMerge));
		AuthorAlias existingAuthorAlias =
				authorAliasService.findByAlias(authorToDelete.getName()).orElseThrow(() -> new Exception("No alias " +
						"with input found " + authorToDelete.getName()));
		Author authorToUpdate = authorService.findById(targetAuthor).orElseThrow(() -> new Exception("No author with " +
				"id found " + targetAuthor));
		AuthorAlias authorAlias = new AuthorAlias(authorToUpdate, authorToDelete.getName());
		authorAlias = authorAliasService.save(authorAlias);
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

	@PutMapping(value = "/put", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody
	String updateArtist(@RequestBody String formData) throws Exception {
		Map<String, ?> mergeInfo = new ObjectMapper().readValue(formData,
				TypeFactory.defaultInstance().constructMapType(Map.class, String.class, Object.class));
		String authorId = (String) mergeInfo.get("authorId");
		String authorName = (String) mergeInfo.get("authorName");
		Author author = authorService.findById(Integer.parseInt(authorId)).get();
		author.setName(authorName);
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
						authorAliasService.findById(Integer.parseInt(actualAliasId)).orElseThrow(() -> new Exception(
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
						authorAliasService.findById(Integer.parseInt(existingId)).orElseThrow(() -> new Exception("No" +
								" authoralias with id found " + existingId));
				authorAlias.setAlias(existingAlias[1]);
				authorAliasService.save(authorAlias);
			}
		}
		authorService.save(author);
		return new ObjectMapper().writeValueAsString("OK");
	}
}
