package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GameSerializer extends StdSerializer<Game> {

    private static final Logger logger = LoggerFactory.getLogger(GameSerializer.class);

    public GameSerializer(Class<Game> t) {
        super(t);
    }

    @Override
    public void serialize(Game game, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", game.getId());
        jsonGenerator.writeNumberField("position", game.getPosition());
        jsonGenerator.writeStringField("displayTitle", game.getDisplayTitle());
        jsonGenerator.writeEndObject();
    }
}
