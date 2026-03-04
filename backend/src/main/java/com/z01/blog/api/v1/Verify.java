package com.z01.blog.api.v1;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.z01.blog.annotation.Auth;
import com.z01.blog.exception.AppError;
import com.z01.blog.model.Account;
import com.z01.blog.model.Session;

@RestController
public class Verify {
    @Autowired
    Session.repo session;
    @Autowired
    private Account.repo accRepo;

    @PostMapping("/api/v1/verify")
    public void verify(@RequestBody int body, @Auth.Account long accountId) {
        Account account = accRepo.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account not found"));

        if (account.verificationCode != body) {
            throw AppError.INVALID_VERIFICATION_CODE.asException();
        }

        LocalDateTime expirationTime = account.codeCreatedAt.plusMinutes(15);
        if (LocalDateTime.now().isAfter(expirationTime)) {
            throw AppError.VERIFICATION_CODE_EXPIRED.asException();
        }

        account.verificationCode = null;
        accRepo.save(account);

    }
}
