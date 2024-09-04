package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * method to render all series from database - serializer by ootb is able of writing multiple series together
 * and we build JSON on this
 */
@JsonComponent
public class SerieSerializer extends JsonSerializer<Serie> {

    @Override
    public void serialize(Serie serie, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", serie.getId());
        jsonGenerator.writeNumberField("position", serie.getPosition());
        jsonGenerator.writeStringField("name", serie.getName());
        jsonGenerator.writeEndObject();
    }
}
