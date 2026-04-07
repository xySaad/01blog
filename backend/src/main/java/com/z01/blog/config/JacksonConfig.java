package com.z01.blog.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

class Serializer extends StdSerializer<Long> {
    Serializer() {
        super(Long.class);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializationContext context) {
        var attrs = RequestContextHolder.getRequestAttributes();
        var request = attrs instanceof ServletRequestAttributes sra ? sra.getRequest() : null;

        if (request != null && "long-as-string".equals(request.getHeader("X-JSON-Format"))) {
            gen.writeString(value.toString());
        } else {
            gen.writeNumber(value);
        }
    }
}

@Configuration
public class JacksonConfig {

    @Bean
    JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            var module = new SimpleModule();
            var serializer = new Serializer();

            module.addSerializer(Long.class, serializer);
            module.addSerializer(long.class, serializer);

            builder.addModule(module);
        };
    }
}