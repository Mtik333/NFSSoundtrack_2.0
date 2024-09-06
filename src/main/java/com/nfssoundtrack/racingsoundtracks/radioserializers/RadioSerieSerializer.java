package com.nfssoundtrack.racingsoundtracks.radioserializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Serie;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class RadioSerieSerializer extends JsonSerializer<Serie> {

    /**
     * i don't recall now why it is even needed, maybe for the future of radio rewritten?
     *
     * @param serie
     * @param jsonGenerator
     * @param serializerProvider
     * @throws IOException
     */
    @Override
    public void serialize(Serie serie, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", serie.getId());
        jsonGenerator.writeStringField("name", serie.getName());
        jsonGenerator.writeEndObject();
    }
}
