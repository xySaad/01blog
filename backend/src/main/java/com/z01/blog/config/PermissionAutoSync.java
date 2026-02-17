package com.z01.blog.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.z01.blog.annotation.RequiresPermission;
import com.z01.blog.model.RBAC.PermissionModel;
import com.z01.blog.model.RBAC.RoleModel;

@Component
public class PermissionAutoSync implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private PermissionModel.repo permissionRepo;

    @Autowired
    private RoleModel.repo roleRepo;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        var scannedPermissions = new HashMap<Long, PermissionModel>();
        handlerMapping.getHandlerMethods().forEach((mapping, handlerMethod) -> {
            RequiresPermission annotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);
            if (annotation != null) {
                var perm = syncToDatabase(annotation.scope(), annotation.description());
                scannedPermissions.put(perm.id, perm);
            }
        });

        // create default role with no permissions
        if (!roleRepo.existsByName("default")) {
            var defaultRole = new RoleModel();
            defaultRole.name = "default";
            defaultRole.description = "default role - should be assigned to all users";
            roleRepo.save(defaultRole);
        }

        // create root role with no permissions
        var rootRole = roleRepo.findByName("root").orElse(new RoleModel());
        rootRole.name = "root";
        rootRole.permissions = new ArrayList<>(scannedPermissions.values());
        roleRepo.save(rootRole);
    }

    private PermissionModel syncToDatabase(String scope, String description) {
        var existingPerm = permissionRepo.findByScope(scope);
        if (existingPerm == null) {
            var perm = new PermissionModel();
            perm.scope = scope;
            perm.description = description;
            return permissionRepo.save(perm);
        }

        if (existingPerm.description == description || description.isEmpty())
            return existingPerm;

        existingPerm.description = description;
        return permissionRepo.save(existingPerm);

    }
}