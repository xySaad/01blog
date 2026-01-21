package com.z01.blog.controller;

import com.z01.blog.model.Session;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;

import java.time.LocalDateTime;
import java.util.Optional;

public abstract class Auth {

    @Autowired
    private SecretKey jwtKey;

    @Autowired
    private Session.repo sessionRepo;

    protected Long getAuthId(String jwt) {
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
}