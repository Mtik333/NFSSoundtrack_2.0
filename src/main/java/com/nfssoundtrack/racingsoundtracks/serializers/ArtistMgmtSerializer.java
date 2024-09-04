package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Author;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorAlias;
import com.nfssoundtrack.racingsoundtracks.dbmodel.AuthorCountry;
import com.nfssoundtrack.racingsoundtracks.services.AuthorAliasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.List;

/**
 * used when we just type in the author name and look for artist
 * so we build JSON based on this info from database
 */
@JsonComponent
public class ArtistMgmtSerializer extends JsonSerializer<Author> {

    @Autowired
    AuthorAliasService authorAliasService;

    private static final Logger logger = LoggerFactory.getLogger(ArtistMgmtSerializer.class);

    @Override
    public void serialize(Author author, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", author.getId());
        jsonGenerator.writeStringField("label", author.getName());
        if (author.getSkipDiscogs()!=null){
            jsonGenerator.writeBooleanField("skipDiscogs", author.getSkipDiscogs());
        }
        List<AuthorAlias> authorAliases = authorAliasService.findByAuthor(author);
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
            jsonGenerator.writeObjectField("countryName", authorCountry.getCountry().getCountryName());
            jsonGenerator.writeObjectField("countryId", authorCountry.getCountry().getId());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        if (logger.isDebugEnabled()){
            logger.debug("resulting json: {}", jsonGenerator);
        }
    }
}
