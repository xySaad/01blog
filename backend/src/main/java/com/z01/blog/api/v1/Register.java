package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import java.util.Random;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.exception.AppError;
import com.z01.blog.model.Account;
import com.z01.blog.model.Session;
import com.z01.blog.model.DTO.AuthRequest;
import com.z01.blog.model.RBAC.AccountRoleModel;
import com.z01.blog.model.RBAC.RoleModel;
import com.z01.blog.services.EmailService;

import cn.hutool.core.util.IdUtil;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

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
    @Autowired
    private AccountRoleModel.repo accountRoleRepo;
    @Autowired
    private RoleModel.repo roleRepo;

    @PostMapping("/api/v1/register")
    public void register(@RequestBody @Valid AuthRequest body, HttpServletResponse response) {
        if (accRepo.existsByEmail(body.email)) {
            throw AppError.EMAIL_ALREADY_EXISTS.asException();
        }

        Account account = new Account();
        account.id = IdUtil.getSnowflake().nextId();
        account.email = body.email;
        account.passwordHash = new BCryptPasswordEncoder().encode(body.password);
        account.codeCreatedAt = LocalDateTime.now();
        account.verificationCode = new Random().nextInt(999999);
        accRepo.save(account);

        var defaultRole = roleRepo.findByName("default").get();
        var accRole = new AccountRoleModel(account.id, defaultRole);
        accountRoleRepo.save(accRole);

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

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
