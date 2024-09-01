package com.nfssoundtrack.racingsoundtracks.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import com.nfssoundtrack.racingsoundtracks.dbmodel.GameStatus;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;
import com.nfssoundtrack.racingsoundtracks.services.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class GameDeserializer extends JsonDeserializer<Game> {

    @Autowired
    SerieService serieService;

    /**
     * used to deserialize when we do 'readyForUpdate' thing to immediately put values from
     * json to the entity - so we do PUT operation
     *
     * @param jsonParser default thing
     * @param ctxt       default thing
     * @param intoValue  entity of game
     * @return modified (but not saved in db yet) game entity
     * @throws IOException
     */
    @Override
    public Game deserialize(JsonParser jsonParser, DeserializationContext ctxt, Game intoValue)
            throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        intoValue.setGameTitle(JustSomeHelper.returnProperValueToDb(node.get("gameTitle").asText()));
        intoValue.setDisplayTitle(JustSomeHelper.returnProperValueToDb(node.get("displayTitle").asText()));
        intoValue.setGameShort(JustSomeHelper.returnProperValueToDb(node.get("gameShort").asText()));
        intoValue.setPrefix(JustSomeHelper.returnProperValueToDb(node.get("gamePrefix").asText()));
        intoValue.setGameStatus(GameStatus.valueOf(node.get("gameStatus").asText()));
        intoValue.setSpotifyId(JustSomeHelper.returnProperValueToDb(node.get("spotifyId").asText()));
        intoValue.setDeezerId(JustSomeHelper.returnProperValueToDb(node.get("deezerId").asText()));
        intoValue.setTidalId(JustSomeHelper.returnProperValueToDb(node.get("tidalId").asText()));
        intoValue.setYoutubeId(JustSomeHelper.returnProperValueToDb(node.get("youtubeId").asText()));
        intoValue.setSoundcloudId(JustSomeHelper.returnProperValueToDb(node.get("soundcloudId").asText()));
        intoValue.setAdditionalInfo(JustSomeHelper.returnProperValueToDb(node.get("additionalInfo").asText()));
        return intoValue;
    }

    /**
     * used to deserialize when we want to save a new game
     *
     * @param jsonParser             default thing
     * @param deserializationContext default thing
     * @return new game entity that should be saved in db
     * @throws IOException
     */
    @Override
    public Game deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String gameTitle = JustSomeHelper.returnProperValueToDb(node.get("gameTitle").asText());
        String displayTitle = JustSomeHelper.returnProperValueToDb(node.get("displayTitle").asText());
        String gameShort = JustSomeHelper.returnProperValueToDb(node.get("gameShort").asText());
        String gamePrefix = JustSomeHelper.returnProperValueToDb(node.get("gamePrefix").asText());
        GameStatus gameStatus = GameStatus.valueOf(node.get("gameStatus").asText());
        String spotifyId = JustSomeHelper.returnProperValueToDb(node.get("spotifyId").asText());
        String deezerId = JustSomeHelper.returnProperValueToDb(node.get("deezerId").asText());
        String tidalId = JustSomeHelper.returnProperValueToDb(node.get("tidalId").asText());
        String youtubeId = JustSomeHelper.returnProperValueToDb(node.get("youtubeId").asText());
        String soundcloudId = JustSomeHelper.returnProperValueToDb(node.get("soundcloudId").asText());
        String additionalInfo = JustSomeHelper.returnProperValueToDb(node.get("additionalInfo").asText());
        int serieId = node.get("serieId").asInt();
        Serie currentSerie = serieService.findById(serieId).orElseThrow(() -> new IOException("No " +
                "serie with id found " + serieId));
        Game game = new Game(1000L, gameTitle, displayTitle, gameShort, gamePrefix, gameStatus, additionalInfo);
        game.setAudioLinks(spotifyId, deezerId, tidalId, youtubeId, soundcloudId);
        game.setSerie(currentSerie);
        return game;
    }


}

