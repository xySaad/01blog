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

        var role = roleRepo.findByName("root").orElse(new RoleModel());
        role.name = "root";
        role.permissions = new ArrayList<>(scannedPermissions.values());
        roleRepo.save(role);
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