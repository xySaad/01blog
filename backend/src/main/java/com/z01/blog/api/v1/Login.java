package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.z01.blog.model.Account;
import com.z01.blog.model.AuthRequest;
import com.z01.blog.model.Session;

import io.jsonwebtoken.Jwts;

@RestController
public class Login {
    @Autowired
    Account.repo accRepo;
    @Autowired
    private SecretKey jwtKey;
    @Autowired
    private Session.repo sessionRepo;

    @PostMapping("/api/v1/login")
    ResponseEntity<?> login(@RequestBody AuthRequest body) {
        Optional<Account> account = accRepo.findByEmail(body.email);
        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(body.password, account.get().passwordHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long id = account.get().id;
        String jwt = Jwts.builder()
                .setSubject(String.valueOf(id))
                .signWith(jwtKey)
                .compact();
        Session session = new Session();
        session.accountId = id;
        session.jwt = jwt;
        session.createdAt = LocalDateTime.now();
        sessionRepo.save(session);

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 3)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).build();
    }
}
