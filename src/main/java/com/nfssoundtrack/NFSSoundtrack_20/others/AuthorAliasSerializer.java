package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;

import java.io.IOException;

public class AuthorAliasSerializer extends StdSerializer<AuthorAlias> {

    public AuthorAliasSerializer(Class<AuthorAlias> t) {
        super(t);
    }

    @Override
    public void serialize(AuthorAlias authorAlias, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value",authorAlias.getId());
        jsonGenerator.writeStringField("label",authorAlias.getAlias());
        jsonGenerator.writeEndObject();
    }
}
