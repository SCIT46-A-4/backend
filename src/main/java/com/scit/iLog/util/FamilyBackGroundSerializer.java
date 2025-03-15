package com.scit.iLog.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.scit.iLog.domain.child.FamilyBackGround;

import java.io.IOException;
import java.util.List;

public class FamilyBackGroundSerializer extends JsonSerializer<List<FamilyBackGround>> {

    @Override
    public void serialize(List<FamilyBackGround> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (FamilyBackGround background : value) {
            gen.writeString(background.getDescription());
        }
        gen.writeEndArray();
    }
}