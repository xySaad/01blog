package com.z01.blog.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.WebUtils;

import com.z01.blog.annotation.Auth;
import com.z01.blog.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthResolver implements HandlerMethodArgumentResolver {
    @Autowired
    AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.User.class)
                || parameter.hasParameterAnnotation(Auth.Account.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        Cookie cookie = WebUtils.getCookie(request, "jwt");
        if (cookie == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        if (parameter.hasParameterAnnotation(Auth.User.class))
            return this.authService.getUserId(cookie.getValue());
        else if (parameter.hasParameterAnnotation(Auth.Account.class))
            return this.authService.getAccountId(cookie.getValue());
        else
            throw new RuntimeException("Invalid Auth annotation");
    }

}
