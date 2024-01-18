package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class ArtistSerializer extends JsonSerializer<Author> {
    private static final Logger logger = LoggerFactory.getLogger(ArtistSerializer.class);

    @Override
    public void serialize(Author author, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", author.getId());
        jsonGenerator.writeStringField("label", author.getName());
        jsonGenerator.writeEndObject();
        if (logger.isDebugEnabled()){
            logger.debug("resulting json: " + jsonGenerator);
        }
    }
}
