package com.nfssoundtrack.racingsoundtracks.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.nfssoundtrack.racingsoundtracks.others.AuthorToDiscoGSObj;
import com.nfssoundtrack.racingsoundtracks.others.DiscoGSObj;
import com.nfssoundtrack.racingsoundtracks.others.JustSomeHelper;

import java.io.IOException;

public class AuthorToDiscoGSDeserializer extends JsonDeserializer<AuthorToDiscoGSObj> {

    public static final String DISCOGS = "discogs";

    @Override
    public AuthorToDiscoGSObj deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int artistId = node.get("artistId").asInt();
        int discogsId = node.get(DISCOGS).get("discogsId").asInt();
        String uri = JustSomeHelper.returnProperValueToDb(node.get(DISCOGS).get("uri").asText());
        String profile = JustSomeHelper.returnProperValueToDb(node.get(DISCOGS).get("profile").asText());
        String twitter = JustSomeHelper.returnProperValueToDb(node.get(DISCOGS).get("twitter").asText());
        String facebook = JustSomeHelper.returnProperValueToDb(node.get(DISCOGS).get("facebook").asText());
        String instagram = JustSomeHelper.returnProperValueToDb(node.get(DISCOGS).get("instagram").asText());
        String soundcloud = JustSomeHelper.returnProperValueToDb(node.get(DISCOGS).get("soundcloud").asText());
        String myspace = JustSomeHelper.returnProperValueToDb(node.get(DISCOGS).get("myspace").asText());
        String wikipedia = JustSomeHelper.returnProperValueToDb(node.get(DISCOGS).get("wikipedia").asText());
        boolean notInDiscogs = node.get(DISCOGS).get("notInDiscogs").asBoolean();
        DiscoGSObj discoGSObj = new DiscoGSObj(notInDiscogs, discogsId, uri, profile);
        discoGSObj.setSocialLink(twitter,facebook,instagram,soundcloud,myspace,wikipedia);
        return new AuthorToDiscoGSObj(artistId, discoGSObj);
    }
}
