package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.MainGroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.SongSubgroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Subgroup;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * used to get all groups that belong to game we are editing
 * we include songs from group as well
 */
@JsonComponent
public class GroupWithSubgroupsSerializer extends JsonSerializer<MainGroup> {

    @Override
    public void serialize(MainGroup mainGroup, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", mainGroup.getId());
        jsonGenerator.writeStringField("groupName", mainGroup.getGroupName());
        jsonGenerator.writeFieldName("subgroups");
        jsonGenerator.writeStartArray();
        for (Subgroup subgroup : mainGroup.getSubgroups()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("id", subgroup.getId());
            jsonGenerator.writeObjectField("subgroupName", subgroup.getSubgroupName());
            jsonGenerator.writeObjectField("position", subgroup.getPosition());
            jsonGenerator.writeFieldName("songSubgroupList");
            jsonGenerator.writeStartArray();
            for (SongSubgroup songSubgroup : subgroup.getSongSubgroupList()){
                //again here we don't really need all the info for editing purpose, just basic stuff to render in manage songs menu
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("id", songSubgroup.getId());
                jsonGenerator.writeObjectField("position", songSubgroup.getPosition());
                jsonGenerator.writeObjectField("ingameDisplayBand", songSubgroup.getIngameDisplayBand());
                jsonGenerator.writeObjectField("ingameDisplayTitle", songSubgroup.getIngameDisplayTitle());
                jsonGenerator.writeFieldName("song");
                //if i recall correctly, official song info is rendered in admin panel
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("id", songSubgroup.getSong().getId());
                jsonGenerator.writeObjectField("officialDisplayBand", songSubgroup.getSong().getOfficialDisplayBand());
                jsonGenerator.writeObjectField("officialDisplayTitle", songSubgroup.getSong().getOfficialDisplayTitle());
                jsonGenerator.writeEndObject();
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
