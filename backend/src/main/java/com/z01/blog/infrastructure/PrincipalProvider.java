package com.z01.blog.infrastructure;

public interface PrincipalProvider<U> {
    U getCurrentPrincipal();
}
