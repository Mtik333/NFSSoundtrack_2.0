package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Song;
import com.nfssoundtrack.NFSSoundtrack_20.serializers.SongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class SimpleSongSerializer extends JsonSerializer<Song> {

    private static final Logger logger = LoggerFactory.getLogger(SongSerializer.class);

    @Override
    public void serialize(Song song, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", song.getId());
        jsonGenerator.writeStringField("label", song.getId().toString());
        jsonGenerator.writeEndObject();
    }
}
