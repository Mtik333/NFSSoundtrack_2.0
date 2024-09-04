package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Game;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * method to render specific serie as well as all of its games
 * from this perspective we just need basic info about game, no need to obtain songs
 */
@JsonComponent
public class GameSerializer extends JsonSerializer<Game> {

    @Override
    public void serialize(Game game, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", game.getId());
        jsonGenerator.writeNumberField("position", game.getPosition());
        jsonGenerator.writeStringField("displayTitle", game.getDisplayTitle());
        jsonGenerator.writeEndObject();
    }
}
