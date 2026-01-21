package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.Random;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import com.z01.blog.model.Account;
import com.z01.blog.model.AuthRequest;
import com.z01.blog.model.Session;
import com.z01.blog.services.EmailService;
import cn.hutool.core.util.IdUtil;
import io.jsonwebtoken.Jwts;

@RestController
public class Register {
    @Autowired
    private Account.repo accRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private Session.repo sessionRepo;
    @Autowired
    private SecretKey jwtKey;

    @PostMapping("/api/v1/register")
    public ResponseEntity<String> register(@Valid @RequestBody AuthRequest body) {
        if (accRepo.existsByEmail(body.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        Account account = new Account();
        account.id = IdUtil.getSnowflake().nextId();
        account.email = body.email;
        account.passwordHash = new BCryptPasswordEncoder().encode(body.password);
        account.codeCreatedAt = LocalDateTime.now();
        account.verificationCode = new Random().nextInt(999999);
        accRepo.save(account);

        // TODO: implement refresh and access token
        String jwt = Jwts.builder()
                .setSubject(String.valueOf(account.id))
                .signWith(jwtKey)
                .compact();

        Session session = new Session();
        session.accountId = account.id;
        session.jwt = jwt;
        session.createdAt = account.codeCreatedAt; // why not?
        sessionRepo.save(session);

        try {
            emailService.sendVerificationEmail(account.email, account.verificationCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 60 * 24 * 3)
                .sameSite("Lax")
                .build();

        return ResponseEntity.status(HttpStatus.OK).header("Set-Cookie", cookie.toString()).body(null);
    }
}
