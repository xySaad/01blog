package com.z01.blog.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.z01.blog.infrastructure.LongAsStringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    private final LongAsStringSerializer conditionalLongSerializer;

    public JacksonConfig(LongAsStringSerializer conditionalLongSerializer) {
        this.conditionalLongSerializer = conditionalLongSerializer;
    }

    @Bean
    public Module longAsStringModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, conditionalLongSerializer);
        module.addSerializer(Long.TYPE, conditionalLongSerializer);
        return module;
    }
}