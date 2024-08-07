package com.nfssoundtrack.racingsoundtracks.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Instrumental;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Remix;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Song;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Optional;

@JsonComponent
public class SongSubgroupDeserializer extends JsonDeserializer<SongSubgroup> {

    public static final String INGAME_BAND = "ingameBand";
    public static final String INGAME_TITLE = "ingameTitle";
    public static final String INGAME_SRC_ID = "ingameSrcId";
    public static final String REMIX = "remix";
    @Autowired
    SongService songService;

    @Override
    public SongSubgroup deserialize(JsonParser jsonParser, DeserializationContext ctxt, SongSubgroup intoValue)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        intoValue.setSpotifyId(JustSomeHelper.returnProperValueToDb(node.get("spotify").asText()));
        intoValue.setItunesLink(JustSomeHelper.returnProperValueToDb(node.get("itunes").asText()));
        intoValue.setDeezerId(JustSomeHelper.returnProperValueToDb(node.get("deezer").asText()));
        intoValue.setTidalLink(JustSomeHelper.returnProperValueToDb(node.get("tidal").asText()));
        intoValue.setSoundcloudLink(JustSomeHelper.returnProperValueToDb(node.get("soundcloud").asText()));
        intoValue.setIngameDisplayBand(JustSomeHelper.returnProperValueToDb(node.get(INGAME_BAND).asText()));
        intoValue.setIngameDisplayTitle(JustSomeHelper.returnProperValueToDb(node.get(INGAME_TITLE).asText()));
        intoValue.setSrcId(JustSomeHelper.returnProperValueToDb(node.get(INGAME_SRC_ID).asText()));
        intoValue.setInstrumental(Instrumental.fromBoolean(node.get("instrumental").asBoolean()));
        intoValue.setRemix(Remix.fromBoolean(node.get(REMIX).asBoolean()));
        intoValue.setInfo(JustSomeHelper.returnProperValueToDb(node.get("info").asText()));
        intoValue.setFilename(JustSomeHelper.returnProperValueToDb(node.get("filename").asText()));
        intoValue.setRemixText(JustSomeHelper.returnProperValueToDb(node.get("remixValue").asText()));
        intoValue.setLyrics(JustSomeHelper.returnProperValueToDb(node.get("lyrics").asText()));
        intoValue.setShowFeat(node.get("showFeat").asBoolean());
        intoValue.setShowSubcomposer(node.get("showSubcomposer").asBoolean());
        if (intoValue.getLyrics() != null) {
            intoValue.setLyrics(intoValue.getLyrics().replace("\n", "<br>"));
        }
        return intoValue;
    }

    @Override
    public SongSubgroup deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String spotifyLink = JustSomeHelper.returnProperValueToDb(node.get("spotify").asText());
        String itunesLink = JustSomeHelper.returnProperValueToDb(node.get("itunes").asText());
        String soundcloudLink = JustSomeHelper.returnProperValueToDb(node.get("soundcloud").asText());
        String deezerLink = JustSomeHelper.returnProperValueToDb(node.get("deezer").asText());
        String tidalink = JustSomeHelper.returnProperValueToDb(node.get("tidal").asText());
        String ingameBand=null;
        if (node.get(INGAME_BAND)!=null){
            ingameBand = JustSomeHelper.returnProperValueToDb(node.get(INGAME_BAND).asText());
        }
        String ingameTitle=null;
        if (node.get(INGAME_TITLE)!=null){
            ingameTitle = JustSomeHelper.returnProperValueToDb(node.get(INGAME_TITLE).asText());
        }
        String ingameSrcId=null;
        if (node.get(INGAME_SRC_ID)!=null){
            ingameSrcId = JustSomeHelper.returnProperValueToDb(node.get(INGAME_SRC_ID).asText());
        }
        String lyrics = JustSomeHelper.returnProperValueToDb(node.get("lyrics").asText());
        if (lyrics != null) {
            lyrics = lyrics.replace("\n", "<br>");
        }
        String info = JustSomeHelper.returnProperValueToDb(node.get("info").asText());
        String filename = JustSomeHelper.returnProperValueToDb(node.get("filename").asText());
        String remixText = JustSomeHelper.returnProperValueToDb(node.get("remixValue").asText());
        Boolean showFeat = node.get("showFeat").asBoolean();
        Boolean showSubcomposer = node.get("showSubcomposer").asBoolean();
        Instrumental instrumental = Instrumental.fromBoolean(node.get("instrumental").asBoolean());
        Remix remix = Remix.NO;
        if (node.get(REMIX)!=null){
            remix = Remix.fromBoolean(node.get(REMIX).asBoolean());
        }
        SongSubgroup songSubgroup = new SongSubgroup(ingameSrcId, ingameBand, ingameTitle, 10000L, lyrics, info, filename);
        songSubgroup.setLinks(spotifyLink, deezerLink, itunesLink, tidalink, soundcloudLink);
        songSubgroup.setRemixFeatSubcomposer(instrumental, remix,remixText, showFeat, showSubcomposer);
        JsonNode existingSongNode = node.get("existingSongId");
        if (existingSongNode!=null && existingSongNode.isInt()) {
            Integer existingSongId = existingSongNode.asInt();
            Optional<Song> existingSong = songService.findById(existingSongId);
            if (existingSong.isPresent()) {
                songSubgroup.setSong(existingSong.get());
                if (existingSong.get().getBaseSong() != null) {
                    songSubgroup.setRemix(Remix.YES);
                } else {
                    songSubgroup.setRemix(Remix.NO);
                }
            }
        }
        return songSubgroup;
    }
}

