package com.z01.blog.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.z01.blog.infrastructure.EntityAccessResolver;
import com.z01.blog.resolver.AuthResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final EntityAccessResolver<Long> entityAccessResolver;
    private final AuthResolver authResolver;

    public WebConfig(AuthResolver authUserResolver, EntityAccessResolver<Long> entityAccessResolver) {
        this.authResolver = authUserResolver;
        this.entityAccessResolver = entityAccessResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins("http://localhost:4200").allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authResolver);
        resolvers.add(entityAccessResolver);
    }
}