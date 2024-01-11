package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorAlias;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.AuthorCountry;
import com.nfssoundtrack.NFSSoundtrack_20.repository.AuthorAliasRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class ArtistMgmtSerializer extends StdSerializer<Author> {

    public static AuthorAliasRepository authorAliasRepository;

    private static final Logger logger = LoggerFactory.getLogger(ArtistMgmtSerializer.class);

    public ArtistMgmtSerializer(Class<Author> t) {
        super(t);
    }

    public ArtistMgmtSerializer() {
        super(Author.class);
    }

    @Autowired
    public ArtistMgmtSerializer(AuthorAliasRepository authorAliasRepository) {
        this();
        ArtistMgmtSerializer.authorAliasRepository = authorAliasRepository;
    }

    @Override
    public void serialize(Author author, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", author.getId());
        jsonGenerator.writeStringField("label", author.getName());
        List<AuthorAlias> authorAliases = authorAliasRepository.findByAuthor(author);
        jsonGenerator.writeArrayFieldStart("aliases");
        for (AuthorAlias authorAlias : authorAliases) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("aliasId", authorAlias.getId());
            jsonGenerator.writeObjectField("aliasName", authorAlias.getAlias());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        List<AuthorCountry> authorCountries = author.getAuthorCountries();
        jsonGenerator.writeArrayFieldStart("countries");
        for (AuthorCountry authorCountry : authorCountries) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("countryAuthorId", authorCountry.getId());
            jsonGenerator.writeObjectField("countryName", authorCountry.getCountry().getCountryName());
            jsonGenerator.writeObjectField("countryId", authorCountry.getCountry().getId());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
