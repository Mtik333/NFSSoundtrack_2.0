package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ArtistMgmtSerializer extends StdSerializer<AuthorAlias> {

    private static final Logger logger = LoggerFactory.getLogger(ArtistMgmtSerializer.class);

    public ArtistMgmtSerializer(Class<AuthorAlias> t) {
        super(t);
    }

    @Override
    public void serialize(AuthorAlias authorAlias, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", authorAlias.getAuthor().getId());
        jsonGenerator.writeStringField("label", authorAlias.getAuthor().getName());
        jsonGenerator.writeObjectField("authorAlias", authorAlias);
        jsonGenerator.writeEndObject();
    }
}
