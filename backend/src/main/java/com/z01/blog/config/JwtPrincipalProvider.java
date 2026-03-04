package com.z01.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.z01.blog.exception.AppError;
import com.z01.blog.infrastructure.PrincipalProvider;
import com.z01.blog.services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtPrincipalProvider implements PrincipalProvider<Long> {
    @Autowired
    HttpServletRequest request;
    @Autowired
    AuthService authService;

    @Override
    public Long getCurrentPrincipal() {
        Cookie cookie = WebUtils.getCookie(request, "jwt");
        if (cookie == null)
            throw AppError.MISSING_JWT_TOKEN.asException();

        return authService.getUserId(cookie.getValue());
    }
}