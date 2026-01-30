package com.z01.blog.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class EntityNotFoundAspect {
    @Around("execution(* org.springframework.data.repository.Repository+.*(..))")
    public Object handleNotFound(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        if (result == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return result;
    }
}
