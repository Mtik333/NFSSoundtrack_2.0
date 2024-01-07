package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GameEditSerializer extends StdSerializer<Game> {

    private static final Logger logger = LoggerFactory.getLogger(GameSerializer.class);

    public GameEditSerializer(Class<Game> t) {
        super(t);
    }

    @Override
    public void serialize(Game game, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", game.getId());
        jsonGenerator.writeStringField("gametitle", game.getGameTitle());
        jsonGenerator.writeStringField("displayTitle", game.getDisplayTitle());
        jsonGenerator.writeStringField("gameshort", game.getGameShort());
        jsonGenerator.writeStringField("prefix", game.getPrefix());
        jsonGenerator.writeStringField("status", game.getGameStatus().value());
        jsonGenerator.writeStringField("spotify_id", game.getSpotifyId());
        jsonGenerator.writeStringField("deezer_id", game.getDeezerId());
        jsonGenerator.writeStringField("tidal_id", game.getTidalId());
        jsonGenerator.writeStringField("soundcloud_id", game.getSoundcloudId());
        jsonGenerator.writeStringField("youtube_id", game.getYoutubeId());
        jsonGenerator.writeEndObject();
    }
}
