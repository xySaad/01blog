package com.z01.blog.services;

import java.time.LocalDateTime;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.model.Session;
import com.z01.blog.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class AuthService {
    @Autowired
    private SecretKey jwtKey;
    @Autowired
    private Session.repo sessionRepo;
    @Autowired
    private UserModel.repo userRepo;

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
        Optional<UserModel> user = userRepo.findById(accountId);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return user.get().accountId;
    }

}
