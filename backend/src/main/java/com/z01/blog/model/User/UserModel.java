package com.z01.blog.model.User;

import com.z01.blog.model.Audit.Deleteable;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public class UserModel implements Deleteable {
    @Id
    public long accountId;

    public String login;
    public String firstName;
    public String lastName;
    public boolean banned;
    public boolean deleted;

    @Transient
    public boolean followed;

    @Override
    public void delete() {
        this.deleted = true;
    }
}