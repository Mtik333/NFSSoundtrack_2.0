package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Serie;

import java.io.IOException;

public class SerieSerializer extends StdSerializer<Serie> {


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
