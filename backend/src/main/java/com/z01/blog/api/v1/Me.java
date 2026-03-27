package com.z01.blog.api.v1;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.exception.AppError;
import com.z01.blog.model.Account;
import com.z01.blog.model.DTO.MeResponse;
import com.z01.blog.model.RBAC.RoleModel;
import com.z01.blog.model.User.UserEntity;
import com.z01.blog.model.User.UserRepo;

@RestController
public class Me {
    @Autowired
    UserRepo userRepo;

    @Autowired
    Account.repo accountRepo;

    @Autowired
    RoleModel.repo roleRepo;

    @GetMapping("/api/v1/me")
    MeResponse getUserOwnInfo(@Auth.Account long accountId) {
        Account account = accountRepo.findById(accountId).get();
        if (account.verificationCode != null) // not verified
            throw AppError.ACCOUNT_NOT_VERIFIED.asException();

        // verified but may not have created a user yet
        Optional<UserEntity> user = userRepo.findByAccountIdAndDeletedFalse(accountId);
        if (user.isEmpty())
            throw AppError.USER_PROFILE_NOT_FOUND.asException();

        var permissions = roleRepo.findScopeByAccountId(accountId);
        return new MeResponse(user.get(), permissions);
    }
}
