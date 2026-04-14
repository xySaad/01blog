package com.z01.blog.model.RBAC;

import java.util.List;

import com.z01.blog.annotation.EntityAccess.Mode;
import com.z01.blog.exception.AppError;
import com.z01.blog.infrastructure.RestrictedEntity;
import com.z01.blog.model.RepoRegistry;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class RoleModel implements RestrictedEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;
    public String name;
    public String description;
    public Integer position;

    @ManyToMany
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    public List<PermissionModel> permissions;

    // TODO: provide User with a list Roles in PrincipleProvider
    // and remove usage of RepoRegistry.rolesRepo
    @Override
    public void ensureAccess(Long user, Mode mode) {
        int highestPosition = RepoRegistry.rolesRepo.findUserHighestPosition(user);
        if (this.position <= highestPosition)
            throw AppError.ACCESS_DENIED.asException();

        if (mode == Mode.Write)
            if (this.position == 0 || this.position == Integer.MAX_VALUE)
                throw AppError.ACCESS_DENIED.asException();
    }
}