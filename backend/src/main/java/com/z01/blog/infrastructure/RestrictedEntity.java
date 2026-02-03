package com.z01.blog.infrastructure;

import com.z01.blog.annotation.EntityAccess;

public interface RestrictedEntity<U> {
    void ensureAccess(U user, EntityAccess.Mode mode);
}