package com.z01.blog.infrastructure;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.annotation.RequiresPermission;

@Aspect
@Component
public class SecurityAspect {

    private final PermissionProvider validator;

    public SecurityAspect(PermissionProvider validator) {
        this.validator = validator;
    }

    @Before("@annotation(requiresPermission)")
    public void doPermissionCheck(RequiresPermission requiresPermission) {
        String scope = requiresPermission.scope();
        if (!validator.hasPermission(scope))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}