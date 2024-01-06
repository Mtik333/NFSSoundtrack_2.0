package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ArtistSerializer extends StdSerializer<Author> {
    private static final Logger logger = LoggerFactory.getLogger(ArtistSerializer.class);

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
