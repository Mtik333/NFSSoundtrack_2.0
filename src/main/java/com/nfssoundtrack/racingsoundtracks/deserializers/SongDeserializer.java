package com.nfssoundtrack.racingsoundtracks.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class SongDeserializer extends JsonDeserializer<Song> {

    /**
     * used to PUT song to database with incoming JSON about modifications and entity to be modified
     *
     * @param jsonParser default thing
     * @param ctxt       default thing
     * @param intoValue  song to be modified
     * @return song entity that should be modified in db
     * @throws IOException
     */
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
        intoValue.setFeatNextToBand(node.get("featNextToComposer").asBoolean());
        if (intoValue.getLyrics() != null) {
            intoValue.setLyrics(intoValue.getLyrics().replace("\n", "<br>"));
        }
        return intoValue;
    }

    /**
     * used to POST song to database with incoming JSON about full song info
     *
     * @param jsonParser default thing
     * @param ctxt       default thing
     * @return song entity to be created in database
     * @throws IOException
     */
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
        Boolean featNextToBand = node.get("featNextToComposer").asBoolean();
        if (lyrics != null) {
            lyrics = lyrics.replace("\n", "<br>");
        }
        Song song = new Song(officialBand, officialTitle, officialSrcId, lyrics);
        song.setLinks(spotifyId, deezer, itunes, tidal, soundcloud);
        song.setFeatNextToBand(featNextToBand);
        return song;
    }
}
