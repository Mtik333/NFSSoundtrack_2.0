package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.*;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.SerieSerializer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JustSomeHelper {

//	public static <T> ObjectMapper getObjectMapper(Class<? extends T> type, JsonSerializer<T> ser) {
//		ObjectMapper objectMapper = new ObjectMapper();
//		SimpleModule simpleModule = new SimpleModule();
//		simpleModule.addSerializer(type, ser);
//		objectMapper.registerModule(simpleModule);
//		return objectMapper;
//	}
	public static String returnProperValueToDb(String value){
		if ("".equals(value) || "null".equals(value) || "undefined".equals(value)){
			return null;
		} return value;
	}

	public static void fillMapForArtistDisplay(AuthorAlias authorAlias, AuthorSong authorSong, Role role,
										 Map<AuthorAlias, Map<Song, List<SongSubgroup>>> songsAsComposer,
										 List<SongSubgroup> songSubgroupList) {
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

}
