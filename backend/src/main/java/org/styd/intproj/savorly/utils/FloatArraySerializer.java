package org.styd.intproj.savorly.utils;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class FloatArraySerializer extends JsonSerializer<float[]> {
    @Override
    public void serialize(float[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (float f : value) {
            gen.writeNumber(f);
        }
        gen.writeEndArray();
    }
}
