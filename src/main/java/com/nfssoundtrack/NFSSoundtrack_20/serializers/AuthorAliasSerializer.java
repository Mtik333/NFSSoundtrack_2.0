package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class AuthorAliasSerializer extends JsonSerializer<AuthorAlias> {

    private static final Logger logger = LoggerFactory.getLogger(AuthorAliasSerializer.class);

    @Override
    public void serialize(AuthorAlias authorAlias, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", authorAlias.getId());
        jsonGenerator.writeStringField("label", authorAlias.getAlias());
        jsonGenerator.writeEndObject();
    }
}
