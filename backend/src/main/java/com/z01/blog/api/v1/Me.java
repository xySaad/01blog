package com.z01.blog.api.v1;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.guards.AuthGuard;
import com.z01.blog.model.Account;
import com.z01.blog.model.UserModel;

@RestController
public class Me extends AuthGuard {
    @Autowired
    UserModel.repo userRepo;

    @Autowired
    Account.repo accountRepo;

    @GetMapping("/api/v1/me")
    UserModel getUserOwnInfo(@CookieValue(name = "jwt") String jwt) {
        long id = this.getAuthId(jwt);
        // if getAuthId didn't throw 401 exception means account is surely exists
        Account account = accountRepo.findById(id).get();
        if (account.verificationCode != null) // not verified
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        // verified but may not have created a user yet
        Optional<UserModel> user = userRepo.findByAccountId(id);
        if (user.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return user.get();
    }
}
