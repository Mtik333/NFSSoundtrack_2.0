package com.nfssoundtrack.NFSSoundtrack_20.others;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Author;
import com.nfssoundtrack.NFSSoundtrack_20.dbmodel.Country;
import org.apache.poi.ss.formula.functions.Count;

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
