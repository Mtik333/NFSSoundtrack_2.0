package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.MainGroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.SongSubgroup;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Subgroup;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class GroupSerializer extends JsonSerializer<MainGroup> {
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
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
