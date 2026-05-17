package com.z01.blog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.z01.blog.infrastructure.APIEventHandler;

/**
 * Marks a controller method as an API event source.
 * After the method returns successfully, the specified handler is invoked.
 *
 * Usage:
 * 
 * @APIEvent(NotificationEventHandler.class)
 *                                           @PostMapping("/api/v1/posts/")
 *                                           Response create(...) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface APIEvent {
    Class<? extends APIEventHandler> value();
}