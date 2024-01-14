package com.nfssoundtrack.NFSSoundtrack_20.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.others.JustSomeHelper;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class SongDeserializer extends JsonDeserializer<Song> {

    @Override
    public Song deserialize(JsonParser jsonParser, DeserializationContext ctxt, Song intoValue)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        intoValue.setSpotifyId(JustSomeHelper.returnProperValueToDb(node.get("spotify").asText()));
        intoValue.setItunesLink(JustSomeHelper.returnProperValueToDb(node.get("itunes").asText()));
        intoValue.setDeezerId(JustSomeHelper.returnProperValueToDb(node.get("deezer").asText()));
        intoValue.setTidalLink(JustSomeHelper.returnProperValueToDb(node.get("tidal").asText()));
        intoValue.setSoundcloudLink(JustSomeHelper.returnProperValueToDb(node.get("soundcloud").asText()));
        intoValue.setOfficialDisplayBand(JustSomeHelper.returnProperValueToDb(node.get("officialBand").asText()));
        intoValue.setOfficialDisplayTitle(JustSomeHelper.returnProperValueToDb(node.get("officialTitle").asText()));
        intoValue.setSrcId(JustSomeHelper.returnProperValueToDb(node.get("officialSrcId").asText()));
        intoValue.setLyrics(JustSomeHelper.returnProperValueToDb(node.get("lyrics").asText()));
        return intoValue;
    }

    @Override
    public Song deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String spotifyId = (JustSomeHelper.returnProperValueToDb(node.get("spotify").asText()));
        String itunes = (JustSomeHelper.returnProperValueToDb(node.get("itunes").asText()));
        String deezer = (JustSomeHelper.returnProperValueToDb(node.get("deezer").asText()));
        String tidal = (JustSomeHelper.returnProperValueToDb(node.get("tidal").asText()));
        String soundcloud = (JustSomeHelper.returnProperValueToDb(node.get("soundcloud").asText()));
        String officialBand = (JustSomeHelper.returnProperValueToDb(node.get("officialBand").asText()));
        String officialTitle = (JustSomeHelper.returnProperValueToDb(node.get("officialTitle").asText()));
        String officialSrcId = (JustSomeHelper.returnProperValueToDb(node.get("officialSrcId").asText()));
        String lyrics = (JustSomeHelper.returnProperValueToDb(node.get("lyrics").asText()));
        return new Song(officialBand, officialTitle, officialSrcId, lyrics, spotifyId, deezer, itunes, tidal,
                soundcloud);
    }
}
