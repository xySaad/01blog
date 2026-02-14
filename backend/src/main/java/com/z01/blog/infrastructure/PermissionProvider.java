package com.z01.blog.infrastructure;

public interface PermissionProvider {
    boolean hasPermission(String scope);
}
