package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.guards.AuthGuard;
import com.z01.blog.model.Account;
import com.z01.blog.model.Session;

@RestController
public class Verify extends AuthGuard {
    @Autowired
    Session.repo session;
    @Autowired
    private Account.repo accRepo;

    @PostMapping("/api/v1/verify")
    public ResponseEntity<?> verify(@RequestBody int body, @CookieValue(name = "jwt") String jwt) {
        try {

            Account account = accRepo.findById(getAuthId(jwt)).get();
            if (account.verificationCode != body) {
                return ResponseEntity.status(400).build();
            }

            LocalDateTime expirationTime = account.codeCreatedAt.plusMinutes(15);
            if (LocalDateTime.now().isAfter(expirationTime)) {
                return ResponseEntity.status(410).build();
            }

            account.verificationCode = null;
            accRepo.save(account);
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

    }
}
