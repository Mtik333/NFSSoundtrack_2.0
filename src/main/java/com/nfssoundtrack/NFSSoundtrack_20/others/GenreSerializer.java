package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Genre;

import java.io.IOException;

public class GenreSerializer extends StdSerializer<Genre> {
    public GenreSerializer(Class<Genre> t) {
        super(t);
    }

    @Override
    public void serialize(Genre genre, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", genre.getId());
        jsonGenerator.writeStringField("label", genre.getGenreName());
        jsonGenerator.writeEndObject();
    }
}
