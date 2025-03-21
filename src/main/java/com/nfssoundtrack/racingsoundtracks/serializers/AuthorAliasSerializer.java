package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * used when editing song, we get and render alias used for the song
 * used when creating song and modifying existing artist (after we selected one through input)
 * so we build JSON based on this info from database
 */
@JsonComponent
public class AuthorAliasSerializer extends JsonSerializer<AuthorAlias> {

    private static final Logger logger = LoggerFactory.getLogger(AuthorAliasSerializer.class);

    @Override
    public void serialize(AuthorAlias authorAlias, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", authorAlias.getAuthor().getId());
        jsonGenerator.writeNumberField("aliasId", authorAlias.getId());
        jsonGenerator.writeStringField("label", authorAlias.getAlias());
        jsonGenerator.writeEndObject();
        if (logger.isDebugEnabled()) {
            logger.debug("resulting json: {}", jsonGenerator);
        }
    }
}
