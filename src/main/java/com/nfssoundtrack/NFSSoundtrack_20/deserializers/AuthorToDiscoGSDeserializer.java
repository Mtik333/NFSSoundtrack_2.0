package com.nfssoundtrack.NFSSoundtrack_20.deserializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.nfssoundtrack.NFSSoundtrack_20.others.AuthorToDiscoGSObj;
import com.nfssoundtrack.NFSSoundtrack_20.others.DiscoGSObj;
import com.nfssoundtrack.NFSSoundtrack_20.others.JustSomeHelper;

import java.io.IOException;

public class AuthorToDiscoGSDeserializer extends JsonDeserializer<AuthorToDiscoGSObj> {
    @Override
    public AuthorToDiscoGSObj deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int artistId = node.get("artistId").asInt();
        int discogsId = node.get("discogs").get("discogsId").asInt();
        String uri = JustSomeHelper.returnProperValueToDb(node.get("discogs").get("uri").asText());
        String profile = JustSomeHelper.returnProperValueToDb(node.get("discogs").get("profile").asText());
        String twitter = JustSomeHelper.returnProperValueToDb(node.get("discogs").get("twitter").asText());
        String facebook = JustSomeHelper.returnProperValueToDb(node.get("discogs").get("facebook").asText());
        String instagram = JustSomeHelper.returnProperValueToDb(node.get("discogs").get("instagram").asText());
        String soundcloud = JustSomeHelper.returnProperValueToDb(node.get("discogs").get("soundcloud").asText());
        String myspace = JustSomeHelper.returnProperValueToDb(node.get("discogs").get("myspace").asText());
        String wikipedia = JustSomeHelper.returnProperValueToDb(node.get("discogs").get("wikipedia").asText());
        Boolean notInDiscogs = node.get("discogs").get("notInDiscogs").asBoolean();
        DiscoGSObj discoGSObj = new DiscoGSObj(notInDiscogs,discogsId,uri,profile);
        discoGSObj.setWikipedia(wikipedia);
        discoGSObj.setMyspace(myspace);
        discoGSObj.setSoundcloud(soundcloud);
        discoGSObj.setTwitter(twitter);
        discoGSObj.setFacebook(facebook);
        discoGSObj.setInstagram(instagram);
        AuthorToDiscoGSObj authorToDiscoGSObj = new AuthorToDiscoGSObj(artistId, discoGSObj);
        return authorToDiscoGSObj;
    }
}
