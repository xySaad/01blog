package com.z01.blog.infrastructure;

/**
 * Implement this interface to handle post-success API events.
 * Implementations must be Spring beans (@Component).
 *
 * @param returnValue the value returned by the controller method
 * @param args        the arguments passed to the controller method, in
 *                    declaration order
 */
public interface APIEventHandler {
    void handle(Object returnValue, Object[] args);
}