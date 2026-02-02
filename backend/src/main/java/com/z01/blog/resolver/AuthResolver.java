package com.z01.blog.resolver;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.annotation.Auth;
import com.z01.blog.model.Session;
import com.z01.blog.model.UserModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthResolver implements HandlerMethodArgumentResolver {

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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        for (Cookie cookie : cookies)
            if ("jwt".equals(cookie.getName())) {
                if (parameter.hasParameterAnnotation(Auth.User.class)) {
                    return getAccountId(cookie.getValue());
                } else if (parameter.hasParameterAnnotation(Auth.Account.class)) {
                    return getUserId(cookie.getValue());
                }
            }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @Autowired
    private SecretKey jwtKey;

    @Autowired
    private Session.repo sessionRepo;
    @Autowired
    private UserModel.repo userRepo;

    protected long getAccountId(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            long accountId = Long.valueOf(claims.getSubject());

            Optional<Session> session = sessionRepo.findById(accountId);
            if (session.isEmpty() || !session.get().jwt.equals(jwt)) {
                throw new RuntimeException();
            }
            if (session.get().createdAt.plusDays(3).isBefore(LocalDateTime.now())) {
                throw new RuntimeException();
            }

            return accountId;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    protected long getUserId(String jwt) {
        long accountId = getAccountId(jwt);
        Optional<UserModel> user = userRepo.findById(accountId);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return user.get().accountId;
    }
}
