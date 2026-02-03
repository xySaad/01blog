package com.z01.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.WebUtils;

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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return authService.getUserId(cookie.getValue());
    }
}