package com.z01.blog.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.z01.blog.annotation.EntityAccess;
import com.z01.blog.infrastructure.RestrictedEntity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@MappedSuperclass
public class BaseEntity implements RestrictedEntity<Long> {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    public long id;
    @JsonSerialize(using = ToStringSerializer.class)
    public long account;
    public boolean deleted;

    @Override
    public void ensureAccess(Long userId, EntityAccess.Mode accessMode) {
        if (deleted)
            throw new ResponseStatusException(HttpStatus.GONE);

        if (accessMode == EntityAccess.Mode.Read)
            return;

        if (userId != null && userId == account)
            return;

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
