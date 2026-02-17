package com.z01.blog.api.v1.moderation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.model.RBAC.AccountRoleModel;
import com.z01.blog.model.RBAC.RoleModel;
import com.z01.blog.model.User.UserEntity;

@RestController
@RequestMapping("/api/v1/moderation/roles")
public class RoleController {
    @Autowired
    RoleModel.repo repo;
    @Autowired
    AccountRoleModel.repo accountRoleRepo;
    @Autowired
    RoleModel.repo roleRepo;

    @RequiresPermission(scope = "v1:roles:read", description = "List all available roles")
    @GetMapping
    List<RoleModel> getAllRoles() {
        return repo.findAll();
    }

    @RequiresPermission(scope = "v1:roles:write", description = "Create or update a role")
    @PostMapping
    void createRole(@RequestBody RoleModel role) {
        repo.save(role);
    }

    @RequiresPermission(scope = "v1:roles:write")
    @DeleteMapping("{roleId}")
    void deleteRole(@PathVariable long roleId) {
        repo.deleteById(roleId);
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

    @PostMapping("{roleId}/users")
    @RequiresPermission(scope = "v1:roles:write")
    void updateUsersInRole(@PathVariable long roleId, @RequestBody UserRoleUpdateRequest req) {

        RoleModel role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        // 1. Handle Deletions
        if (req.deleted() != null && !req.deleted().isEmpty()) {
            accountRoleRepo.deleteByRoleIdAndAccountIds(roleId, req.deleted());
        }

        if (req.added() != null && !req.added().isEmpty()) {
            List<AccountRoleModel> rows = req.added().stream()
                    .map(id -> new AccountRoleModel(id, role)).toList();
            accountRoleRepo.saveAll(rows);
        }
    }
}
