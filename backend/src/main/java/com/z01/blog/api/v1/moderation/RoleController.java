package com.z01.blog.api.v1.moderation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.Auth;
import com.z01.blog.annotation.EntityAccess;
import com.z01.blog.annotation.EntityAccess.Mode;
import com.z01.blog.exception.AppError;
import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.infrastructure.PermissionProvider;
import com.z01.blog.model.RBAC.AccountRoleModel;
import com.z01.blog.model.RBAC.PermissionModel;
import com.z01.blog.model.RBAC.RoleModel;
import com.z01.blog.model.RBAC.RoleRepo;
import com.z01.blog.model.User.UserEntity;

@RestController
@RequestMapping("/api/v1/moderation/roles")
public class RoleController {
    @Autowired
    AccountRoleModel.repo accountRoleRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    PermissionProvider permissionProvider;

    @RequiresPermission(scope = "v1:roles:read", description = "List all available roles")
    @GetMapping
    List<RoleModel> getAllRoles() {
        return roleRepo.findAllByOrderByPositionAsc();
    }

    // TODO: change @Auth.User principle to UserId + Permission instead of querying
    // DB each time
    @RequiresPermission(scope = "v1:roles:write", description = "Create or update a role")
    @PostMapping
    void createRole(@Auth.User long userId, @RequestBody RoleModel role) {
        role.ensureAccess(userId, Mode.Write);

        // TODO: check all permission of users highest role and below
        for (PermissionModel perm : role.permissions)
            if (!permissionProvider.hasPermission(perm.scope))
                throw AppError.ACCESS_DENIED.asException();

        roleRepo.save(role);
    }

    @RequiresPermission(scope = "v1:roles:write")
    @DeleteMapping("{role}")
    void deleteRole(@EntityAccess(mode = Mode.Write) RoleModel role) {
        roleRepo.deleteById(role.id);
    }

    @GetMapping("/{roleId}/users")
    @RequiresPermission(scope = "v1:roles:read")
    List<UserEntity> getRoleUsers(@PathVariable long roleId) {
        return accountRoleRepo.findUserLoginsByRoleId(roleId);
    }

    public static record UserRoleUpdateRequest(
            List<Long> added,
            List<Long> deleted) {
    }

    @PostMapping("{role}/users")
    @RequiresPermission(scope = "v1:roles:write")
    void updateUsersInRole(@EntityAccess(mode = Mode.Write) RoleModel role, @RequestBody UserRoleUpdateRequest req) {
        // 1. Handle Deletions
        if (req.deleted() != null && !req.deleted().isEmpty()) {
            accountRoleRepo.deleteByRoleIdAndAccountIds(role.id, req.deleted());
        }

        if (req.added() != null && !req.added().isEmpty()) {
            List<AccountRoleModel> rows = req.added().stream()
                    .map(id -> new AccountRoleModel(id, role)).toList();
            accountRoleRepo.saveAll(rows);
        }
    }
}
