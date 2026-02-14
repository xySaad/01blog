package com.z01.blog.api.v1.moderation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.model.RBAC.RoleModel;

@RestController
@RequestMapping("/api/v1/moderation/roles")
public class RoleController {
    @Autowired
    RoleModel.repo repo;

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
}
