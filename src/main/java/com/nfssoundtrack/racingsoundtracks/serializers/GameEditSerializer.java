package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class GameEditSerializer extends JsonSerializer<Game> {

    @Override
    public void serialize(Game game, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
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
        jsonGenerator.writeStringField("additionalInfo", game.getAdditionalInfo());
        jsonGenerator.writeEndObject();
    }
}
