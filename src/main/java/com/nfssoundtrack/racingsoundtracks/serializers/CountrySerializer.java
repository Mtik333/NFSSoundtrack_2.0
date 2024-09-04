package com.nfssoundtrack.racingsoundtracks.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.nfssoundtrack.racingsoundtracks.dbmodel.Country;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * method to get country / countries based on name input
 * so we build JSON based on this info from database
 */
@JsonComponent
public class CountrySerializer extends JsonSerializer<Country> {

    @Override
    public void serialize(Country country, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();
        //the input-select uses value and label mechanism
        //so the label is just a country name, but value is going to be its id
        jsonGenerator.writeNumberField("value", country.getId());
        jsonGenerator.writeStringField("label", country.getCountryName());
        jsonGenerator.writeEndObject();
    }
}
