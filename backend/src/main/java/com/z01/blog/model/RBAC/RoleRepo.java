package com.z01.blog.model.RBAC;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.z01.blog.annotation.AccessMethod;

public interface RoleRepo extends JpaRepository<RoleModel, Long> {
        @AccessMethod
        @Override
        Optional<RoleModel> findById(Long id);

        Optional<RoleModel> findByName(String name);

        boolean existsByName(String name);

        List<RoleModel> findAllByOrderByPositionAsc();

        @Query("""
                        SELECT r.position FROM RoleModel r
                        JOIN AccountRoleModel ar ON ar.id.roleId = r.id
                        WHERE ar.id.accountId = :userId
                        ORDER BY position LIMIT 1
                        """)
        int findUserHighestPosition(long userId);

        @Query("""
                        SELECT DISTINCT p.scope FROM RoleModel r
                        JOIN r.permissions p
                        JOIN AccountRoleModel ar ON ar.id.roleId = r.id
                        WHERE ar.id.accountId = :accountId""")
        List<String> findScopeByAccountId(long accountId);
}