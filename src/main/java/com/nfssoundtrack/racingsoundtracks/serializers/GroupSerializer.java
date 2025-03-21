package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.MainGroup;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Subgroup;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * method to get all groups that belong to game we are editing,
 * but we do not fetch info about songs in subgroups
 */
@JsonComponent
public class GroupSerializer extends JsonSerializer<MainGroup> {

    @Override
    public void serialize(MainGroup mainGroup, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", mainGroup.getId());
        jsonGenerator.writeStringField("groupName", mainGroup.getGroupName());
        jsonGenerator.writeNumberField("position", mainGroup.getPosition());
        jsonGenerator.writeFieldName("subgroups");
        jsonGenerator.writeStartArray();
        for (Subgroup subgroup : mainGroup.getSubgroups()) {
            jsonGenerator.writeStartObject();
            //for this purpose subgroup name and position value is sufficient
            jsonGenerator.writeObjectField("id", subgroup.getId());
            jsonGenerator.writeObjectField("subgroupName", subgroup.getSubgroupName());
            jsonGenerator.writeObjectField("position", subgroup.getPosition());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
