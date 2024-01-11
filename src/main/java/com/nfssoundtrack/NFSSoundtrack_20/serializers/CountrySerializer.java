package com.nfssoundtrack.NFSSoundtrack_20.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Country;

import java.io.IOException;

public class CountrySerializer extends StdSerializer<Country> {
    public CountrySerializer(Class<Country> t) {
        super(t);
    }

    @Override
    public void serialize(Country country, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("value", country.getId());
        jsonGenerator.writeStringField("label", country.getCountryName());
        jsonGenerator.writeEndObject();
    }
}
