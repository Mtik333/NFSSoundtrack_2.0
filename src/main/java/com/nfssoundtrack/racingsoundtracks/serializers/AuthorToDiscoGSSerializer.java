package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.others.AuthorToDiscoGSObj;

import java.io.IOException;

/**
 * used when we obtained discogs info about author, and we will store this info as JSON on disk
 */
public class AuthorToDiscoGSSerializer extends JsonSerializer<AuthorToDiscoGSObj> {

    @Override
    public void serialize(AuthorToDiscoGSObj authorToDiscoGSObj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("artistId", authorToDiscoGSObj.getArtistId());
        jsonGenerator.writeFieldName("discogs");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeBooleanField("notInDiscogs", authorToDiscoGSObj.getDiscoGSObj().isNotInDiscogs());
        jsonGenerator.writeNumberField("discogsId", authorToDiscoGSObj.getDiscoGSObj().getDiscogsId());
        jsonGenerator.writeStringField("uri", authorToDiscoGSObj.getDiscoGSObj().getUri());
        jsonGenerator.writeStringField("profile", authorToDiscoGSObj.getDiscoGSObj().getProfile());
        jsonGenerator.writeStringField("twitter", authorToDiscoGSObj.getDiscoGSObj().getTwitter());
        jsonGenerator.writeStringField("facebook", authorToDiscoGSObj.getDiscoGSObj().getFacebook());
        jsonGenerator.writeStringField("instagram", authorToDiscoGSObj.getDiscoGSObj().getInstagram());
        jsonGenerator.writeStringField("soundcloud", authorToDiscoGSObj.getDiscoGSObj().getSoundcloud());
        jsonGenerator.writeStringField("wikipedia", authorToDiscoGSObj.getDiscoGSObj().getWikipedia());
        jsonGenerator.writeStringField("myspace", authorToDiscoGSObj.getDiscoGSObj().getMyspace());
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
    }
}
