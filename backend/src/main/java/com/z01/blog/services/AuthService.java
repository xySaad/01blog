package com.z01.blog.services;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.model.Session;
import com.z01.blog.model.User.UserEntity;
import com.z01.blog.model.User.UserRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class AuthService {
    @Autowired
    private SecretKey jwtKey;
    @Autowired
    private Session.repo sessionRepo;
    @Autowired
    private UserRepo userRepo;

    public long getAccountId(String jwt) {
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

    public long getUserId(String jwt) {
        long accountId = getAccountId(jwt);
        Optional<UserEntity> user = userRepo.findById(accountId);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (user.get().banned)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is banned");

        return user.get().accountId;
    }

}
