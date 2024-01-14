package com.nfssoundtrack.NFSSoundtrack_20.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.GameStatus;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import com.nfssoundtrack.NFSSoundtrack_20.others.JustSomeHelper;
import com.nfssoundtrack.NFSSoundtrack_20.services.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class GameDeserializer extends JsonDeserializer<Game> {

    @Autowired
    SerieService serieService;

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
        return intoValue;
    }

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
        int serieId = node.get("serieId").asInt();
        Serie currentSerie = serieService.findById(serieId).orElseThrow(() -> new IOException("No " +
                "serie with id found " + serieId));
        Game game = new Game(1000L, gameTitle, displayTitle, gameShort, gamePrefix, spotifyId,
                deezerId, tidalId, youtubeId, soundcloudId, gameStatus);
        game.setSerie(currentSerie);
        return game;
    }


}

