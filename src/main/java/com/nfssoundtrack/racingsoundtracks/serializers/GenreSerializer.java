package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Genre;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * used to get genres with similar name from database
 * so we build JSON based on this info from database
 */
@JsonComponent
public class GenreSerializer extends JsonSerializer<Genre> {

    @Override
    public void serialize(Genre genre, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", genre.getId());
        jsonGenerator.writeStringField("label", genre.getGenreName());
        jsonGenerator.writeEndObject();
    }
}
