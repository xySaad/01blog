package com.z01.blog.config;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.z01.blog.model.Account;
import com.z01.blog.model.RBAC.RoleModel;
import com.z01.blog.model.RBAC.AccountRoleModel;

import cn.hutool.core.util.IdUtil;

@Component
public class RootBootstrap implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private Account.repo accountRepo;
    @Autowired
    private AccountRoleModel.repo userRoleRepo;
    @Autowired
    private RoleModel.repo roleRepo;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (accountRepo.findByEmail("root").isPresent())
            return;

        char[] password = resolvePassword();

        Account root = new Account();
        root.id = IdUtil.getSnowflakeNextId();
        root.email = "root";
        root.passwordHash = new BCryptPasswordEncoder().encode(new String(password));
        root.codeCreatedAt = LocalDateTime.now();
        root.verificationCode = null;
        accountRepo.save(root);

        RoleModel rootRole = roleRepo.findByName("root")
                .orElseThrow(() -> new IllegalStateException("root role not found"));

        userRoleRepo.save(new AccountRoleModel(root.id, rootRole));
    }

    private char[] resolvePassword() {
        if (System.console() != null) {
            System.out.print("Enter root password: ");
            char[] pw = System.console().readPassword();
            System.out.print("Confirm root password: ");
            char[] confirm = System.console().readPassword();

            if (!Arrays.equals(pw, confirm))
                throw new IllegalStateException("Passwords do not match");

            return pw;
        }

        throw new IllegalStateException("Root user not bootstrapped. " + "run interactively.");
    }
}