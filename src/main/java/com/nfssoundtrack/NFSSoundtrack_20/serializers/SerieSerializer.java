package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SerieSerializer extends StdSerializer<Serie> {

    private static final Logger logger = LoggerFactory.getLogger(SerieSerializer.class);

    public SerieSerializer(Class<Serie> t) {
        super(t);
    }

    @Override
    public void serialize(Serie serie, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", serie.getId());
        jsonGenerator.writeNumberField("position", serie.getPosition());
        jsonGenerator.writeStringField("name", serie.getName());
        jsonGenerator.writeEndObject();
    }
}
