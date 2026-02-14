package com.z01.blog.config;

import org.springframework.stereotype.Component;

import com.z01.blog.infrastructure.PermissionProvider;
import com.z01.blog.infrastructure.PrincipalProvider;
import com.z01.blog.model.RBAC.AccountRoleModel;

@Component
public class PermissionValidator implements PermissionProvider {
    private final PrincipalProvider<Long> principalProvider;
    private final AccountRoleModel.repo roleRepo;

    public PermissionValidator(PrincipalProvider<Long> principalProvider, AccountRoleModel.repo roleRepo) {
        this.principalProvider = principalProvider;
        this.roleRepo = roleRepo;

    }

    @Override
    public boolean hasPermission(String scope) {
        Long accountId = principalProvider.getCurrentPrincipal();
        System.out.println("account with id: " + accountId + " needs: " + scope);
        return roleRepo.existsById_AccountIdAndRole_Permissions_Scope(accountId, scope);
    }
}