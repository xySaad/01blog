package com.z01.blog.infrastructure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LongAsStringSerializer extends JsonSerializer<Long> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String header = request.getHeader("X-JSON-Format");
        if ("long-as-string".equalsIgnoreCase(header)) {
            gen.writeString(value.toString());
        } else {
            gen.writeNumber(value);
        }
    }
}