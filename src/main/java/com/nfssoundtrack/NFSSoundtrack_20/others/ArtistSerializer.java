package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;

import java.io.IOException;

public class ArtistSerializer extends StdSerializer<Author> {
    public ArtistSerializer(Class<Author> t) {
        super(t);
    }

    @Override
    public void serialize(Author author, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", author.getId());
        jsonGenerator.writeStringField("label", author.getName());
        jsonGenerator.writeEndObject();
    }
}
