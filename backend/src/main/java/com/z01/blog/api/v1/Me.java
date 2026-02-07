package com.z01.blog.api.v1;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.annotation.Auth;
import com.z01.blog.model.Account;
import com.z01.blog.model.User.UserEntity;
import com.z01.blog.model.User.UserModel;
import com.z01.blog.model.User.UserRepo;

@RestController
public class Me {
    @Autowired
    UserRepo userRepo;

    @Autowired
    Account.repo accountRepo;

    @GetMapping("/api/v1/me")
    UserModel getUserOwnInfo(@Auth.Account long accountId) {
        Account account = accountRepo.findById(accountId).get();
        if (account.verificationCode != null) // not verified
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        // verified but may not have created a user yet
        Optional<UserEntity> user = userRepo.findByAccountId(accountId);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return user.get();
    }
}
