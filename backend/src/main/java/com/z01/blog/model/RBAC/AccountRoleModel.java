package com.z01.blog.model.RBAC;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.z01.blog.model.User.UserEntity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

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

        public boolean existsById_AccountIdAndRole(long userId, RoleModel role);

        @Query("""
                SELECT user
                FROM AccountRoleModel accRole
                JOIN UserEntity user ON user.accountId = accRole.id.accountId
                WHERE accRole.id.roleId = :roleId""")
        List<UserEntity> findUserLoginsByRoleId(long roleId);

        @Modifying
        @Transactional
        @Query("DELETE FROM AccountRoleModel a WHERE a.id.roleId = :roleId AND a.id.accountId IN :accountIds")
        void deleteByRoleIdAndAccountIds(long roleId, List<Long> accountIds);
    }
}