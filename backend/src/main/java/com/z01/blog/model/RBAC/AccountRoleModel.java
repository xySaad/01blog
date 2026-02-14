package com.z01.blog.model.RBAC;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "account_roles")
public class AccountRoleModel {
    @Embeddable
    static public class ID {
        public long accountId;
        public long roleId;
    }

    @EmbeddedId
    private ID id;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private RoleModel role;

    public AccountRoleModel() {
    }

    public AccountRoleModel(long accountId, RoleModel role) {
        this.id = new ID();
        this.id.accountId = accountId;
        this.role = role;
    }

    public interface repo extends JpaRepository<AccountRoleModel, ID> {
        public boolean existsById_AccountIdAndRole_Permissions_Scope(long userId, String scope);
    }
}