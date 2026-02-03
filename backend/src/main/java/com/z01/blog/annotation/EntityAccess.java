package com.z01.blog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.data.repository.Repository;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityAccess {
    public static enum Mode {
        Read,
        Write
    }

    Mode mode();

    Class<? extends Repository> repo() default Repository.class;
}
