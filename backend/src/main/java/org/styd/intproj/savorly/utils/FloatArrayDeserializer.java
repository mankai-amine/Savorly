package org.styd.intproj.savorly.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


public class FloatArrayDeserializer extends JsonDeserializer<float[]> {
    @Override
    public float[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Convert JSON array to float[]
        return p.readValueAs(float[].class);
    }
}
