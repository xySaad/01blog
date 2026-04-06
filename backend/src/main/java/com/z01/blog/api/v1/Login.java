package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.exception.AppError;
import com.z01.blog.model.Account;
import com.z01.blog.model.Session;
import com.z01.blog.model.DTO.AuthRequest;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class Login {
    @Autowired
    Account.repo accRepo;
    @Autowired
    private SecretKey jwtKey;
    @Autowired
    private Session.repo sessionRepo;

    @PostMapping("/api/v1/login")
    void login(@RequestBody AuthRequest body, HttpServletResponse response) {
        Optional<Account> account = accRepo.findByEmail(body.email);
        if (account.isEmpty()) {
            throw AppError.INVALID_EMAIL_OR_PASSWORD.asException();
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(body.password, account.get().passwordHash)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
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

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
