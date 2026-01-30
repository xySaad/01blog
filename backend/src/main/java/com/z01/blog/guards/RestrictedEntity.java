package com.z01.blog.guards;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@MappedSuperclass
public abstract class RestrictedEntity {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    public long id;
    @JsonSerialize(using = ToStringSerializer.class)
    public long account;
    public boolean deleted;

    public RestrictedEntity ensureAccess(long userId, boolean publicAccess) {
        if (deleted)
            throw new ResponseStatusException(HttpStatus.GONE);

        if (publicAccess)
            return this;

        // userId > 0 in case both userId and account are default values somehow.
        if (userId > 0 && userId == account)
            return this;

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
}
