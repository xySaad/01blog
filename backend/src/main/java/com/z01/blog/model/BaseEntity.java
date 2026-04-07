package com.z01.blog.model;

import com.z01.blog.annotation.EntityAccess.Mode;
import com.z01.blog.exception.AppError;
import com.z01.blog.infrastructure.RestrictedEntity;
import com.z01.blog.model.Audit.Deleteable;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity implements RestrictedEntity<Long>, Deleteable {
    @Id
    public long id;
    public long account;
    public boolean deleted;

    @Override
    public void ensureAccess(Long userId, Mode accessMode) {
        if (accessMode == Mode.Read)
            return;

        if (userId != null && userId == account)
            return;

        throw AppError.ACCESS_DENIED.asException();
    }

    @Override
    public void delete() {
        this.deleted = true;
    }
}
