package com.z01.blog.infrastructure;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.z01.blog.annotation.APIEvent;

/**
 * Intercepts controller methods annotated with @APIEvent and invokes the
 * declared handler after the method returns successfully.
 *
 * Exceptions thrown by the handler are logged but do NOT propagate —
 * the controller response has already been committed at this point.
 */
@Aspect
@Component
public class APIEventAspect {

    @Autowired
    private ApplicationContext context;

    @AfterReturning(pointcut = "@annotation(apiEvent)", returning = "returnValue")
    public void handleEvent(JoinPoint joinPoint, APIEvent apiEvent, Object returnValue) {
        Class<? extends APIEventHandler> handlerClass = apiEvent.value();
        try {
            APIEventHandler handler = context.getBean(handlerClass);
            handler.handle(returnValue, joinPoint.getArgs());
        } catch (Exception e) {
            // Handler failures must never affect the already-completed API response.
            System.err.println("[APIEventAspect] Handler " + handlerClass.getSimpleName()
                    + " threw an exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}