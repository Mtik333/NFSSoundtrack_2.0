package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * endpoint used for stuff from Toni's Music Library - not relevant to the website
 */
@JsonComponent
public class SongSubgroupFilenameSerializer extends JsonSerializer<SongSubgroup> {

    @Override
    public void serialize(SongSubgroup songSubgroup, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("officialArtist", songSubgroup.getSong().getOfficialDisplayBand());
        jsonGenerator.writeStringField("officialTitle", songSubgroup.getSong().getOfficialDisplayTitle());
        jsonGenerator.writeStringField("ingameDisplayBand", songSubgroup.getIngameDisplayBand());
        jsonGenerator.writeStringField("ingameDisplayArtist", songSubgroup.getIngameDisplayTitle());
        jsonGenerator.writeStringField("group", songSubgroup.getSubgroup().getMainGroup().getGroupName());
        jsonGenerator.writeStringField("subgroup", songSubgroup.getSubgroup().getSubgroupName());
        jsonGenerator.writeStringField("game", songSubgroup.getSubgroup().getMainGroup().getGame().getDisplayTitle());
        jsonGenerator.writeStringField("filename", songSubgroup.getFilename());
        jsonGenerator.writeStringField("info", songSubgroup.getInfo());
        jsonGenerator.writeEndObject();
    }
}
